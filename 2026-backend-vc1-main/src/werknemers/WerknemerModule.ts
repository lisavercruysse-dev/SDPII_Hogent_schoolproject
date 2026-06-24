import { Module } from '@nestjs/common';
import { DrizzleModule } from 'src/drizzle/drizzle.module';
import { WerknemerService } from './WerknemerService';
import { WerknemerController } from './WerknemerController';
import { TaakModule } from 'src/taken/TaakModule';

@Module({
  imports: [DrizzleModule, TaakModule],
  providers: [WerknemerService],
  controllers: [WerknemerController],
  exports: [WerknemerService],
})
export class WerknemerModule {}
