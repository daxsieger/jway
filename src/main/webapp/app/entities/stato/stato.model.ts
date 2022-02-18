import dayjs from 'dayjs/esm';
import { IStadio } from 'app/entities/stadio/stadio.model';
import { IEvento } from 'app/entities/evento/evento.model';

export interface IStato {
  id?: number;
  idStadio?: number | null;
  dsStadio?: string | null;
  tsCambioStato?: dayjs.Dayjs | null;
  stadio?: IStadio | null;
  eventis?: IEvento[] | null;
}

export class Stato implements IStato {
  constructor(
    public id?: number,
    public idStadio?: number | null,
    public dsStadio?: string | null,
    public tsCambioStato?: dayjs.Dayjs | null,
    public stadio?: IStadio | null,
    public eventis?: IEvento[] | null
  ) {}
}

export function getStatoIdentifier(stato: IStato): number | undefined {
  return stato.id;
}
