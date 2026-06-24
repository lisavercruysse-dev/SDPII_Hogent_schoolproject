import { firstValueFrom, of } from 'rxjs';
import { ConfigService } from '@nestjs/config';
import { AuthDelayInterceptor } from 'src/auth/interceptors/authDelayInterceptor';

describe('AuthDelayInterceptor', () => {
  afterEach(() => {
    jest.restoreAllMocks();
    jest.useRealTimers();
  });

  it('delays response using configured max delay', async () => {
    jest.useFakeTimers();

    const get = jest.fn().mockReturnValue(100);
    const configService = {
      get,
    } as unknown as ConfigService;
    const interceptor = new AuthDelayInterceptor(configService);
    const mathSpy = jest.spyOn(Math, 'random').mockReturnValue(0.5);

    const handle = jest.fn().mockReturnValue(of('ok'));
    const next = {
      handle,
    };

    const resultPromise = firstValueFrom(
      interceptor.intercept({} as never, next as never),
    );

    expect(get).toHaveBeenCalledWith('auth.maxDelay');
    expect(handle).toHaveBeenCalledTimes(1);

    jest.advanceTimersByTime(49);
    await Promise.resolve();

    const isResolved = await Promise.race([
      resultPromise.then(() => true),
      Promise.resolve(false),
    ]);
    expect(isResolved).toBe(false);

    jest.advanceTimersByTime(1);
    await expect(resultPromise).resolves.toBe('ok');
    expect(mathSpy).toHaveBeenCalledTimes(1);
  });
});
