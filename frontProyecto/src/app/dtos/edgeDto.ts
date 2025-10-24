export interface EdgeDTO {
  id: string;
  processId: string;
  fromId: string; // id de origen
  toId: string;   // id de destino
  label?: string; // texto opcional en el arco
}
