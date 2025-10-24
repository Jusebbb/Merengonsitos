export type ProcessStatus = 'DRAFT' | 'ACTIVE' | 'ARCHIVED';

export interface ProcessDTO {
  id: string;
  name: string;
  description?: string;
  status: ProcessStatus;
  createdAt: string;
  updatedAt: string;
}
