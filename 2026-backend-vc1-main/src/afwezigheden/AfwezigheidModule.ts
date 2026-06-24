import { Module } from '@nestjs/common';
import { AfwezigheidController } from './AfwezigheidController';
import { AfwezigheidService } from './AfwezigheidService';
import { DrizzleModule } from '../drizzle/drizzle.module';
import { NotificatieModule } from '../notificaties/NotificatieModule';

@Module({
  imports: [DrizzleModule, NotificatieModule],
  controllers: [AfwezigheidController],
  providers: [AfwezigheidService],
})
export class AfwezigheidModule {}
