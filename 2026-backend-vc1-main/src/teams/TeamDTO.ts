import { ApiProperty } from '@nestjs/swagger';
import { BasicWerknemerDTO } from 'src/werknemers/WerknemerDTO';

export class TeamResponseDTO {
  @ApiProperty({ example: 1, description: 'Id van een team' })
  id: number;

  @ApiProperty({ example: 'Team A', description: 'Naam van het team' })
  name: string;

  @ApiProperty({ example: 1, description: 'Id van een site' })
  siteId: number;

  @ApiProperty({ description: 'Alle werknemers die tot dit team behoren' })
  werknemers: BasicWerknemerDTO[];
}

export class TeamListResponseDTO {
  items: TeamResponseDTO[];
}
