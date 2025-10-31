
export type RolUsuario = 'ADMIN' | 'EDITOR' | 'LECTOR';


export interface LoginRequest {
  email: string;
  password: string;
}


export interface LoginResponse {
  token: string;

  role?: RolUsuario | string;
  rol?: RolUsuario | string;
  userId?: string;
  name?: string;
  empresaId?: number;
}
