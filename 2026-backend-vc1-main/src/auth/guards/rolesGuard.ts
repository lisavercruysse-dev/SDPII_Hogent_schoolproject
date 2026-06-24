import {
  Injectable,
  CanActivate,
  ExecutionContext,
  ForbiddenException,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { ROLES_KEY } from '../decorators/rolesDecorator';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private reflector: Reflector) {}

  private normalizeRoles(roles: unknown): string[] {
    if (Array.isArray(roles)) {
      return roles
        .map((role) => String(role).trim().toLowerCase())
        .filter(Boolean);
    }

    if (typeof roles === 'string') {
      const normalizedRole = roles.trim().toLowerCase();
      return normalizedRole ? [normalizedRole] : [];
    }

    return [];
  }

  canActivate(context: ExecutionContext): boolean {
    const requiredRoles = this.reflector.getAllAndOverride<string[]>(
      ROLES_KEY,
      [context.getHandler(), context.getClass()],
    );

    if (!requiredRoles) {
      return true;
    }

    const request = context.switchToHttp().getRequest();

    if (!request.user) {
      throw new UnauthorizedException('You need to be signed in');
    }

    const userRoles = this.normalizeRoles(request.user.roles);
    const normalizedRequiredRoles = requiredRoles.map((role) =>
      String(role).trim().toLowerCase(),
    );

    const hasRole = normalizedRequiredRoles.some((role) =>
      userRoles.includes(role),
    );

    if (!hasRole) {
      throw new ForbiddenException('You do not have access to this resource');
    }

    return true;
  }
}
