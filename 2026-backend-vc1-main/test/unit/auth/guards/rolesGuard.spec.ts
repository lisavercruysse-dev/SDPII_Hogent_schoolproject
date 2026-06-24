/// <reference types="jest" />

import {
  ForbiddenException,
  UnauthorizedException,
  type ExecutionContext,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { RolesGuard } from 'src/auth/guards/rolesGuard';

const createExecutionContext = (request: Record<string, unknown>) =>
  ({
    switchToHttp: () => ({
      getRequest: () => request,
    }),
    getHandler: () => jest.fn(),
    getClass: () => class {},
  }) as unknown as ExecutionContext;

describe('RolesGuard', () => {
  const getAllAndOverride = jest.fn();
  let guard: RolesGuard;

  beforeEach(() => {
    getAllAndOverride.mockReset();
    guard = new RolesGuard({ getAllAndOverride } as unknown as Reflector);
  });

  it('returns true when endpoint has no required roles', () => {
    getAllAndOverride.mockReturnValue(undefined);
    const context = createExecutionContext({});

    expect(guard.canActivate(context)).toBe(true);
  });

  it('throws unauthorized when request has no user', () => {
    getAllAndOverride.mockReturnValue(['manager']);
    const context = createExecutionContext({});

    expect(() => guard.canActivate(context)).toThrow(
      new UnauthorizedException('You need to be signed in'),
    );
  });

  it('authorizes user with role match regardless of casing', () => {
    getAllAndOverride.mockReturnValue(['MANAGER']);
    const context = createExecutionContext({
      user: { roles: [' manager '] },
    });

    expect(guard.canActivate(context)).toBe(true);
  });

  it('authorizes when user roles are provided as a single string', () => {
    getAllAndOverride.mockReturnValue(['werknemer']);
    const context = createExecutionContext({
      user: { roles: 'WERKNEMER' },
    });

    expect(guard.canActivate(context)).toBe(true);
  });

  it('throws forbidden when user does not have required role', () => {
    getAllAndOverride.mockReturnValue(['manager']);
    const context = createExecutionContext({
      user: { roles: ['werknemer'] },
    });

    expect(() => guard.canActivate(context)).toThrow(
      new ForbiddenException('You do not have access to this resource'),
    );
  });
});
