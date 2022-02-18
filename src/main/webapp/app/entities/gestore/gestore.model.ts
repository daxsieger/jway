export interface IGestore {
  id?: number;
  idGestore?: number | null;
}

export class Gestore implements IGestore {
  constructor(public id?: number, public idGestore?: number | null) {}
}

export function getGestoreIdentifier(gestore: IGestore): number | undefined {
  return gestore.id;
}
