import { UnauthorizedException, type ExecutionContext } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { AuthService } from 'src/auth/auth.service';
import { AuthGuard } from 'src/auth/guards/authGuard';

const createExecutionContext = (request: Record<string, unknown>) =>
  ({
    switchToHttp: () => ({
      getRequest: () => request,
    }),
    getHandler: () => jest.fn(),
    getClass: () => class {},
  }) as unknown as ExecutionContext;

describe('AuthGuard', () => {
  const verifyJwt = jest.fn();
  const getAllAndOverride = jest.fn();
  let guard: AuthGuard;

  beforeEach(() => {
    verifyJwt.mockReset();
    getAllAndOverride.mockReset();
    guard = new AuthGuard(
      { verifyJwt } as unknown as AuthService,
      { getAllAndOverride } as unknown as Reflector,
    );
  });

  it('returns true for public routes', async () => {
    getAllAndOverride.mockReturnValue(true);
    const context = createExecutionContext({ headers: {} });

    await expect(guard.canActivate(context)).resolves.toBe(true);
    expect(verifyJwt).not.toHaveBeenCalled();
  });

  it('throws when bearer token is missing', async () => {
    getAllAndOverride.mockReturnValue(false);
    const context = createExecutionContext({ headers: {} });

    await expect(guard.canActivate(context)).rejects.toThrow(
      new UnauthorizedException('You need to be signed in'),
    );
  });

  it('attaches normalized user payload to request', async () => {
    getAllAndOverride.mockReturnValue(false);
    verifyJwt.mockResolvedValue({
      sub: 7,
      email: 'test@example.com',
      jobTitel: ' Manager ',
    });

    const request = {
      headers: { authorization: ['Bearer', 'mock-token'].join(' ') },
    };
    const context = createExecutionContext(request);

    await expect(guard.canActivate(context)).resolves.toBe(true);
    expect(request).toMatchObject({
      user: {
        id: 7,
        roles: ['manager'],
        jobTitel: 'manager',
        email: 'test@example.com',
      },
    });
  });

  it('throws token expired message for expired token errors', async () => {
    getAllAndOverride.mockReturnValue(false);
    verifyJwt.mockRejectedValue({ name: 'TokenExpiredError' });
    const context = createExecutionContext({
      headers: { authorization: ['Bearer', 'mock-token'].join(' ') },
    });

    await expect(guard.canActivate(context)).rejects.toThrow(
      new UnauthorizedException('Token has expired'),
    );
  });

  it('throws invalid token message for other jwt errors', async () => {
    getAllAndOverride.mockReturnValue(false);
    verifyJwt.mockRejectedValue(new Error('invalid'));
    const context = createExecutionContext({
      headers: { authorization: ['Bearer', 'mock-token'].join(' ') },
    });

    await expect(guard.canActivate(context)).rejects.toThrow(
      new UnauthorizedException('Invalid authentication token'),
    );
  });
});
