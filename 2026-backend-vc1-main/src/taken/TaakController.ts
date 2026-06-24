import {
  Body,
  Controller,
  Get,
  Param,
  ParseIntPipe,
  Put,
  UseGuards,
  Delete,
  Post,
} from '@nestjs/common';
import { TaakService } from './TaakService';
import { ApiBearerAuth, ApiTags } from '@nestjs/swagger';
import {
  CreateTaakRequestDTO,
  TaakDetailsResponseDTO,
  TaakUpdateRequestDTO,
  TaakUpdateStatusRequestDTO,
  TakenListResponseDTO,
} from './TaakDTO';
import { AuthGuard } from 'src/auth/guards/authGuard';
import { Roles } from 'src/auth/decorators/rolesDecorator';
import { Role } from 'src/auth/roles';
import { RolesGuard } from 'src/auth/guards/rolesGuard';

@ApiTags('taken')
@ApiBearerAuth()
@Controller('taken')
export class TaakController {
  constructor(private readonly taakService: TaakService) {}

  @Get(':id/details')
  @UseGuards(AuthGuard)
  async getById(
    @Param('id', ParseIntPipe) id: number,
  ): Promise<TaakDetailsResponseDTO> {
    const taakDetails = await this.taakService.getTaakDetails(id);

    return taakDetails;
  }

  @Get('onafgewerkt')
  @Roles(Role.MANAGER, Role.VERANTWOORDELIJKE)
  async getOnafgewerkt(): Promise<TakenListResponseDTO> {
    return await this.taakService.getOnafgewerkt();
  }

  @Put(':id/status')
  async updateTaskStatus(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateStatusDto: TaakUpdateStatusRequestDTO,
  ): Promise<TaakDetailsResponseDTO> {
    await this.taakService.updateStatusById(id, updateStatusDto);
    return this.taakService.getTaakDetails(id);
  }

  @Put(':id')
  @UseGuards(RolesGuard)
  @Roles(Role.VERANTWOORDELIJKE, Role.MANAGER)
  async updateTask(
    @Param('id', ParseIntPipe) id: number,
    @Body() updateTaskDto: TaakUpdateRequestDTO,
  ): Promise<TaakDetailsResponseDTO> {
    await this.taakService.updateById(id, updateTaskDto);
    return this.taakService.getTaakDetails(id);
  }

  @Delete(':id')
  @Roles(Role.VERANTWOORDELIJKE, Role.MANAGER)
  async deleteTask(@Param('id', ParseIntPipe) id: number): Promise<void> {
    await this.taakService.deleteById(id);
  }

  @Post()
  @Roles(Role.MANAGER, Role.VERANTWOORDELIJKE)
  async createTask(
    @Body() createTaakRequestDTO: CreateTaakRequestDTO,
  ): Promise<TaakDetailsResponseDTO> {
    return await this.taakService.create(createTaakRequestDTO);
  }

  @Get()
  @Roles(Role.MANAGER)
  async getAll(): Promise<TakenListResponseDTO> {
    return await this.taakService.getAll();
  }
}
