import { Module } from '@nestjs/common';
import { AppController } from './app.controller';
import { AppService } from './app.service';
import { HealthController } from './health/health.controller';
import { ConfigModule } from '@nestjs/config';
import { DrizzleModule } from './drizzle/drizzle.module';
import configuration from './config/configuration';
import { AfwezigheidModule } from './afwezigheden/AfwezigheidModule';
import { WerknemerModule } from './werknemers/WerknemerModule';
import { NotificatieModule } from './notificaties/NotificatieModule';
import { AuthModule } from './auth/auth.module';
import { SessionModule } from './session/session.module';
import { APP_GUARD } from '@nestjs/core';
import { AuthGuard } from './auth/guards/authGuard';
import { RolesGuard } from './auth/guards/rolesGuard';
import { TaakModule } from './taken/TaakModule';
import { TaakTemplateModule } from './taakTemplates/TaakTemplateModule';
import { TeamModule } from './teams/TeamModule';
import { SiteModule } from './sites/SiteModule';


@Module({
  imports: [
    ConfigModule.forRoot({
      load: [configuration],
      isGlobal: true,
    }),
    DrizzleModule,
    AfwezigheidModule,
    WerknemerModule,
    NotificatieModule,
    AuthModule,
    SessionModule,
    TaakModule,
    TaakTemplateModule,
    TeamModule,
    SiteModule,
  ],
  controllers: [AppController, HealthController],
  providers: [
    {
      provide: APP_GUARD,
      useClass: AuthGuard,
    },
    {
      provide: APP_GUARD,
      useClass: RolesGuard,
    },
    AppService,
  ],
})
export class AppModule {}
