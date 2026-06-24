import { IsString, IsBoolean, IsInt, IsOptional, IsIn } from 'class-validator';

export const NotificatieTypes = [
  'ALGEMEEN',
  'TAAK_TOEGEWEZEN',
  'TAAK_GEWIJZIGD',
  'TAAK_VERWIJDERD',
  'ZIEKMELDING',
  'VAKANTIEAANVRAAG',
  'AFWEZIGHEID_GEANNULEERD',
] as const;

export type NotificatieType = (typeof NotificatieTypes)[number];

export class CreateNotificatieDto {
  @IsString()
  title: string;

  @IsString()
  description: string;

  @IsString()
  time: string;

  @IsOptional()
  @IsBoolean()
  isRead?: boolean;

  @IsOptional()
  @IsString()
  @IsIn(NotificatieTypes)
  type?: NotificatieType;

  @IsInt()
  werknemerId: number;
}

export class NotificatieResponseDto {
  id: number;
  title: string;
  description: string;
  time: string;
  isRead: boolean;
  type: string;
  werknemerId: number;
}
