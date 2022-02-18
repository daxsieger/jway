import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { ProduttoreComponent } from './list/produttore.component';
import { ProduttoreDetailComponent } from './detail/produttore-detail.component';
import { ProduttoreUpdateComponent } from './update/produttore-update.component';
import { ProduttoreDeleteDialogComponent } from './delete/produttore-delete-dialog.component';
import { ProduttoreRoutingModule } from './route/produttore-routing.module';

@NgModule({
  imports: [SharedModule, ProduttoreRoutingModule],
  declarations: [ProduttoreComponent, ProduttoreDetailComponent, ProduttoreUpdateComponent, ProduttoreDeleteDialogComponent],
  entryComponents: [ProduttoreDeleteDialogComponent],
})
export class ProduttoreModule {}
