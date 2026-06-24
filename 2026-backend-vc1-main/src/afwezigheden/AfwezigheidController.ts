import { Controller, Get, Post, Body, Param, Patch } from '@nestjs/common';
import { AfwezigheidService } from './AfwezigheidService';
import { CreateAfwezigheidDto } from './AfwezigheidDTO';
import { CurrentUser } from '../auth/decorators/currentUserDecorator';
import { type Session } from '../types/auth';

@Controller('afwezigheden')
export class AfwezigheidController {
  constructor(private readonly afwezigheidService: AfwezigheidService) {}

  @Get('me')
  async getMyAbsences(@CurrentUser() user: Session) {
    return this.afwezigheidService.getMyAbsences(user.id);
  }

  @Get('all')
  async getAll(@CurrentUser() user: Session) {
    return this.afwezigheidService.findAll(user.id);
  }

  @Get('werknemer/:werknemerId')
  async getByWerknemer(@Param('werknemerId') werknemerId: string) {
    return this.afwezigheidService.findAllByWerknemer(Number(werknemerId));
  }

  @Post()
  async create(
    @Body() createAfwezigheidDto: CreateAfwezigheidDto,
    @CurrentUser() user: Session,
  ) {
    createAfwezigheidDto.werknemerId = user.id;
    return this.afwezigheidService.create(createAfwezigheidDto);
  }

  @Patch(':id/cancel')
  async cancel(@Param('id') id: string, @CurrentUser() user: Session) {
    return this.afwezigheidService.cancel(Number(id), user.id);
  }

  @Patch(':id/approve')
  async approve(@Param('id') id: string, @CurrentUser() user: Session) {
    return this.afwezigheidService.approve(Number(id), user.id);
  }

  @Patch(':id/reject')
  async reject(@Param('id') id: string, @CurrentUser() user: Session) {
    return this.afwezigheidService.reject(Number(id), user.id);
  }
}
