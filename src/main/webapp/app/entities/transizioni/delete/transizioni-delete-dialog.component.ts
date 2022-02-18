import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITransizioni } from '../transizioni.model';
import { TransizioniService } from '../service/transizioni.service';

@Component({
  templateUrl: './transizioni-delete-dialog.component.html',
})
export class TransizioniDeleteDialogComponent {
  transizioni?: ITransizioni;

  constructor(protected transizioniService: TransizioniService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.transizioniService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
