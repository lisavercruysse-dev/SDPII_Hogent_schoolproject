import { Injectable, UnauthorizedException } from '@nestjs/common';
import {
    type DatabaseProvider,
    InjectDrizzle,
} from '../drizzle/drizzle.provider';
import { JwtService } from '@nestjs/jwt';
import { ConfigService } from '@nestjs/config';
import { AuthConfig, ServerConfig } from 'src/config/configuration';
import * as argon2 from 'argon2'
import { Werknemer } from 'src/types/werknemer';
import { JwtPayload } from 'src/types/auth';
import { LoginRequestDTO } from 'src/session/sessionDTO';
import { werknemers } from 'src/drizzle/schema';
import { eq } from 'drizzle-orm';

@Injectable()
export class AuthService {
    constructor(
        @InjectDrizzle()
        private readonly db: DatabaseProvider,
        private readonly jwtService: JwtService,
        private readonly configService: ConfigService<ServerConfig>,
    ) {}

    async hasPassword(password: string): Promise<string> {
        const authConfig = this.configService.get<AuthConfig>('auth')!;

        return argon2.hash(password, {
            type: argon2.argon2id,
            hashLength: authConfig.hashLength,
            timeCost: authConfig.timeCost,
            memoryCost: authConfig.memoryCost,
        })
    }

    async verifyPassword(password: string, hash: string): Promise<boolean> {
        return await argon2.verify(hash, password);
    }

    private signJwt(werknemer: Werknemer): string {
        return this.jwtService.sign({
            sub: werknemer.id,
            email: werknemer.email,
            jobTitel: werknemer.jobTitel,
        })
    }

    async verifyJwt(token: string): Promise<JwtPayload> {
        const payload = await this.jwtService.verifyAsync<JwtPayload>(token);

        if (!payload) {
            throw new UnauthorizedException('Invalid authentication token');
        }

        return payload;
    }

    async login({email, password}: LoginRequestDTO): Promise<string> {
        const werknemer = await this.db.query.werknemers.findFirst({
            where: eq(werknemers.email, email)
        });

        if (!werknemer) {
            throw new UnauthorizedException(
                'The given email and password do not match',
            );
        }

        const passwordValid = await this.verifyPassword(
            password,
            werknemer.passwordHash,
        );

        if (!passwordValid) {
            throw new UnauthorizedException(
                'The given email and password do not match',
            );
        }

        return this.signJwt(werknemer);
    }
}
