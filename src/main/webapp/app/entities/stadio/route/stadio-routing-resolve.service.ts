import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IStadio, Stadio } from '../stadio.model';
import { StadioService } from '../service/stadio.service';

@Injectable({ providedIn: 'root' })
export class StadioRoutingResolveService implements Resolve<IStadio> {
  constructor(protected service: StadioService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IStadio> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((stadio: HttpResponse<Stadio>) => {
          if (stadio.body) {
            return of(stadio.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Stadio());
  }
}
