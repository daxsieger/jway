import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IProduttore } from '../produttore.model';
import { ProduttoreService } from '../service/produttore.service';

@Component({
  templateUrl: './produttore-delete-dialog.component.html',
})
export class ProduttoreDeleteDialogComponent {
  produttore?: IProduttore;

  constructor(protected produttoreService: ProduttoreService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.produttoreService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
