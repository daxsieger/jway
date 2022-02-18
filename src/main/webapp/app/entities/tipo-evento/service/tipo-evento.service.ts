import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITipoEvento, getTipoEventoIdentifier } from '../tipo-evento.model';

export type EntityResponseType = HttpResponse<ITipoEvento>;
export type EntityArrayResponseType = HttpResponse<ITipoEvento[]>;

@Injectable({ providedIn: 'root' })
export class TipoEventoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/tipo-eventos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(tipoEvento: ITipoEvento): Observable<EntityResponseType> {
    return this.http.post<ITipoEvento>(this.resourceUrl, tipoEvento, { observe: 'response' });
  }

  update(tipoEvento: ITipoEvento): Observable<EntityResponseType> {
    return this.http.put<ITipoEvento>(`${this.resourceUrl}/${getTipoEventoIdentifier(tipoEvento) as number}`, tipoEvento, {
      observe: 'response',
    });
  }

  partialUpdate(tipoEvento: ITipoEvento): Observable<EntityResponseType> {
    return this.http.patch<ITipoEvento>(`${this.resourceUrl}/${getTipoEventoIdentifier(tipoEvento) as number}`, tipoEvento, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITipoEvento>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITipoEvento[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTipoEventoToCollectionIfMissing(
    tipoEventoCollection: ITipoEvento[],
    ...tipoEventosToCheck: (ITipoEvento | null | undefined)[]
  ): ITipoEvento[] {
    const tipoEventos: ITipoEvento[] = tipoEventosToCheck.filter(isPresent);
    if (tipoEventos.length > 0) {
      const tipoEventoCollectionIdentifiers = tipoEventoCollection.map(tipoEventoItem => getTipoEventoIdentifier(tipoEventoItem)!);
      const tipoEventosToAdd = tipoEventos.filter(tipoEventoItem => {
        const tipoEventoIdentifier = getTipoEventoIdentifier(tipoEventoItem);
        if (tipoEventoIdentifier == null || tipoEventoCollectionIdentifiers.includes(tipoEventoIdentifier)) {
          return false;
        }
        tipoEventoCollectionIdentifiers.push(tipoEventoIdentifier);
        return true;
      });
      return [...tipoEventosToAdd, ...tipoEventoCollection];
    }
    return tipoEventoCollection;
  }
}
