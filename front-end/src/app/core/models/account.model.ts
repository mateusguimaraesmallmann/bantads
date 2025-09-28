import { Observable } from "rxjs";

export interface Account {
  number: number;
  clientCpf: string | null;
  clientName: string | null;
  balance: number;
  limit: number;
  managerCpf: string | null;
  managerName: string | null;
  createdAt: Date;
}
