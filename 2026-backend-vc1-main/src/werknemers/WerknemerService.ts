import { Injectable, NotFoundException } from '@nestjs/common';
import {
  WerknemerDetailListResponseDTO,
  WerknemerDetailResponseDTO,
  WerknemerResponseDTO,
} from './WerknemerDTO';
import { werknemers, team_werknemers } from 'src/drizzle/schema';
import { eq } from 'drizzle-orm';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';
import { plainToInstance } from 'class-transformer';

@Injectable()
export class WerknemerService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
  ) {}

  async getById(id: number): Promise<WerknemerResponseDTO> {
    const werknemer = await this.db.query.werknemers.findFirst({
      where: eq(werknemers.id, id),
    });

    if (!werknemer) {
      throw new NotFoundException('Er bestaat geen werknemer met dit id.');
    }

    return plainToInstance(WerknemerResponseDTO, werknemer, {
      excludeExtraneousValues: true,
    });
  }

  async getAll(): Promise<WerknemerDetailListResponseDTO> {
    const items = await this.db.query.werknemers.findMany();
    if (items.length === 0) {
      throw new NotFoundException('Er zijn geen werknemers');
    }
    const teamLinks = await this.db
      .select({
        werknemerId: team_werknemers.werknemerId,
        teamId: team_werknemers.teamId,
      })
      .from(team_werknemers);
    const werknemerTeamsMap = teamLinks.reduce(
      (acc, link) => {
        if (!acc[link.werknemerId]) acc[link.werknemerId] = [];
        acc[link.werknemerId].push(link.teamId);
        return acc;
      },
      {} as Record<number, number[]>,
    );
    const dtoItems = items.map((item) =>
      plainToInstance(
        WerknemerDetailResponseDTO,
        {
          ...item,
          teams: werknemerTeamsMap[item.id] || [],
        },
        { excludeExtraneousValues: true },
      ),
    );

    return { items: dtoItems };
  }
}
