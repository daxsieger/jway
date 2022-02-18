import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TipoEventoComponent } from '../list/tipo-evento.component';
import { TipoEventoDetailComponent } from '../detail/tipo-evento-detail.component';
import { TipoEventoUpdateComponent } from '../update/tipo-evento-update.component';
import { TipoEventoRoutingResolveService } from './tipo-evento-routing-resolve.service';

const tipoEventoRoute: Routes = [
  {
    path: '',
    component: TipoEventoComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TipoEventoDetailComponent,
    resolve: {
      tipoEvento: TipoEventoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TipoEventoUpdateComponent,
    resolve: {
      tipoEvento: TipoEventoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TipoEventoUpdateComponent,
    resolve: {
      tipoEvento: TipoEventoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(tipoEventoRoute)],
  exports: [RouterModule],
})
export class TipoEventoRoutingModule {}
