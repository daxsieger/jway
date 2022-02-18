export interface IProduttore {
  id?: number;
  idProduttore?: number | null;
  dsProduttore?: string | null;
}

export class Produttore implements IProduttore {
  constructor(public id?: number, public idProduttore?: number | null, public dsProduttore?: string | null) {}
}

export function getProduttoreIdentifier(produttore: IProduttore): number | undefined {
  return produttore.id;
}
