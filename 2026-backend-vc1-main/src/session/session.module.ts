import { Module } from '@nestjs/common';
import { SessionController } from './session.controller';
import { AuthService } from 'src/auth/auth.service';
import { AuthModule } from 'src/auth/auth.module';

@Module({
  controllers: [SessionController],
  imports: [AuthModule],
})
export class SessionModule {}
