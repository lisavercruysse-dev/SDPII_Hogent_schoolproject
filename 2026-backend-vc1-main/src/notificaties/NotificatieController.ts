import {
  Controller,
  Get,
  Post,
  Body,
  Param,
  Patch,
  Query,
} from '@nestjs/common';
import { NotificatieService } from './NotificatieService';
import { CreateNotificatieDto, NotificatieType } from './NotificatieDTO';

@Controller('api/notificaties')
export class NotificatieController {
  constructor(private readonly notificatieService: NotificatieService) {}

  @Get('werknemer/:werknemerId')
  async getByWerknemer(
    @Param('werknemerId') werknemerId: string,
    @Query('filter') filter?: 'unread' | NotificatieType,
  ) {
    return this.notificatieService.findAllByWerknemer(
      Number(werknemerId),
      filter,
    );
  }

  @Get('werknemer/:werknemerId/unread-count')
  async getUnreadCount(@Param('werknemerId') werknemerId: string) {
    return this.notificatieService.getUnreadCount(Number(werknemerId));
  }

  @Get(':id')
  async getById(@Param('id') id: string) {
    return this.notificatieService.findById(Number(id));
  }

  @Patch(':id/read')
  async markAsRead(@Param('id') id: string) {
    return this.notificatieService.markAsRead(Number(id));
  }

  @Post()
  async create(@Body() createNotificatieDto: CreateNotificatieDto) {
    return this.notificatieService.create(createNotificatieDto);
  }
}
