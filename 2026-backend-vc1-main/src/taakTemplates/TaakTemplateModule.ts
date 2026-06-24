import { Module } from '@nestjs/common';
import { AuthModule } from 'src/auth/auth.module';
import { DrizzleModule } from 'src/drizzle/drizzle.module';
import { TaakTemplateController } from './TaakTemplateController';
import { TaakTemplateService } from './TaakTemplateService';

@Module({
  imports: [DrizzleModule, AuthModule],
  providers: [TaakTemplateService],
  controllers: [TaakTemplateController],
  exports: [TaakTemplateService],
})
export class TaakTemplateModule {}
