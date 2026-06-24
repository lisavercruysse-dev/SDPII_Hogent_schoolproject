import { TaakTemplateResponseDTO } from 'src/taakTemplates/TaakTemplateDTO';
import { ApiProperty } from '@nestjs/swagger';
import { IsDate, IsNumber, IsString, Min } from 'class-validator';
import { Type } from 'class-transformer';

export class TaakResponseDTO {
  @ApiProperty({ example: 1, description: 'ID van een taak' })
  id: number;

  @ApiProperty({
    example: 1,
    description: 'Werknemer aan wie taak toegewezen werd',
  })
  werknemerId: number;

  @ApiProperty({
    description: 'Taak template dat gebruikt werd om taak aan te maken',
    type: TaakResponseDTO,
  })
  taakTemplate: TaakTemplateResponseDTO;

  @ApiProperty({ description: 'Datum van de taak' })
  datum: Date;

  @ApiProperty({ example: 'afgewerkt', description: 'Status van de taak' })
  status: string;

  @ApiProperty({
    example: '30',
    description: 'Tijd die werknemer nodig had om af te werken',
  })
  tijdGespendeerd: number | null;

  @ApiProperty({
    example: 'Statuscheck machine KL-528 en problemen oplossen indien nodig.',
    description: 'Extra info over de taak',
  })
  specificaties: string;
}

export class TakenListResponseDTO {
  @ApiProperty({ type: () => [TaakResponseDTO] })
  items: TaakResponseDTO[];
}

export class TaakDetailsResponseDTO extends TaakResponseDTO {
  @ApiProperty({
    description: 'Machine waarvoor taak aangemaakt werd',
  })
  machine: {
    machineId: number;
    machineName: string;
    siteName: string;
    locationOnSite: string;
  };
}

export class TaakUpdateStatusRequestDTO {
  @IsString()
  status: string;

  @IsNumber()
  tijdGespendeerd: number;
}

export class CreateTaakRequestDTO {
  @IsNumber()
  @Min(1)
  taakTemplateId: number;

  @IsNumber()
  @Min(1)
  werknemerId: number;

  @IsNumber()
  @Min(1)
  machineId: number;

  @IsDate()
  @Type(() => Date)
  datum: Date;

  @IsString()
  specificaties: string;
}

export class TaakUpdateRequestDTO extends CreateTaakRequestDTO {}
