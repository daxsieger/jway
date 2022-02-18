import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TransizioniComponent } from './list/transizioni.component';
import { TransizioniDetailComponent } from './detail/transizioni-detail.component';
import { TransizioniUpdateComponent } from './update/transizioni-update.component';
import { TransizioniDeleteDialogComponent } from './delete/transizioni-delete-dialog.component';
import { TransizioniRoutingModule } from './route/transizioni-routing.module';

@NgModule({
  imports: [SharedModule, TransizioniRoutingModule],
  declarations: [TransizioniComponent, TransizioniDetailComponent, TransizioniUpdateComponent, TransizioniDeleteDialogComponent],
  entryComponents: [TransizioniDeleteDialogComponent],
})
export class TransizioniModule {}
