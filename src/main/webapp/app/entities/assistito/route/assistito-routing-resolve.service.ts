import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IAssistito, Assistito } from '../assistito.model';
import { AssistitoService } from '../service/assistito.service';

@Injectable({ providedIn: 'root' })
export class AssistitoRoutingResolveService implements Resolve<IAssistito> {
  constructor(protected service: AssistitoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IAssistito> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((assistito: HttpResponse<Assistito>) => {
          if (assistito.body) {
            return of(assistito.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Assistito());
  }
}
