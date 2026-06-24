import { ApiProperty } from '@nestjs/swagger';

export class MachineResponseDTO {
  @ApiProperty({ example: 1, description: 'ID van een machine' })
  id: number;

  @ApiProperty({ example: 1, description: 'Werknemer aan wie toegewezen' })
  werknemerId?: number;

  @ApiProperty({ example: 'MC-PLT-001', description: 'Naam van een machine' })
  name: string;

  @ApiProperty({ example: 'DRAAIT', description: 'Status van de machine' })
  status: string;

  @ApiProperty({
    example: 'GEZOND',
    description: 'Productiestatus van een machine',
  })
  productieStatus: string;

  @ApiProperty({
    example: 'Spijkers type A',
    description: 'Productinfo over wat de machine produceert',
  })
  productinfo?: string | null;

  @ApiProperty({ example: 800, description: 'Uptime in aantal minuten' })
  upTime: number;

  @ApiProperty({ description: 'Datum sinds laatste onderhoud' })
  datumLaatsteOnderhoud?: Date | null;

  @ApiProperty({ example: false, description: 'Is de machine soft-deleted?' })
  isDeleted: boolean;

  @ApiProperty({ description: 'Locatie van machine op site' })
  location: {
    siteId: number;
    siteName: string;
    locationOnSite: string;
  };
}
