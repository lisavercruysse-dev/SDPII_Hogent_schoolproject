import { Injectable, NotFoundException } from '@nestjs/common';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';
import {
  machines,
  site_machines,
  sites,
  taakTemplates,
  taken,
  team_werknemers,
  teams,
  werknemers,
} from 'src/drizzle/schema';
import {
  TaakDetailsResponseDTO,
  TaakResponseDTO,
  TakenListResponseDTO,
  TaakUpdateStatusRequestDTO,
  TaakUpdateRequestDTO,
  CreateTaakRequestDTO,
} from './TaakDTO';
import { eq, getTableColumns, and, lt } from 'drizzle-orm';
import { NotificatieService } from '../notificaties/NotificatieService';

@Injectable()
export class TaakService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
    private readonly notificatieService: NotificatieService,
  ) {}

  async getTakenWerknemer(werknemerId: number): Promise<TakenListResponseDTO> {
    const rows = await this.db
      .select({
        ...getTableColumns(taken),
        takenId: taken.id,
        werknemerId: taken.werknemerId,
        taakTemplateId: taakTemplates.id,
        taakTemplateType: taakTemplates.type,
        taakTemplateOmschrijving: taakTemplates.omschrijving,
        taakTemplateDuurTijd: taakTemplates.duurTijd,
      })
      .from(taken)
      .innerJoin(taakTemplates, eq(taakTemplates.id, taken.templateId))
      .where(eq(taken.werknemerId, werknemerId));

    if (rows.length === 0) {
      throw new NotFoundException('Deze werknemer heeft geen taken');
    }

    const items: TaakResponseDTO[] = rows.map((row) => ({
      id: row.takenId,
      datum: row.datum,
      status: row.status,
      tijdGespendeerd: row.tijdGespendeerd,
      specificaties: row.specificaties,
      werknemerId: row.werknemerId,
      taakTemplate: {
        id: row.taakTemplateId,
        type: row.taakTemplateType,
        omschrijving: row.taakTemplateOmschrijving,
        duurTijd: row.taakTemplateDuurTijd,
      },
    }));

    return { items };
  }

  async getTakenSupervisor(
    supervisorId: number,
  ): Promise<TakenListResponseDTO> {
    const rows = await this.db
      .select({
        ...getTableColumns(taken),
        takenId: taken.id,
        werknemerId: taken.werknemerId,
        taakTemplateId: taakTemplates.id,
        taakTemplateType: taakTemplates.type,
        taakTemplateOmschrijving: taakTemplates.omschrijving,
        taakTemplateDuurTijd: taakTemplates.duurTijd,
      })
      .from(taken)
      .innerJoin(taakTemplates, eq(taakTemplates.id, taken.templateId))
      .innerJoin(werknemers, eq(werknemers.id, taken.werknemerId))
      .innerJoin(
        team_werknemers,
        eq(team_werknemers.werknemerId, werknemers.id),
      )
      .innerJoin(teams, eq(teams.id, team_werknemers.teamId))
      .where(eq(teams.supervisorId, supervisorId));

    if (rows.length === 0) {
      throw new NotFoundException('Geen taken gevonden voor dit team.');
    }

    const items: TaakResponseDTO[] = rows.map((row) => ({
      id: row.takenId,
      datum: row.datum,
      status: row.status,
      tijdGespendeerd: row.tijdGespendeerd,
      specificaties: row.specificaties,
      werknemerId: row.werknemerId,
      taakTemplate: {
        id: row.taakTemplateId,
        type: row.taakTemplateType,
        omschrijving: row.taakTemplateOmschrijving,
        duurTijd: row.taakTemplateDuurTijd,
      },
    }));

    return { items };
  }

  async getTaakDetails(taakId: number): Promise<TaakDetailsResponseDTO> {
    const rows = await this.db
      .select({
        ...getTableColumns(taken),
        takenId: taken.id,
        taakTemplateId: taakTemplates.id,
        taakTemplateType: taakTemplates.type,
        taakTemplateOmschrijving: taakTemplates.omschrijving,
        taakTemplateDuurTijd: taakTemplates.duurTijd,
        machineId: machines.id,
        machineName: machines.name,
        locationOnSite: site_machines.location,
        siteName: sites.name,
      })
      .from(taken)
      .innerJoin(taakTemplates, eq(taakTemplates.id, taken.templateId))
      .innerJoin(machines, eq(machines.id, taken.machineId))
      .innerJoin(site_machines, eq(site_machines.machineId, machines.id))
      .innerJoin(sites, eq(sites.id, site_machines.siteId))
      .where(eq(taken.id, taakId));

    const row = rows[0];

    if (!row) {
      throw new NotFoundException('No task with this id exists');
    }

    const item: TaakDetailsResponseDTO = {
      id: row.takenId,
      datum: row.datum,
      status: row.status,
      tijdGespendeerd: row.tijdGespendeerd,
      specificaties: row.specificaties,
      werknemerId: row.werknemerId,
      taakTemplate: {
        id: row.taakTemplateId,
        type: row.taakTemplateType,
        omschrijving: row.taakTemplateOmschrijving,
        duurTijd: row.taakTemplateDuurTijd,
      },
      machine: {
        machineId: row.machineId,
        machineName: row.machineName,
        siteName: row.siteName,
        locationOnSite: row.locationOnSite,
      },
    };

    return item;
  }

  async updateStatusById(
    taakId: number,
    changes: TaakUpdateStatusRequestDTO,
  ): Promise<void> {
    const existingTask = await this.db.query.taken.findFirst({
      where: eq(taken.id, taakId),
    });

    if (!existingTask) {
      throw new NotFoundException('No task with this ID exists');
    }

    await this.db.update(taken).set(changes).where(eq(taken.id, taakId));

    await this.notificatieService.sendNotification(
      existingTask.werknemerId,
      'Taakstatus gewijzigd',
      `De status van uw taak is gewijzigd naar ${changes.status}.`,
      'TAAK_GEWIJZIGD',
    );
  }

  async updateById(
    taakId: number,
    changes: TaakUpdateRequestDTO,
  ): Promise<void> {
    const existingTask = await this.db.query.taken.findFirst({
      where: eq(taken.id, taakId),
    });

    if (!existingTask) {
      throw new NotFoundException('No task with this ID exists');
    }

    await this.db.update(taken).set(changes).where(eq(taken.id, taakId));

    await this.notificatieService.sendNotification(
      existingTask.werknemerId,
      'Taak gewijzigd',
      'Een van uw toegewezen taken is gewijzigd.',
      'TAAK_GEWIJZIGD',
    );
  }

  async deleteById(taakId: number): Promise<void> {
    const existingTask = await this.db.query.taken.findFirst({
      where: eq(taken.id, taakId),
    });

    if (!existingTask) {
      throw new NotFoundException('No task with this ID exists');
    }

    await this.notificatieService.sendNotification(
      existingTask.werknemerId,
      'Taak verwijderd',
      'Een aan u toegewezen taak is verwijderd uit de planning.',
      'TAAK_VERWIJDERD',
    );

    const [result] = await this.db.delete(taken).where(eq(taken.id, taakId));

    if (result.affectedRows === 0) {
      throw new NotFoundException('No task with this ID exists');
    }
  }

  async create(taak: CreateTaakRequestDTO): Promise<TaakDetailsResponseDTO> {
    const [newTask] = await this.db
      .insert(taken)
      .values({
        werknemerId: taak.werknemerId,
        templateId: taak.taakTemplateId,
        machineId: taak.machineId,
        datum: taak.datum,
        status: 'Gepland',
        specificaties: taak.specificaties,
      })
      .$returningId();

    await this.notificatieService.sendNotification(
      taak.werknemerId,
      'Nieuwe taak toegewezen',
      'Er is een nieuwe taak aan u toegewezen.',
      'TAAK_TOEGEWEZEN',
    );

    return this.getTaakDetails(newTask.id);
  }

  async getOnafgewerkt(): Promise<TakenListResponseDTO> {
    const now = new Date();
    now.setHours(0, 0, 0, 0);

    const rows = await this.db
      .select({
        ...getTableColumns(taken),
        takenId: taken.id,
        werknemerId: taken.werknemerId,
        taakTemplateId: taakTemplates.id,
        taakTemplateType: taakTemplates.type,
        taakTemplateOmschrijving: taakTemplates.omschrijving,
        taakTemplateDuurTijd: taakTemplates.duurTijd,
      })
      .from(taken)
      .innerJoin(taakTemplates, eq(taakTemplates.id, taken.templateId))
      .where(and(eq(taken.status, 'gepland'), lt(taken.datum, now)));

    if (rows.length === 0) {
      throw new NotFoundException('Er zijn geen onafgeronde taken');
    }

    const items: TaakResponseDTO[] = rows.map((row) => ({
      id: row.takenId,
      datum: row.datum,
      status: row.status,
      tijdGespendeerd: row.tijdGespendeerd,
      specificaties: row.specificaties,
      werknemerId: row.werknemerId,
      taakTemplate: {
        id: row.taakTemplateId,
        type: row.taakTemplateType,
        omschrijving: row.taakTemplateOmschrijving,
        duurTijd: row.taakTemplateDuurTijd,
      },
    }));

    return { items };
  }

  async getAll(): Promise<TakenListResponseDTO> {
    const rows = await this.db
      .select({
        ...getTableColumns(taken),
        takenId: taken.id,
        werknemerId: taken.werknemerId,
        taakTemplateId: taakTemplates.id,
        taakTemplateType: taakTemplates.type,
        taakTemplateOmschrijving: taakTemplates.omschrijving,
        taakTemplateDuurTijd: taakTemplates.duurTijd,
      })
      .from(taken)
      .innerJoin(taakTemplates, eq(taakTemplates.id, taken.templateId));

    if (rows.length === 0) {
      throw new NotFoundException('Geen taken gevonden');
    }

    const items: TaakResponseDTO[] = rows.map((row) => ({
      id: row.takenId,
      datum: row.datum,
      status: row.status,
      tijdGespendeerd: row.tijdGespendeerd,
      specificaties: row.specificaties,
      werknemerId: row.werknemerId,
      taakTemplate: {
        id: row.taakTemplateId,
        type: row.taakTemplateType,
        omschrijving: row.taakTemplateOmschrijving,
        duurTijd: row.taakTemplateDuurTijd,
      },
    }));

    return { items };
  }
}
