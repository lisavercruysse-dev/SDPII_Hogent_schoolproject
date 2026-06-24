import { Module } from '@nestjs/common';
import { DrizzleModule } from 'src/drizzle/drizzle.module';
import { SiteService } from './SiteService';
import { SiteController } from './SiteController';

@Module({
  imports: [DrizzleModule],
  providers: [SiteService],
  controllers: [SiteController],
  exports: [SiteService],
})
export class SiteModule {}
