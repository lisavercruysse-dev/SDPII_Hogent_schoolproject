import { Controller, Get, UseGuards } from '@nestjs/common';
import { TeamService } from './TeamService';
import { Roles } from 'src/auth/decorators/rolesDecorator';
import { Role } from 'src/auth/roles';
import { TeamListResponseDTO } from './TeamDTO';
import { CheckUserAccessGuard } from 'src/auth/guards/userAccessGuard';
import { CurrentUser } from 'src/auth/decorators/currentUserDecorator';
import { type Session } from 'src/types/auth';

@Controller('teams')
@UseGuards(CheckUserAccessGuard)
export class TeamController {
  constructor(private readonly teamService: TeamService) {}

  @Get()
  @Roles(Role.MANAGER)
  async getAll(): Promise<TeamListResponseDTO> {
    return this.teamService.getAll();
  }

  @Get('mine')
  @Roles(Role.VERANTWOORDELIJKE)
  async getMyTeams(@CurrentUser() user: Session): Promise<TeamListResponseDTO> {
    return this.teamService.getMyTeams(user.id);
  }
}
