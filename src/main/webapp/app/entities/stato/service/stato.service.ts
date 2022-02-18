import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IStato, getStatoIdentifier } from '../stato.model';

export type EntityResponseType = HttpResponse<IStato>;
export type EntityArrayResponseType = HttpResponse<IStato[]>;

@Injectable({ providedIn: 'root' })
export class StatoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/statoes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(stato: IStato): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stato);
    return this.http
      .post<IStato>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(stato: IStato): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stato);
    return this.http
      .put<IStato>(`${this.resourceUrl}/${getStatoIdentifier(stato) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(stato: IStato): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(stato);
    return this.http
      .patch<IStato>(`${this.resourceUrl}/${getStatoIdentifier(stato) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IStato>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IStato[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addStatoToCollectionIfMissing(statoCollection: IStato[], ...statoesToCheck: (IStato | null | undefined)[]): IStato[] {
    const statoes: IStato[] = statoesToCheck.filter(isPresent);
    if (statoes.length > 0) {
      const statoCollectionIdentifiers = statoCollection.map(statoItem => getStatoIdentifier(statoItem)!);
      const statoesToAdd = statoes.filter(statoItem => {
        const statoIdentifier = getStatoIdentifier(statoItem);
        if (statoIdentifier == null || statoCollectionIdentifiers.includes(statoIdentifier)) {
          return false;
        }
        statoCollectionIdentifiers.push(statoIdentifier);
        return true;
      });
      return [...statoesToAdd, ...statoCollection];
    }
    return statoCollection;
  }

  protected convertDateFromClient(stato: IStato): IStato {
    return Object.assign({}, stato, {
      tsCambioStato: stato.tsCambioStato?.isValid() ? stato.tsCambioStato.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.tsCambioStato = res.body.tsCambioStato ? dayjs(res.body.tsCambioStato) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((stato: IStato) => {
        stato.tsCambioStato = stato.tsCambioStato ? dayjs(stato.tsCambioStato) : undefined;
      });
    }
    return res;
  }
}
