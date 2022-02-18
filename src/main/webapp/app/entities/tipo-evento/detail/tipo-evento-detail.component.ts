import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITipoEvento } from '../tipo-evento.model';

@Component({
  selector: 'jhi-tipo-evento-detail',
  templateUrl: './tipo-evento-detail.component.html',
})
export class TipoEventoDetailComponent implements OnInit {
  tipoEvento: ITipoEvento | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tipoEvento }) => {
      this.tipoEvento = tipoEvento;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
