import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { StadioComponent } from './list/stadio.component';
import { StadioDetailComponent } from './detail/stadio-detail.component';
import { StadioUpdateComponent } from './update/stadio-update.component';
import { StadioDeleteDialogComponent } from './delete/stadio-delete-dialog.component';
import { StadioRoutingModule } from './route/stadio-routing.module';

@NgModule({
  imports: [SharedModule, StadioRoutingModule],
  declarations: [StadioComponent, StadioDetailComponent, StadioUpdateComponent, StadioDeleteDialogComponent],
  entryComponents: [StadioDeleteDialogComponent],
})
export class StadioModule {}
