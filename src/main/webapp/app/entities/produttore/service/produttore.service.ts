import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProduttore, getProduttoreIdentifier } from '../produttore.model';

export type EntityResponseType = HttpResponse<IProduttore>;
export type EntityArrayResponseType = HttpResponse<IProduttore[]>;

@Injectable({ providedIn: 'root' })
export class ProduttoreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/produttores');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(produttore: IProduttore): Observable<EntityResponseType> {
    return this.http.post<IProduttore>(this.resourceUrl, produttore, { observe: 'response' });
  }

  update(produttore: IProduttore): Observable<EntityResponseType> {
    return this.http.put<IProduttore>(`${this.resourceUrl}/${getProduttoreIdentifier(produttore) as number}`, produttore, {
      observe: 'response',
    });
  }

  partialUpdate(produttore: IProduttore): Observable<EntityResponseType> {
    return this.http.patch<IProduttore>(`${this.resourceUrl}/${getProduttoreIdentifier(produttore) as number}`, produttore, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProduttore>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProduttore[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProduttoreToCollectionIfMissing(
    produttoreCollection: IProduttore[],
    ...produttoresToCheck: (IProduttore | null | undefined)[]
  ): IProduttore[] {
    const produttores: IProduttore[] = produttoresToCheck.filter(isPresent);
    if (produttores.length > 0) {
      const produttoreCollectionIdentifiers = produttoreCollection.map(produttoreItem => getProduttoreIdentifier(produttoreItem)!);
      const produttoresToAdd = produttores.filter(produttoreItem => {
        const produttoreIdentifier = getProduttoreIdentifier(produttoreItem);
        if (produttoreIdentifier == null || produttoreCollectionIdentifiers.includes(produttoreIdentifier)) {
          return false;
        }
        produttoreCollectionIdentifiers.push(produttoreIdentifier);
        return true;
      });
      return [...produttoresToAdd, ...produttoreCollection];
    }
    return produttoreCollection;
  }
}
