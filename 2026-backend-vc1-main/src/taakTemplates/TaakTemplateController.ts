import { Controller, Get } from '@nestjs/common';
import { Roles } from 'src/auth/decorators/rolesDecorator';
import { Role } from 'src/auth/roles';
import { TaakTemplateResponseListDTO } from './TaakTemplateDTO';
import { TaakTemplateService } from './TaakTemplateService';

@Controller('taakTemplates')
export class TaakTemplateController {
  constructor(private readonly taakTemplateService: TaakTemplateService) {}

  @Get('')
  @Roles(Role.MANAGER, Role.VERANTWOORDELIJKE)
  async getAll(): Promise<TaakTemplateResponseListDTO> {
    return await this.taakTemplateService.getAll();
  }
}
