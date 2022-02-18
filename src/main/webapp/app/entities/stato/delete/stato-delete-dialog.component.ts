import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStato } from '../stato.model';
import { StatoService } from '../service/stato.service';

@Component({
  templateUrl: './stato-delete-dialog.component.html',
})
export class StatoDeleteDialogComponent {
  stato?: IStato;

  constructor(protected statoService: StatoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.statoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
