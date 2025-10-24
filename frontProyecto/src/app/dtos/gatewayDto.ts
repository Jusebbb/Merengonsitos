export interface GatewayDTO {
  id: string;
  processId: string;
  kind: 'EXCLUSIVE' | 'PARALLEL' | 'INCLUSIVE';
  name?: string;
}
