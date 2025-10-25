export interface CrearEmpresaRequest {
  nombreEmpresa: string;
  nit: string;
  correoEmpresa: string;

  nombreAdmin?: string;
  correoAdmin?: string;
  password?: string;
}

export interface CrearEmpresaResponse {
  id: number;
  nombre: string;
  nit: string;
  correoContacto: string;
  password?: string;
  
}
