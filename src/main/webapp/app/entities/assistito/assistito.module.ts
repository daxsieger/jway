import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { AssistitoComponent } from './list/assistito.component';
import { AssistitoDetailComponent } from './detail/assistito-detail.component';
import { AssistitoUpdateComponent } from './update/assistito-update.component';
import { AssistitoDeleteDialogComponent } from './delete/assistito-delete-dialog.component';
import { AssistitoRoutingModule } from './route/assistito-routing.module';

@NgModule({
  imports: [SharedModule, AssistitoRoutingModule],
  declarations: [AssistitoComponent, AssistitoDetailComponent, AssistitoUpdateComponent, AssistitoDeleteDialogComponent],
  entryComponents: [AssistitoDeleteDialogComponent],
})
export class AssistitoModule {}
