import { ApiProperty } from '@nestjs/swagger';
import { Expose } from 'class-transformer';
import { IsBoolean, IsInt, IsNumber, IsString } from 'class-validator';

export class SiteResponseDTO {
  @ApiProperty()
  @Expose()
  @IsInt()
  id: number;

  @ApiProperty()
  @Expose()
  @IsString()
  name: string;

  @ApiProperty()
  @Expose()
  @IsString()
  locatie: string;

  @ApiProperty()
  @Expose()
  @IsString()
  land: string;

  @ApiProperty()
  @Expose()
  @IsString()
  operationeleStatus: string;

  @ApiProperty()
  @Expose()
  @IsString()
  siteProductieStatus: string;

  @ApiProperty()
  @Expose()
  @IsInt()
  capaciteit: number;

  @ApiProperty()
  @Expose()
  @IsNumber()
  breedtegraad: number;

  @ApiProperty()
  @Expose()
  @IsNumber()
  lengtegraad: number;

  @ApiProperty()
  @Expose()
  @IsBoolean()
  isDeleted: boolean;

  @ApiProperty()
  @Expose()
  @IsInt()
  werknemerCount: number;

  @ApiProperty()
  @Expose()
  @IsInt()
  availableWorkers: number;
}

export class SitesListResponseDTO {
  @ApiProperty({ type: [SiteResponseDTO] })
  items: SiteResponseDTO[];
}

export class SiteDetailResponseDTO extends SiteResponseDTO {
  @ApiProperty()
  @Expose()
  @IsString()
  verantwoordelijke: string;

  @ApiProperty()
  @Expose()
  @IsInt()
  geplandVandaag: number;

  @ApiProperty()
  @Expose()
  @IsInt()
  afgewerktVandaag: number;

  @ApiProperty()
  @Expose()
  gemiddeldeVoltooiingstijd: number | null;
}

export class SiteTaskStatsDTO {
  @ApiProperty() @Expose() @IsInt() openTaken: number;
  @ApiProperty() @Expose() @IsInt() toegewezenTaken: number;
  @ApiProperty() @Expose() @IsInt() totaal: number;
  @ApiProperty() @Expose() @IsNumber() percentageOpen: number;
  @ApiProperty() @Expose() @IsNumber() percentageToegewezen: number;
  @ApiProperty() @Expose() ratio: number | null;
  @ApiProperty() @Expose() @IsInt() aantalAfgewerkteTaken: number;
  @ApiProperty() @Expose() gemiddeldeVoltooiingstijd: number | null;
  @ApiProperty() @Expose() @IsInt() afgewerktVandaag: number;
  @ApiProperty() @Expose() @IsInt() geplandVandaag: number;
}
