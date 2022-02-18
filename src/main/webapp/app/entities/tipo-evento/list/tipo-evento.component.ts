import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ITipoEvento } from '../tipo-evento.model';
import { TipoEventoService } from '../service/tipo-evento.service';
import { TipoEventoDeleteDialogComponent } from '../delete/tipo-evento-delete-dialog.component';

@Component({
  selector: 'jhi-tipo-evento',
  templateUrl: './tipo-evento.component.html',
})
export class TipoEventoComponent implements OnInit {
  tipoEventos?: ITipoEvento[];
  isLoading = false;

  constructor(protected tipoEventoService: TipoEventoService, protected modalService: NgbModal) {}

  loadAll(): void {
    this.isLoading = true;

    this.tipoEventoService.query().subscribe({
      next: (res: HttpResponse<ITipoEvento[]>) => {
        this.isLoading = false;
        this.tipoEventos = res.body ?? [];
      },
      error: () => {
        this.isLoading = false;
      },
    });
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: ITipoEvento): number {
    return item.id!;
  }

  delete(tipoEvento: ITipoEvento): void {
    const modalRef = this.modalService.open(TipoEventoDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.tipoEvento = tipoEvento;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.loadAll();
      }
    });
  }
}
