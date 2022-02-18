import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProcesso } from '../processo.model';
import { ProcessoService } from '../service/processo.service';

@Component({
  templateUrl: './processo-delete-dialog.component.html',
})
export class ProcessoDeleteDialogComponent {
  processo?: IProcesso;

  constructor(protected processoService: ProcessoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.processoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
