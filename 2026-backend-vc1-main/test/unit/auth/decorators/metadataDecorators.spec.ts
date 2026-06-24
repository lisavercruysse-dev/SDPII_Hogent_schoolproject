import 'reflect-metadata';
import { IS_PUBLIC_KEY, Public } from 'src/auth/decorators/publicDecorator';
import { ROLES_KEY, Roles } from 'src/auth/decorators/rolesDecorator';

describe('Auth decorators metadata', () => {
  it('sets public metadata on a method', () => {
    class TestClass {
      @Public()
      endpoint() {
        return null;
      }
    }
    const endpoint = Object.getOwnPropertyDescriptor(
      TestClass.prototype,
      'endpoint',
    )?.value;

    const metadata = Reflect.getMetadata(IS_PUBLIC_KEY, endpoint) as boolean;
    expect(metadata).toBe(true);
  });

  it('sets roles metadata with provided values', () => {
    class TestClass {
      @Roles('manager', 'werknemer')
      endpoint() {
        return null;
      }
    }
    const endpoint = Object.getOwnPropertyDescriptor(
      TestClass.prototype,
      'endpoint',
    )?.value;

    const metadata = Reflect.getMetadata(ROLES_KEY, endpoint) as string[];
    expect(metadata).toEqual(['manager', 'werknemer']);
  });
});
