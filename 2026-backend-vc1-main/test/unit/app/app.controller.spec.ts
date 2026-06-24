import { Test, TestingModule } from '@nestjs/testing';
import { AppController } from 'src/app.controller';
import { AppService } from 'src/app.service';

describe('AppController', () => {
  let appController: AppController;
  let appService: AppService;

  beforeEach(async () => {
    const app: TestingModule = await Test.createTestingModule({
      controllers: [AppController],
      providers: [AppService],
    }).compile();

    appController = app.get<AppController>(AppController);
    appService = app.get<AppService>(AppService);
  });

  describe('root', () => {
    it('should return welcome message from service', () => {
      expect(appController.getHello()).toBe('Hallo Team VC-G01!');
    });

    it('should delegate to AppService', () => {
      const spy = jest.spyOn(appService, 'getHello');
      appController.getHello();
      expect(spy).toHaveBeenCalledTimes(1);
    });
  });
});
