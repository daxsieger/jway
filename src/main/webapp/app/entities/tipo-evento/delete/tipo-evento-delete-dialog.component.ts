import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITipoEvento } from '../tipo-evento.model';
import { TipoEventoService } from '../service/tipo-evento.service';

@Component({
  templateUrl: './tipo-evento-delete-dialog.component.html',
})
export class TipoEventoDeleteDialogComponent {
  tipoEvento?: ITipoEvento;

  constructor(protected tipoEventoService: TipoEventoService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.tipoEventoService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
