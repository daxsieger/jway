import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IAssistito, getAssistitoIdentifier } from '../assistito.model';

export type EntityResponseType = HttpResponse<IAssistito>;
export type EntityArrayResponseType = HttpResponse<IAssistito[]>;

@Injectable({ providedIn: 'root' })
export class AssistitoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/assistitos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(assistito: IAssistito): Observable<EntityResponseType> {
    return this.http.post<IAssistito>(this.resourceUrl, assistito, { observe: 'response' });
  }

  update(assistito: IAssistito): Observable<EntityResponseType> {
    return this.http.put<IAssistito>(`${this.resourceUrl}/${getAssistitoIdentifier(assistito) as number}`, assistito, {
      observe: 'response',
    });
  }

  partialUpdate(assistito: IAssistito): Observable<EntityResponseType> {
    return this.http.patch<IAssistito>(`${this.resourceUrl}/${getAssistitoIdentifier(assistito) as number}`, assistito, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IAssistito>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IAssistito[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addAssistitoToCollectionIfMissing(
    assistitoCollection: IAssistito[],
    ...assistitosToCheck: (IAssistito | null | undefined)[]
  ): IAssistito[] {
    const assistitos: IAssistito[] = assistitosToCheck.filter(isPresent);
    if (assistitos.length > 0) {
      const assistitoCollectionIdentifiers = assistitoCollection.map(assistitoItem => getAssistitoIdentifier(assistitoItem)!);
      const assistitosToAdd = assistitos.filter(assistitoItem => {
        const assistitoIdentifier = getAssistitoIdentifier(assistitoItem);
        if (assistitoIdentifier == null || assistitoCollectionIdentifiers.includes(assistitoIdentifier)) {
          return false;
        }
        assistitoCollectionIdentifiers.push(assistitoIdentifier);
        return true;
      });
      return [...assistitosToAdd, ...assistitoCollection];
    }
    return assistitoCollection;
  }
}
