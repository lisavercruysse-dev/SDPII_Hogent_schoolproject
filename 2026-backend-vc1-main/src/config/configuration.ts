export default () => ({
  env: process.env.NODE_ENV, 
  port: parseInt(process.env.PORT || '3000'),
  database: {
    url: process.env.DATABASE_URL,
  },
  auth: {
    hashLength: parseInt(process.env.AUTH_HASH_LENGTH || '32'),
    timeCost: parseInt(process.env.AUTH_HASH_TIME_COST || '6'),
    memoryCost: parseInt(process.env.AUTH_HASH_MEMORY_COST || '65536'),
    maxDelay: parseInt(process.env.AUTH_MAX_DELAY  || '100'),
    jwt: {
      expirationInterval:
        Number(process.env.AUTH_JWT_EXPIRATION_INTERVAL || '3600'),
      secret: process.env.AUTH_JWT_SECRET || '',
      audience: process.env.AUTH_JWT_AUDIENCE || 'SDPII_VC01',
      issuer: process.env.AUTH_JWT_ISSUER || 'SDPII_VC01',
    }
  }
});

export interface ServerConfig {
  env: string;
  port: number;
  database: DatabaseConfig;
  auth: AuthConfig;
}

export interface DatabaseConfig {
  url: string;
}

export interface JwtConfig {
  expirationInterval: number;
  secret: string;
  audience: string;
  issuer: string;
}

export interface AuthConfig {
  hashLength: number;
  timeCost: number;
  memoryCost: number;
  jwt: JwtConfig;
}