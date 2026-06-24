export interface JwtPayload {
  sub: number;
  email: string;
  jobTitel: string;
}

export interface Session {
  id: number;
  email: string;
  jobTitel: string;
}
