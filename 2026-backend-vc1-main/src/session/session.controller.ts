import { Controller, Post, Body, HttpStatus, HttpCode, UseInterceptors } from '@nestjs/common';
import { AuthService } from 'src/auth/auth.service';
import { LoginRequestDTO, LoginResponseDTO } from './sessionDTO';
import { Public } from 'src/auth/decorators/publicDecorator';
import { AuthDelayInterceptor } from 'src/auth/interceptors/authDelayInterceptor';

@Controller('sessions')
export class SessionController {
    constructor(private authService: AuthService) {}

    @UseInterceptors(AuthDelayInterceptor)
    @Public()
    @Post()
    @HttpCode(HttpStatus.OK)
    async signIn(@Body() loginDto: LoginRequestDTO): Promise<LoginResponseDTO> {
        const token = await this.authService.login(loginDto)
        return {token}
    }
}
