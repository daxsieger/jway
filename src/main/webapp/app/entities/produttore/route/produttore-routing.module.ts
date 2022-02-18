import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { ProduttoreComponent } from '../list/produttore.component';
import { ProduttoreDetailComponent } from '../detail/produttore-detail.component';
import { ProduttoreUpdateComponent } from '../update/produttore-update.component';
import { ProduttoreRoutingResolveService } from './produttore-routing-resolve.service';

const produttoreRoute: Routes = [
  {
    path: '',
    component: ProduttoreComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: ProduttoreDetailComponent,
    resolve: {
      produttore: ProduttoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: ProduttoreUpdateComponent,
    resolve: {
      produttore: ProduttoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: ProduttoreUpdateComponent,
    resolve: {
      produttore: ProduttoreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(produttoreRoute)],
  exports: [RouterModule],
})
export class ProduttoreRoutingModule {}
