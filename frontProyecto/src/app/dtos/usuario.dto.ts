export interface UsuarioDTO {
  id?: number;
  nombre: string;
  email: string;
  password: string;
  empresaId: number;
}

export type CrearUsuarioRequest = Omit<UsuarioDTO, 'id'>;
