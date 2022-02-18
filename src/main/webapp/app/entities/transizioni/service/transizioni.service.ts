import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITransizioni, getTransizioniIdentifier } from '../transizioni.model';

export type EntityResponseType = HttpResponse<ITransizioni>;
export type EntityArrayResponseType = HttpResponse<ITransizioni[]>;

@Injectable({ providedIn: 'root' })
export class TransizioniService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/transizionis');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(transizioni: ITransizioni): Observable<EntityResponseType> {
    return this.http.post<ITransizioni>(this.resourceUrl, transizioni, { observe: 'response' });
  }

  update(transizioni: ITransizioni): Observable<EntityResponseType> {
    return this.http.put<ITransizioni>(`${this.resourceUrl}/${getTransizioniIdentifier(transizioni) as number}`, transizioni, {
      observe: 'response',
    });
  }

  partialUpdate(transizioni: ITransizioni): Observable<EntityResponseType> {
    return this.http.patch<ITransizioni>(`${this.resourceUrl}/${getTransizioniIdentifier(transizioni) as number}`, transizioni, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITransizioni>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITransizioni[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTransizioniToCollectionIfMissing(
    transizioniCollection: ITransizioni[],
    ...transizionisToCheck: (ITransizioni | null | undefined)[]
  ): ITransizioni[] {
    const transizionis: ITransizioni[] = transizionisToCheck.filter(isPresent);
    if (transizionis.length > 0) {
      const transizioniCollectionIdentifiers = transizioniCollection.map(transizioniItem => getTransizioniIdentifier(transizioniItem)!);
      const transizionisToAdd = transizionis.filter(transizioniItem => {
        const transizioniIdentifier = getTransizioniIdentifier(transizioniItem);
        if (transizioniIdentifier == null || transizioniCollectionIdentifiers.includes(transizioniIdentifier)) {
          return false;
        }
        transizioniCollectionIdentifiers.push(transizioniIdentifier);
        return true;
      });
      return [...transizionisToAdd, ...transizioniCollection];
    }
    return transizioniCollection;
  }
}
