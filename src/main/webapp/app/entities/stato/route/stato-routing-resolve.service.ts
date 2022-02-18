import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStato, Stato } from '../stato.model';
import { StatoService } from '../service/stato.service';

@Injectable({ providedIn: 'root' })
export class StatoRoutingResolveService implements Resolve<IStato> {
  constructor(protected service: StatoService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStato> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stato: HttpResponse<Stato>) => {
          if (stato.body) {
            return of(stato.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Stato());
  }
}
