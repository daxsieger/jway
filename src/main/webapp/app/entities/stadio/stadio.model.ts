import { IProcesso } from 'app/entities/processo/processo.model';

export interface IStadio {
  id?: number;
  idStadio?: number | null;
  dsStadio?: string | null;
  processo?: IProcesso | null;
}

export class Stadio implements IStadio {
  constructor(public id?: number, public idStadio?: number | null, public dsStadio?: string | null, public processo?: IProcesso | null) {}
}

export function getStadioIdentifier(stadio: IStadio): number | undefined {
  return stadio.id;
}
