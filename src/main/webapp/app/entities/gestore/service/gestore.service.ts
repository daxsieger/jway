import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IGestore, getGestoreIdentifier } from '../gestore.model';

export type EntityResponseType = HttpResponse<IGestore>;
export type EntityArrayResponseType = HttpResponse<IGestore[]>;

@Injectable({ providedIn: 'root' })
export class GestoreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/gestores');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(gestore: IGestore): Observable<EntityResponseType> {
    return this.http.post<IGestore>(this.resourceUrl, gestore, { observe: 'response' });
  }

  update(gestore: IGestore): Observable<EntityResponseType> {
    return this.http.put<IGestore>(`${this.resourceUrl}/${getGestoreIdentifier(gestore) as number}`, gestore, { observe: 'response' });
  }

  partialUpdate(gestore: IGestore): Observable<EntityResponseType> {
    return this.http.patch<IGestore>(`${this.resourceUrl}/${getGestoreIdentifier(gestore) as number}`, gestore, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IGestore>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IGestore[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addGestoreToCollectionIfMissing(gestoreCollection: IGestore[], ...gestoresToCheck: (IGestore | null | undefined)[]): IGestore[] {
    const gestores: IGestore[] = gestoresToCheck.filter(isPresent);
    if (gestores.length > 0) {
      const gestoreCollectionIdentifiers = gestoreCollection.map(gestoreItem => getGestoreIdentifier(gestoreItem)!);
      const gestoresToAdd = gestores.filter(gestoreItem => {
        const gestoreIdentifier = getGestoreIdentifier(gestoreItem);
        if (gestoreIdentifier == null || gestoreCollectionIdentifiers.includes(gestoreIdentifier)) {
          return false;
        }
        gestoreCollectionIdentifiers.push(gestoreIdentifier);
        return true;
      });
      return [...gestoresToAdd, ...gestoreCollection];
    }
    return gestoreCollection;
  }
}
