import { Injectable } from '@nestjs/common';
import {
  type DatabaseProvider,
  InjectDrizzle,
} from 'src/drizzle/drizzle.provider';
import { TaakTemplateResponseListDTO } from './TaakTemplateDTO';

@Injectable()
export class TaakTemplateService {
  constructor(
    @InjectDrizzle()
    private readonly db: DatabaseProvider,
  ) {}

  async getAll(): Promise<TaakTemplateResponseListDTO> {
    const items = await this.db.query.taakTemplates.findMany();

    return { items: items };
  }
}
