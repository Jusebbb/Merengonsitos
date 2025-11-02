export interface ActivityDTO {
  id: string;
  processId: string;
  name: string;
  type: 'TASK' | 'EVENT' | 'SUBPROCESS';
  description?: string;
  responsibleRole?: string; // opcional (admin, empleado, etc.)
}
