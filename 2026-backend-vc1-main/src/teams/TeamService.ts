import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';
import { Injectable, NotFoundException } from '@nestjs/common';
import { TeamListResponseDTO, TeamResponseDTO } from './TeamDTO';
import { team_werknemers, teams, werknemers } from 'src/drizzle/schema';
import { eq } from 'drizzle-orm';

@Injectable()
export class TeamService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
  ) {}

  async getAll(): Promise<TeamListResponseDTO> {
    const rows = await this.db
      .select({
        id: teams.id,
        name: teams.name,
        siteId: teams.siteId,
        werknemerId: werknemers.id,
        werknemerFirstName: werknemers.firstName,
        werknemerLastName: werknemers.lastName,
      })
      .from(teams)
      .innerJoin(team_werknemers, eq(team_werknemers.teamId, teams.id))
      .innerJoin(werknemers, eq(werknemers.id, team_werknemers.werknemerId));

    if (rows.length === 0) {
      throw new NotFoundException('Er bestaan geen teams');
    }

    return { items: this.mapToDTO(rows) };
  }

  async getMyTeams(supervisorId: number): Promise<TeamListResponseDTO> {
    const rows = await this.db
      .select({
        id: teams.id,
        name: teams.name,
        siteId: teams.siteId,
        werknemerId: werknemers.id,
        werknemerFirstName: werknemers.firstName,
        werknemerLastName: werknemers.lastName,
      })
      .from(teams)
      .innerJoin(team_werknemers, eq(team_werknemers.teamId, teams.id))
      .innerJoin(werknemers, eq(werknemers.id, team_werknemers.werknemerId))
      .where(eq(teams.supervisorId, supervisorId));

    if (rows.length === 0) {
      throw new NotFoundException(
        'Geen teams gevonden voor deze verantwoordelijke',
      );
    }

    return { items: this.mapToDTO(rows) };
  }

  private mapToDTO(
    rows: {
      id: number;
      name: string;
      siteId: number;
      werknemerId: number;
      werknemerFirstName: string;
      werknemerLastName: string;
    }[],
  ): TeamResponseDTO[] {
    const teamMap = new Map<number, TeamResponseDTO>();

    for (const row of rows) {
      if (!teamMap.has(row.id)) {
        teamMap.set(row.id, {
          id: row.id,
          name: row.name,
          siteId: row.siteId,
          werknemers: [],
        });
      }
      teamMap.get(row.id)!.werknemers.push({
        id: row.werknemerId,
        firstName: row.werknemerFirstName,
        lastName: row.werknemerLastName,
      });
    }

    return Array.from(teamMap.values());
  }
}
