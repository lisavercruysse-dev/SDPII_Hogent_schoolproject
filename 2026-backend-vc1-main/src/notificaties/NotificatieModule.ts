import { Module } from '@nestjs/common';
import { NotificatieController } from './NotificatieController';
import { NotificatieService } from './NotificatieService';
import { DrizzleModule } from 'src/drizzle/drizzle.module';

@Module({
  imports: [DrizzleModule],
  controllers: [NotificatieController],
  providers: [NotificatieService],
  exports: [NotificatieService],
})
export class NotificatieModule {}
