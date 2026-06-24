import { Controller, Get, UseGuards } from '@nestjs/common';
import { ApiBearerAuth, ApiResponse, ApiTags } from '@nestjs/swagger';
import { SiteService } from './SiteService';
import {
  SitesListResponseDTO,
  SiteDetailResponseDTO,
  SiteTaskStatsDTO,
} from './SiteDTO';
import { MachineResponseDTO } from '../machines/MachineDTO';
import { CheckUserAccessGuard } from 'src/auth/guards/userAccessGuard';
import { Param } from '@nestjs/common';
import { type Session } from 'src/types/auth';
import { CurrentUser } from 'src/auth/decorators/currentUserDecorator';

@ApiTags('sites')
@ApiBearerAuth()
@ApiResponse({ status: 401, description: 'Unauthorized' })
@Controller('sites')
export class SiteController {
  constructor(private readonly siteService: SiteService) {}

  @Get()
  @UseGuards(CheckUserAccessGuard)
  async getAll(): Promise<SitesListResponseDTO> {
    return this.siteService.getAll();
  }

  @Get('mine')
  @UseGuards(CheckUserAccessGuard)
  async getMySite(@CurrentUser() user: Session) {
    return this.siteService.getMySite(user.id);
  }

  @Get(':id')
  @UseGuards(CheckUserAccessGuard)
  async getById(@Param('id') id: string): Promise<SiteDetailResponseDTO> {
    return this.siteService.getById(Number(id));
  }

  @Get(':id/machines')
  @UseGuards(CheckUserAccessGuard)
  async getMachinesBySiteId(
    @Param('id') id: string,
  ): Promise<{ items: MachineResponseDTO[] }> {
    return this.siteService.getMachinesBySiteId(Number(id));
  }

  @Get(':id/machine-stats')
  @UseGuards(CheckUserAccessGuard)
  async getMachineStats(@Param('id') id: string) {
    return this.siteService.getMachineStats(Number(id));
  }

  @Get(':id/worker-stats')
  @UseGuards(CheckUserAccessGuard)
  async getWorkerStats(@Param('id') id: string) {
    return this.siteService.getWorkerStats(Number(id));
  }

  @Get(':id/task-stats')
  @UseGuards(CheckUserAccessGuard)
  async getTaskStats(@Param('id') id: string): Promise<SiteTaskStatsDTO> {
    return this.siteService.getTaskStats(Number(id));
  }
}
