import { Module } from '@nestjs/common';
import { AuthModule } from 'src/auth/auth.module';
import { DrizzleModule } from 'src/drizzle/drizzle.module';
import { TeamService } from './TeamService';
import { TeamController } from './TeamController';

@Module({
  imports: [DrizzleModule, AuthModule],
  providers: [TeamService],
  controllers: [TeamController],
  exports: [TeamService],
})
export class TeamModule {}
