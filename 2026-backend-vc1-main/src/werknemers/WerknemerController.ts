import { CheckUserAccessGuard } from 'src/auth/guards/userAccessGuard';
import {
  WerknemerDetailListResponseDTO,
  WerknemerResponseDTO,
} from './WerknemerDTO';
import { WerknemerService } from './WerknemerService';
import { Controller, UseGuards } from '@nestjs/common';
import { Get, Param } from '@nestjs/common';
import { CurrentUser } from 'src/auth/decorators/currentUserDecorator';
import { type Session } from 'src/types/auth';
import { ParseUserIdPipe } from 'src/auth/pipes/parseUserIdPipe';
import { TakenListResponseDTO } from 'src/taken/TaakDTO';
import { TaakService } from 'src/taken/TaakService';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import { ApiResponse } from '@nestjs/swagger';
import { Roles } from 'src/auth/decorators/rolesDecorator';
import { Role } from 'src/auth/roles';

@ApiTags('werknemers')
@ApiBearerAuth()
@ApiResponse({
  status: 401,
  description: 'Unauthorized - je moet ingelogd zijn',
})
@Controller('werknemers')
export class WerknemerController {
  constructor(
    private readonly werknemerService: WerknemerService,
    private readonly taakService: TaakService,
  ) {}

  @Get(':id')
  @UseGuards(CheckUserAccessGuard)
  async getById(
    @Param('id', ParseUserIdPipe) id: 'me' | number,
    @CurrentUser() user: Session,
  ): Promise<WerknemerResponseDTO> {
    const werknemerId = id === 'me' ? user.id : id;
    return this.werknemerService.getById(werknemerId);
  }

  @ApiResponse({
    status: 200,
    description: 'Get alle taken van een user',
    type: TakenListResponseDTO,
  })
  @Get(':id/taken')
  @UseGuards(CheckUserAccessGuard)
  async getTaken(
    @Param('id', ParseUserIdPipe) id: 'me' | number,
    @CurrentUser() user: Session,
  ): Promise<TakenListResponseDTO> {
    const werknemerId = id === 'me' ? user.id : id;
    return this.taakService.getTakenWerknemer(werknemerId);
  }

  @Get(':id/takenSupervisor')
  @Roles(Role.VERANTWOORDELIJKE)
  async getTakenSupervisor(
    @Param('id', ParseUserIdPipe) id: 'me' | number,
    @CurrentUser() user: Session,
  ): Promise<TakenListResponseDTO> {
    const supervisorId = id === 'me' ? user.id : id;
    return this.taakService.getTakenSupervisor(supervisorId);
  }

  @Get()
  @Roles(Role.MANAGER)
  async getAll(): Promise<WerknemerDetailListResponseDTO> {
    return this.werknemerService.getAll();
  }
}
