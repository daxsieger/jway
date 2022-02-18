import dayjs from 'dayjs/esm';
import { IAssistito } from 'app/entities/assistito/assistito.model';
import { ITipoEvento } from 'app/entities/tipo-evento/tipo-evento.model';
import { IGestore } from 'app/entities/gestore/gestore.model';
import { IProduttore } from 'app/entities/produttore/produttore.model';
import { IStato } from 'app/entities/stato/stato.model';

export interface IEvento {
  id?: number;
  idEvento?: number | null;
  tsEvento?: dayjs.Dayjs | null;
  note?: string | null;
  assistito?: IAssistito | null;
  tipo?: ITipoEvento | null;
  gestore?: IGestore | null;
  origine?: IProduttore | null;
  statis?: IStato[] | null;
}

export class Evento implements IEvento {
  constructor(
    public id?: number,
    public idEvento?: number | null,
    public tsEvento?: dayjs.Dayjs | null,
    public note?: string | null,
    public assistito?: IAssistito | null,
    public tipo?: ITipoEvento | null,
    public gestore?: IGestore | null,
    public origine?: IProduttore | null,
    public statis?: IStato[] | null
  ) {}
}

export function getEventoIdentifier(evento: IEvento): number | undefined {
  return evento.id;
}
