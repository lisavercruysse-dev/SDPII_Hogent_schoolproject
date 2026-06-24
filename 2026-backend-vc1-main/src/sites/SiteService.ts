import { Injectable } from '@nestjs/common';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';
import { plainToInstance } from 'class-transformer';
import {
  SiteResponseDTO,
  SitesListResponseDTO,
  SiteDetailResponseDTO,
  SiteTaskStatsDTO,
} from './SiteDTO';
import { MachineResponseDTO } from '../machines/MachineDTO';

import {
  eq,
  and,
  gte,
  lte,
  or,
  sql,
  getTableColumns,
  inArray,
} from 'drizzle-orm';
import {
  sites,
  teams,
  team_werknemers,
  werknemers,
  afwezigheden,
  machines,
} from 'src/drizzle/schema';
import { taken, site_machines } from 'src/drizzle/schema';
import { NotFoundException } from '@nestjs/common';

@Injectable()
export class SiteService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
  ) {}

  async getAll(): Promise<SitesListResponseDTO> {
    const today = new Date().toISOString().split('T')[0];

    const result = await this.db
      .select({
        ...getTableColumns(sites),
        werknemerCount:
          sql<number>`COUNT(DISTINCT ${team_werknemers.werknemerId})`.as(
            'werknemerCount',
          ),
      })
      .from(sites)
      .leftJoin(teams, eq(teams.siteId, sites.id))
      .leftJoin(team_werknemers, eq(team_werknemers.teamId, teams.id))
      .leftJoin(
        werknemers,
        and(
          eq(werknemers.id, team_werknemers.werknemerId),
          eq(werknemers.jobTitel, 'WERKNEMER'),
        ),
      )
      .where(eq(sites.isDeleted, false))
      .groupBy(sites.id);

    const absentResult = await this.db
      .select({
        siteId: teams.siteId,
        absentCount:
          sql<number>`COUNT(DISTINCT ${afwezigheden.werknemerId})`.as(
            'absentCount',
          ),
      })
      .from(afwezigheden)
      .innerJoin(
        team_werknemers,
        eq(team_werknemers.werknemerId, afwezigheden.werknemerId),
      )
      .innerJoin(teams, eq(teams.id, team_werknemers.teamId))
      .where(
        and(
          lte(afwezigheden.startDate, today),
          gte(afwezigheden.endDate, today),
          or(
            eq(afwezigheden.status, 'Goedgekeurd'),
            eq(afwezigheden.status, 'In behandeling'),
          ),
        ),
      )
      .groupBy(teams.siteId);

    const absentMap = new Map(
      absentResult.map((r) => [r.siteId, r.absentCount]),
    );

    const sitesWithStats = result.map((site) => ({
      ...site,
      availableWorkers: site.werknemerCount - (absentMap.get(site.id) ?? 0),
    }));

    return {
      items: plainToInstance(SiteResponseDTO, sitesWithStats, {
        excludeExtraneousValues: true,
      }),
    };
  }

  async getMySite(userId: number) {
    const team = await this.db.query.teams.findFirst({
      where: eq(teams.supervisorId, userId),
    });

    if (!team)
      throw new NotFoundException('Geen site gekoppeld aan dit account.');

    const result = await this.db
      .select({
        ...getTableColumns(sites),
        werknemerCount:
          sql<number>`COUNT(DISTINCT ${team_werknemers.werknemerId})`.as(
            'werknemerCount',
          ),
      })
      .from(sites)
      .leftJoin(teams, eq(teams.siteId, sites.id))
      .leftJoin(team_werknemers, eq(team_werknemers.teamId, teams.id))
      .leftJoin(
        werknemers,
        and(
          eq(werknemers.id, team_werknemers.werknemerId),
          eq(werknemers.jobTitel, 'WERKNEMER'),
        ),
      )
      .where(and(eq(sites.id, team.siteId), eq(sites.isDeleted, false)))
      .groupBy(sites.id)
      .limit(1);

    if (!result.length) throw new NotFoundException('Site niet gevonden.');

    const today = new Date().toISOString().split('T')[0];

    const absentResult = await this.db
      .select({
        absentCount:
          sql<number>`COUNT(DISTINCT ${afwezigheden.werknemerId})`.as(
            'absentCount',
          ),
      })
      .from(afwezigheden)
      .innerJoin(
        team_werknemers,
        eq(team_werknemers.werknemerId, afwezigheden.werknemerId),
      )
      .innerJoin(teams, eq(teams.id, team_werknemers.teamId))
      .where(
        and(
          eq(teams.siteId, team.siteId),
          lte(afwezigheden.startDate, today),
          gte(afwezigheden.endDate, today),
          or(
            eq(afwezigheden.status, 'Goedgekeurd'),
            eq(afwezigheden.status, 'In behandeling'),
          ),
        ),
      );

    const site = result[0];
    const absentCount = absentResult[0]?.absentCount ?? 0;

    return plainToInstance(
      SiteResponseDTO,
      { ...site, availableWorkers: site.werknemerCount - absentCount },
      { excludeExtraneousValues: true },
    );
  }

  async getById(id: number): Promise<SiteDetailResponseDTO> {
    const site = await this.db.query.sites.findFirst({
      where: and(eq(sites.id, id), eq(sites.isDeleted, false)),
    });

    if (!site) throw new NotFoundException('Site niet gevonden.');

    const supervisors = await this.db
      .selectDistinct({
        naam: sql<string>`CONCAT(${werknemers.firstName}, ' ', ${werknemers.lastName})`.as(
          'naam',
        ),
      })
      .from(teams)
      .innerJoin(werknemers, eq(werknemers.id, teams.supervisorId))
      .where(eq(teams.siteId, id));

    const verantwoordelijke =
      supervisors.map((s) => s.naam).join(', ') ||
      'Geen verantwoordelijke toegewezen';

    const today = new Date();
    const todayStart = new Date(
      today.getFullYear(),
      today.getMonth(),
      today.getDate(),
    );
    const todayEnd = new Date(todayStart.getTime() + 24 * 60 * 60 * 1000);

    const taakData = await this.db
      .select({
        status: taken.status,
        tijdGespendeerd: taken.tijdGespendeerd,
        datum: taken.datum,
      })
      .from(taken)
      .innerJoin(site_machines, eq(site_machines.machineId, taken.machineId))
      .where(eq(site_machines.siteId, id));

    let geplandVandaag = 0;
    let afgewerktVandaag = 0;
    let totaalTijd = 0;
    let aantalAfgewerkt = 0;

    for (const taak of taakData) {
      const datum = new Date(taak.datum);
      const isVandaag = datum >= todayStart && datum < todayEnd;

      if (taak.status === 'gepland' && isVandaag) geplandVandaag++;

      if (taak.status === 'afgewerkt') {
        aantalAfgewerkt++;
        totaalTijd += taak.tijdGespendeerd ?? 0;
        if (isVandaag) afgewerktVandaag++;
      }
    }

    const gemiddeldeVoltooiingstijd =
      aantalAfgewerkt > 0 ? Math.round(totaalTijd / aantalAfgewerkt) : null;

    return plainToInstance(
      SiteDetailResponseDTO,
      {
        ...site,
        verantwoordelijke,
        geplandVandaag,
        afgewerktVandaag,
        gemiddeldeVoltooiingstijd,
      },
      { excludeExtraneousValues: true },
    );
  }

  async getMachinesBySiteId(
    id: number,
  ): Promise<{ items: MachineResponseDTO[] }> {
    const site = await this.db.query.sites.findFirst({
      where: and(eq(sites.id, id), eq(sites.isDeleted, false)),
    });

    if (!site) throw new NotFoundException('Site niet gevonden.');

    const result = await this.db
      .select({
        ...getTableColumns(machines),
        locationOnSite: site_machines.location,
        siteId: sites.id,
        siteName: sites.name,
      })
      .from(site_machines)
      .innerJoin(machines, eq(machines.id, site_machines.machineId))
      .innerJoin(sites, eq(sites.id, site_machines.siteId))
      .where(and(eq(site_machines.siteId, id), eq(machines.isDeleted, false)))
      .orderBy(site_machines.location, machines.name);

    const items = result.map((row) => ({
      ...row,
      location: {
        siteId: row.siteId,
        siteName: row.siteName,
        locationOnSite: row.locationOnSite,
      },
    }));

    return { items };
  }

  async getMachineStats(id: number) {
    const site = await this.db.query.sites.findFirst({
      where: and(eq(sites.id, id), eq(sites.isDeleted, false)),
    });
    if (!site) throw new NotFoundException('Site niet gevonden.');

    const result = await this.db
      .select({
        status: machines.status,
        count: sql<number>`COUNT(*)`.as('count'),
      })
      .from(site_machines)
      .innerJoin(machines, eq(machines.id, site_machines.machineId))
      .where(and(eq(site_machines.siteId, id), eq(machines.isDeleted, false)))
      .groupBy(machines.status);

    const stats = {
      total: 0,
      DRAAIT: 0,
      GESTOPT: 0,
      ONDERHOUD: 0,
      NOOD_AAN_ONDERHOUD: 0,
    };
    for (const row of result) {
      const key = row.status as keyof typeof stats;
      if (key in stats) stats[key] = Number(row.count);
      stats.total += Number(row.count);
    }
    return stats;
  }

  async getWorkerStats(id: number) {
    const site = await this.db.query.sites.findFirst({
      where: and(eq(sites.id, id), eq(sites.isDeleted, false)),
    });
    if (!site) throw new NotFoundException('Site niet gevonden.');

    const today = new Date().toISOString().split('T')[0];

    // Alle werknemers (jobTitel WERKNEMER) op deze site via teams
    const siteWorkers = await this.db
      .selectDistinct({ werknemerId: team_werknemers.werknemerId })
      .from(teams)
      .innerJoin(team_werknemers, eq(team_werknemers.teamId, teams.id))
      .innerJoin(
        werknemers,
        and(
          eq(werknemers.id, team_werknemers.werknemerId),
          eq(werknemers.jobTitel, 'WERKNEMER'),
        ),
      )
      .where(eq(teams.siteId, id));

    const werknemerIds = siteWorkers.map((w) => w.werknemerId);
    const werknemerCount = werknemerIds.length;

    if (werknemerCount === 0) {
      return {
        werknemerCount: 0,
        availableWorkers: 0,
        afwezigheden: 0,
        ziekteAfwezigheden: 0,
        vakantieAfwezigheden: 0,
      };
    }

    // Actieve afwezigheden vandaag voor deze werknemers
    const absentToday = await this.db
      .select({
        werknemerId: afwezigheden.werknemerId,
        type: afwezigheden.type,
      })
      .from(afwezigheden)
      .where(
        and(
          inArray(afwezigheden.werknemerId, werknemerIds),
          lte(afwezigheden.startDate, today),
          gte(afwezigheden.endDate, today),
          or(
            eq(afwezigheden.status, 'Goedgekeurd'),
            eq(afwezigheden.status, 'In behandeling'),
          ),
        ),
      );

    // Unieke werknemers per type (iemand telt maar één keer)
    const absentIds = new Set(absentToday.map((a) => a.werknemerId));
    const ziekteIds = new Set(
      absentToday.filter((a) => a.type === 'Ziekte').map((a) => a.werknemerId),
    );
    const vakantieIds = new Set(
      absentToday
        .filter((a) => a.type === 'Vakantie')
        .map((a) => a.werknemerId),
    );

    return {
      werknemerCount,
      availableWorkers: werknemerCount - absentIds.size,
      afwezigheden: absentIds.size,
      ziekteAfwezigheden: ziekteIds.size,
      vakantieAfwezigheden: vakantieIds.size,
    };
  }

  async getTaskStats(id: number): Promise<SiteTaskStatsDTO> {
    const site = await this.db.query.sites.findFirst({
      where: and(eq(sites.id, id), eq(sites.isDeleted, false)),
    });
    if (!site) throw new NotFoundException('Site niet gevonden.');

    const todayStr = new Date().toISOString().split('T')[0];

    const [row] = await this.db
      .select({
        openTaken: sql<number>`
      SUM(CASE WHEN ${taken.status} = 'gepland' THEN 1 ELSE 0 END)
    `.as('openTaken'),
        toegewezenTaken: sql<number>`
      SUM(CASE WHEN ${taken.status} != 'gepland' THEN 1 ELSE 0 END)
    `.as('toegewezenTaken'),
        aantalAfgewerkteTaken: sql<number>`
      SUM(CASE WHEN ${taken.status} = 'afgewerkt' THEN 1 ELSE 0 END)
    `.as('aantalAfgewerkteTaken'),
        totaalTijd: sql<number>`
      COALESCE(SUM(CASE WHEN ${taken.status} = 'afgewerkt' THEN ${taken.tijdGespendeerd} ELSE 0 END), 0)
    `.as('totaalTijd'),
        geplandVandaag: sql<number>`
      SUM(CASE WHEN ${taken.status} = 'gepland' AND DATE(${taken.datum}) = ${todayStr} THEN 1 ELSE 0 END)
    `.as('geplandVandaag'),
        afgewerktVandaag: sql<number>`
      SUM(CASE WHEN ${taken.status} = 'afgewerkt' AND DATE(${taken.datum}) = ${todayStr} THEN 1 ELSE 0 END)
    `.as('afgewerktVandaag'),
      })
      .from(taken)
      .innerJoin(site_machines, eq(site_machines.machineId, taken.machineId))
      .where(eq(site_machines.siteId, id));

    const openTaken = Number(row.openTaken);
    const toegewezenTaken = Number(row.toegewezenTaken);
    const aantalAfgewerkteTaken = Number(row.aantalAfgewerkteTaken);
    const totaalTijd = Number(row.totaalTijd);
    const totaal = openTaken + toegewezenTaken;

    return plainToInstance(
      SiteTaskStatsDTO,
      {
        openTaken,
        toegewezenTaken,
        totaal,
        percentageOpen: totaal ? (openTaken / totaal) * 100 : 0,
        percentageToegewezen: totaal ? (toegewezenTaken / totaal) * 100 : 0,
        ratio: toegewezenTaken ? openTaken / toegewezenTaken : null,
        aantalAfgewerkteTaken,
        gemiddeldeVoltooiingstijd:
          aantalAfgewerkteTaken > 0
            ? Math.round(totaalTijd / aantalAfgewerkteTaken)
            : null,
        geplandVandaag: Number(row.geplandVandaag),
        afgewerktVandaag: Number(row.afgewerktVandaag),
      },
      { excludeExtraneousValues: true },
    );
  }
}
