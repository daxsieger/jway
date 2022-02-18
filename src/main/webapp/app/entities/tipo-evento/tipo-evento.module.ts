import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TipoEventoComponent } from './list/tipo-evento.component';
import { TipoEventoDetailComponent } from './detail/tipo-evento-detail.component';
import { TipoEventoUpdateComponent } from './update/tipo-evento-update.component';
import { TipoEventoDeleteDialogComponent } from './delete/tipo-evento-delete-dialog.component';
import { TipoEventoRoutingModule } from './route/tipo-evento-routing.module';

@NgModule({
  imports: [SharedModule, TipoEventoRoutingModule],
  declarations: [TipoEventoComponent, TipoEventoDetailComponent, TipoEventoUpdateComponent, TipoEventoDeleteDialogComponent],
  entryComponents: [TipoEventoDeleteDialogComponent],
})
export class TipoEventoModule {}
