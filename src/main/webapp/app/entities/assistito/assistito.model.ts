export interface IAssistito {
  id?: number;
  idAssistito?: number | null;
}

export class Assistito implements IAssistito {
  constructor(public id?: number, public idAssistito?: number | null) {}
}

export function getAssistitoIdentifier(assistito: IAssistito): number | undefined {
  return assistito.id;
}
