import { Module } from '@nestjs/common';
import { DrizzleModule } from 'src/drizzle/drizzle.module';
import { TaakService } from './TaakService';
import { TaakController } from './TaakController';
import { AuthModule } from 'src/auth/auth.module';
import { NotificatieModule } from 'src/notificaties/NotificatieModule';

@Module({
  imports: [DrizzleModule, AuthModule, NotificatieModule],
  providers: [TaakService],
  controllers: [TaakController],
  exports: [TaakService],
})
export class TaakModule {}
