import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { StadioComponent } from '../list/stadio.component';
import { StadioDetailComponent } from '../detail/stadio-detail.component';
import { StadioUpdateComponent } from '../update/stadio-update.component';
import { StadioRoutingResolveService } from './stadio-routing-resolve.service';

const stadioRoute: Routes = [
  {
    path: '',
    component: StadioComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: StadioDetailComponent,
    resolve: {
      stadio: StadioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: StadioUpdateComponent,
    resolve: {
      stadio: StadioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: StadioUpdateComponent,
    resolve: {
      stadio: StadioRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(stadioRoute)],
  exports: [RouterModule],
})
export class StadioRoutingModule {}
