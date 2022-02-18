import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IProduttore, Produttore } from '../produttore.model';
import { ProduttoreService } from '../service/produttore.service';

@Injectable({ providedIn: 'root' })
export class ProduttoreRoutingResolveService implements Resolve<IProduttore> {
  constructor(protected service: ProduttoreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IProduttore> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((produttore: HttpResponse<Produttore>) => {
          if (produttore.body) {
            return of(produttore.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Produttore());
  }
}
