import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStadio, getStadioIdentifier } from '../stadio.model';

export type EntityResponseType = HttpResponse<IStadio>;
export type EntityArrayResponseType = HttpResponse<IStadio[]>;

@Injectable({ providedIn: 'root' })
export class StadioService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/stadios');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stadio: IStadio): Observable<EntityResponseType> {
    return this.http.post<IStadio>(this.resourceUrl, stadio, { observe: 'response' });
  }

  update(stadio: IStadio): Observable<EntityResponseType> {
    return this.http.put<IStadio>(`${this.resourceUrl}/${getStadioIdentifier(stadio) as number}`, stadio, { observe: 'response' });
  }

  partialUpdate(stadio: IStadio): Observable<EntityResponseType> {
    return this.http.patch<IStadio>(`${this.resourceUrl}/${getStadioIdentifier(stadio) as number}`, stadio, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IStadio>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IStadio[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addStadioToCollectionIfMissing(stadioCollection: IStadio[], ...stadiosToCheck: (IStadio | null | undefined)[]): IStadio[] {
    const stadios: IStadio[] = stadiosToCheck.filter(isPresent);
    if (stadios.length > 0) {
      const stadioCollectionIdentifiers = stadioCollection.map(stadioItem => getStadioIdentifier(stadioItem)!);
      const stadiosToAdd = stadios.filter(stadioItem => {
        const stadioIdentifier = getStadioIdentifier(stadioItem);
        if (stadioIdentifier == null || stadioCollectionIdentifiers.includes(stadioIdentifier)) {
          return false;
        }
        stadioCollectionIdentifiers.push(stadioIdentifier);
        return true;
      });
      return [...stadiosToAdd, ...stadioCollection];
    }
    return stadioCollection;
  }
}
