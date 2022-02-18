import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IProcesso, getProcessoIdentifier } from '../processo.model';

export type EntityResponseType = HttpResponse<IProcesso>;
export type EntityArrayResponseType = HttpResponse<IProcesso[]>;

@Injectable({ providedIn: 'root' })
export class ProcessoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/processos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(processo: IProcesso): Observable<EntityResponseType> {
    return this.http.post<IProcesso>(this.resourceUrl, processo, { observe: 'response' });
  }

  update(processo: IProcesso): Observable<EntityResponseType> {
    return this.http.put<IProcesso>(`${this.resourceUrl}/${getProcessoIdentifier(processo) as number}`, processo, { observe: 'response' });
  }

  partialUpdate(processo: IProcesso): Observable<EntityResponseType> {
    return this.http.patch<IProcesso>(`${this.resourceUrl}/${getProcessoIdentifier(processo) as number}`, processo, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IProcesso>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IProcesso[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addProcessoToCollectionIfMissing(processoCollection: IProcesso[], ...processosToCheck: (IProcesso | null | undefined)[]): IProcesso[] {
    const processos: IProcesso[] = processosToCheck.filter(isPresent);
    if (processos.length > 0) {
      const processoCollectionIdentifiers = processoCollection.map(processoItem => getProcessoIdentifier(processoItem)!);
      const processosToAdd = processos.filter(processoItem => {
        const processoIdentifier = getProcessoIdentifier(processoItem);
        if (processoIdentifier == null || processoCollectionIdentifiers.includes(processoIdentifier)) {
          return false;
        }
        processoCollectionIdentifiers.push(processoIdentifier);
        return true;
      });
      return [...processosToAdd, ...processoCollection];
    }
    return processoCollection;
  }
}
