export interface ITipoEvento {
  id?: number;
  idTipoEvento?: number | null;
  dsTipoEvento?: string | null;
}

export class TipoEvento implements ITipoEvento {
  constructor(public id?: number, public idTipoEvento?: number | null, public dsTipoEvento?: string | null) {}
}

export function getTipoEventoIdentifier(tipoEvento: ITipoEvento): number | undefined {
  return tipoEvento.id;
}
