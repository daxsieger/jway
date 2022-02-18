import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TransizioniComponent } from '../list/transizioni.component';
import { TransizioniDetailComponent } from '../detail/transizioni-detail.component';
import { TransizioniUpdateComponent } from '../update/transizioni-update.component';
import { TransizioniRoutingResolveService } from './transizioni-routing-resolve.service';

const transizioniRoute: Routes = [
  {
    path: '',
    component: TransizioniComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TransizioniDetailComponent,
    resolve: {
      transizioni: TransizioniRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TransizioniUpdateComponent,
    resolve: {
      transizioni: TransizioniRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TransizioniUpdateComponent,
    resolve: {
      transizioni: TransizioniRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(transizioniRoute)],
  exports: [RouterModule],
})
export class TransizioniRoutingModule {}
