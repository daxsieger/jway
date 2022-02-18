import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StatoComponent } from './list/stato.component';
import { StatoDetailComponent } from './detail/stato-detail.component';
import { StatoUpdateComponent } from './update/stato-update.component';
import { StatoDeleteDialogComponent } from './delete/stato-delete-dialog.component';
import { StatoRoutingModule } from './route/stato-routing.module';

@NgModule({
  imports: [SharedModule, StatoRoutingModule],
  declarations: [StatoComponent, StatoDetailComponent, StatoUpdateComponent, StatoDeleteDialogComponent],
  entryComponents: [StatoDeleteDialogComponent],
})
export class StatoModule {}
