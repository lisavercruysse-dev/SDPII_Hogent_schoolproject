import { ApiProperty } from '@nestjs/swagger';
import { Expose } from 'class-transformer';
import {
  IsInt,
  IsNotEmpty,
  IsPositive,
  IsString,
  MaxLength,
} from 'class-validator';

export class WerknemerResponseDTO {
  @ApiProperty({ example: 1, description: 'ID van een werknemer' })
  @Expose()
  @IsInt()
  @IsPositive()
  id: number;

  @ApiProperty({ example: 'Jonas', description: 'Voornaam werknemer' })
  @Expose()
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  firstName: string;

  @ApiProperty({ example: 'VanAert', description: 'Achternaam werknemer' })
  @Expose()
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  lastName: string;

  @ApiProperty({
    example: 'werknemer',
    description: 'Jobfunctie van een werknemer',
  })
  @Expose()
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  jobTitel: string;
}

export class BasicWerknemerDTO {
  @ApiProperty({ example: 1, description: 'ID van een werknemer' })
  @Expose()
  @IsInt()
  @IsPositive()
  id: number;

  @ApiProperty({ example: 'Jonas', description: 'Voornaam werknemer' })
  @Expose()
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  firstName: string;

  @ApiProperty({ example: 'VanAert', description: 'Achternaam werknemer' })
  @Expose()
  @IsString()
  @IsNotEmpty()
  @MaxLength(255)
  lastName: string;
}

export class WerknemerDetailResponseDTO extends WerknemerResponseDTO {
  @ApiProperty({
    example: [1, 2],
    description: 'De teams waartoe een werknemer behoort',
  })
  @Expose()
  @IsInt({ each: true })
  teams: number[];
}
export class WerknemerDetailListResponseDTO {
  items: WerknemerDetailResponseDTO[];
}
