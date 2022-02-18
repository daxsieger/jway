import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AssistitoComponent } from '../list/assistito.component';
import { AssistitoDetailComponent } from '../detail/assistito-detail.component';
import { AssistitoUpdateComponent } from '../update/assistito-update.component';
import { AssistitoRoutingResolveService } from './assistito-routing-resolve.service';

const assistitoRoute: Routes = [
  {
    path: '',
    component: AssistitoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AssistitoDetailComponent,
    resolve: {
      assistito: AssistitoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AssistitoUpdateComponent,
    resolve: {
      assistito: AssistitoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AssistitoUpdateComponent,
    resolve: {
      assistito: AssistitoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(assistitoRoute)],
  exports: [RouterModule],
})
export class AssistitoRoutingModule {}
