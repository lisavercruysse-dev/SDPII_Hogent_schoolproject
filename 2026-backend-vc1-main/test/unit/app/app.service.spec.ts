import { AppService } from 'src/app.service';

describe('AppService', () => {
  it('returns the configured greeting message', () => {
    const service = new AppService();
    expect(service.getHello()).toBe('Hallo Team VC-G01!');
  });
});
