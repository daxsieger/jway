import { IProcesso } from 'app/entities/processo/processo.model';
import { IStadio } from 'app/entities/stadio/stadio.model';

export interface ITransizioni {
  id?: number;
  idTransizione?: number | null;
  dsTransizione?: string | null;
  processo?: IProcesso | null;
  stadioIniziale?: IStadio | null;
  stadioFinale?: IStadio | null;
}

export class Transizioni implements ITransizioni {
  constructor(
    public id?: number,
    public idTransizione?: number | null,
    public dsTransizione?: string | null,
    public processo?: IProcesso | null,
    public stadioIniziale?: IStadio | null,
    public stadioFinale?: IStadio | null
  ) {}
}

export function getTransizioniIdentifier(transizioni: ITransizioni): number | undefined {
  return transizioni.id;
}
