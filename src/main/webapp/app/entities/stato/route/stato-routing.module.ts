import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StatoComponent } from '../list/stato.component';
import { StatoDetailComponent } from '../detail/stato-detail.component';
import { StatoUpdateComponent } from '../update/stato-update.component';
import { StatoRoutingResolveService } from './stato-routing-resolve.service';

const statoRoute: Routes = [
  {
    path: '',
    component: StatoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StatoDetailComponent,
    resolve: {
      stato: StatoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StatoUpdateComponent,
    resolve: {
      stato: StatoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StatoUpdateComponent,
    resolve: {
      stato: StatoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(statoRoute)],
  exports: [RouterModule],
})
export class StatoRoutingModule {}
