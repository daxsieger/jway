import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITipoEvento, TipoEvento } from '../tipo-evento.model';
import { TipoEventoService } from '../service/tipo-evento.service';

@Injectable({ providedIn: 'root' })
export class TipoEventoRoutingResolveService implements Resolve<ITipoEvento> {
  constructor(protected service: TipoEventoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITipoEvento> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((tipoEvento: HttpResponse<TipoEvento>) => {
          if (tipoEvento.body) {
            return of(tipoEvento.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TipoEvento());
  }
}
