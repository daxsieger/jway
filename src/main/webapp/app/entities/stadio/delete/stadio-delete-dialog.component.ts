import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IStadio } from '../stadio.model';
import { StadioService } from '../service/stadio.service';

@Component({
  templateUrl: './stadio-delete-dialog.component.html',
})
export class StadioDeleteDialogComponent {
  stadio?: IStadio;

  constructor(protected stadioService: StadioService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.stadioService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
