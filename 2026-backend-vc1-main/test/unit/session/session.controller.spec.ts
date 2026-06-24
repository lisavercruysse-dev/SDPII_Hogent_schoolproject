import { SessionController } from 'src/session/session.controller';
import { AuthService } from 'src/auth/auth.service';

describe('SessionController', () => {
  const login = jest.fn();
  let controller: SessionController;

  beforeEach(() => {
    login.mockReset();
    controller = new SessionController({ login } as unknown as AuthService);
  });

  it('returns token response after successful login', async () => {
    login.mockResolvedValue('signed-jwt-token');

    await expect(
      controller.signIn({ email: 'user@example.com', password: 'secret' }),
    ).resolves.toEqual({ token: 'signed-jwt-token' });
    expect(login).toHaveBeenCalledWith({
      email: 'user@example.com',
      password: 'secret',
    });
  });

  it('propagates auth errors from service', async () => {
    const error = new Error('invalid credentials');
    login.mockRejectedValue(error);

    await expect(
      controller.signIn({ email: 'user@example.com', password: 'wrong' }),
    ).rejects.toThrow(error);
  });
});
