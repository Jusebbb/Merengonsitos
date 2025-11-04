export class UsuarioDTO {
  constructor(
  public id?: number,
  public nombre?: string,
  public email?: string,
  public password?: string,
  public empresaId?: number,
  public rol?: string
  ){}
}