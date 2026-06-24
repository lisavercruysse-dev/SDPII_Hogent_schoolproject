import {
  NotFoundException,
  UnauthorizedException,
  type ExecutionContext,
} from '@nestjs/common';
import { CheckUserAccessGuard } from 'src/auth/guards/userAccessGuard';

const createExecutionContext = (request: Record<string, unknown>) =>
  ({
    switchToHttp: () => ({
      getRequest: () => request,
    }),
  }) as unknown as ExecutionContext;

describe('CheckUserAccessGuard', () => {
  const guard = new CheckUserAccessGuard();

  it('throws unauthorized when no user is attached', () => {
    const context = createExecutionContext({ params: { id: '1' } });

    expect(() => guard.canActivate(context)).toThrow(
      new UnauthorizedException('You need to be signed in'),
    );
  });

  it('allows access to own user id', () => {
    const context = createExecutionContext({
      user: { id: 15, jobTitel: 'werknemer' },
      params: { id: '15' },
    });

    expect(guard.canActivate(context)).toBe(true);
  });

  it('allows access for /me route', () => {
    const context = createExecutionContext({
      user: { id: 15, jobTitel: 'werknemer' },
      params: { id: 'me' },
    });

    expect(guard.canActivate(context)).toBe(true);
  });

  it('allows manager access to another werknemer id', () => {
    const context = createExecutionContext({
      user: { id: 15, jobTitel: 'manager' },
      params: { id: '21' },
    });

    expect(guard.canActivate(context)).toBe(true);
  });

  it('throws not found for werknemer requesting another id', () => {
    const context = createExecutionContext({
      user: { id: 15, jobTitel: 'werknemer' },
      params: { id: '21' },
    });

    expect(() => guard.canActivate(context)).toThrow(
      new NotFoundException('No user with this id exists'),
    );
  });
});
