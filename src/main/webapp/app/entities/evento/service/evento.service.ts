import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEvento, getEventoIdentifier } from '../evento.model';

export type EntityResponseType = HttpResponse<IEvento>;
export type EntityArrayResponseType = HttpResponse<IEvento[]>;

@Injectable({ providedIn: 'root' })
export class EventoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/eventos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(evento: IEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evento);
    return this.http
      .post<IEvento>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(evento: IEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evento);
    return this.http
      .put<IEvento>(`${this.resourceUrl}/${getEventoIdentifier(evento) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(evento: IEvento): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(evento);
    return this.http
      .patch<IEvento>(`${this.resourceUrl}/${getEventoIdentifier(evento) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEvento>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEvento[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEventoToCollectionIfMissing(eventoCollection: IEvento[], ...eventosToCheck: (IEvento | null | undefined)[]): IEvento[] {
    const eventos: IEvento[] = eventosToCheck.filter(isPresent);
    if (eventos.length > 0) {
      const eventoCollectionIdentifiers = eventoCollection.map(eventoItem => getEventoIdentifier(eventoItem)!);
      const eventosToAdd = eventos.filter(eventoItem => {
        const eventoIdentifier = getEventoIdentifier(eventoItem);
        if (eventoIdentifier == null || eventoCollectionIdentifiers.includes(eventoIdentifier)) {
          return false;
        }
        eventoCollectionIdentifiers.push(eventoIdentifier);
        return true;
      });
      return [...eventosToAdd, ...eventoCollection];
    }
    return eventoCollection;
  }

  protected convertDateFromClient(evento: IEvento): IEvento {
    return Object.assign({}, evento, {
      tsEvento: evento.tsEvento?.isValid() ? evento.tsEvento.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.tsEvento = res.body.tsEvento ? dayjs(res.body.tsEvento) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((evento: IEvento) => {
        evento.tsEvento = evento.tsEvento ? dayjs(evento.tsEvento) : undefined;
      });
    }
    return res;
  }
}
