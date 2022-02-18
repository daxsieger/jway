import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITransizioni, Transizioni } from '../transizioni.model';
import { TransizioniService } from '../service/transizioni.service';

@Injectable({ providedIn: 'root' })
export class TransizioniRoutingResolveService implements Resolve<ITransizioni> {
  constructor(protected service: TransizioniService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITransizioni> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((transizioni: HttpResponse<Transizioni>) => {
          if (transizioni.body) {
            return of(transizioni.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Transizioni());
  }
}
