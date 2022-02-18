import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IAssistito } from '../assistito.model';
import { AssistitoService } from '../service/assistito.service';

@Component({
  templateUrl: './assistito-delete-dialog.component.html',
})
export class AssistitoDeleteDialogComponent {
  assistito?: IAssistito;

  constructor(protected assistitoService: AssistitoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.assistitoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
