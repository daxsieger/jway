export interface IProcesso {
  id?: number;
  idProcesso?: number | null;
  dsProcesso?: string | null;
}

export class Processo implements IProcesso {
  constructor(public id?: number, public idProcesso?: number | null, public dsProcesso?: string | null) {}
}

export function getProcessoIdentifier(processo: IProcesso): number | undefined {
  return processo.id;
}
