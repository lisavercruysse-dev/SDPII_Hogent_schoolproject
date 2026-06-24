import { IsString, IsInt, IsOptional, IsBoolean } from 'class-validator';

export class CreateAfwezigheidDto {
  @IsString()
  startDate: string;

  @IsString()
  endDate: string;

  @IsInt()
  days: number;

  @IsString()
  type: string;

  @IsString()
  reason: string;

  @IsOptional()
  @IsString()
  status?: string;

  @IsOptional()
  @IsBoolean()
  canCancel?: boolean;

  @IsOptional()
  @IsInt()
  werknemerId: number;
}
