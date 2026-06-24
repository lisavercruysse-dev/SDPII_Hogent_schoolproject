import { ApiProperty } from '@nestjs/swagger';

export class TaakTemplateResponseDTO {
  @ApiProperty({ example: 1, description: 'ID van een taak template' })
  id: number;

  @ApiProperty({ example: 'Onderhoud', description: 'Type taak' })
  type: string;

  @ApiProperty({
    example: 'Onderhouden machine',
    description: 'Heel korte omschrijving van taak inhoud',
  })
  omschrijving: string;

  @ApiProperty({
    example: '60',
    description: 'Inschatting hoe lang taak zal duren',
  })
  duurTijd: number;
}

export class TaakTemplateResponseListDTO {
  @ApiProperty({ type: () => [TaakTemplateResponseDTO] })
  items: TaakTemplateResponseDTO[];
}
