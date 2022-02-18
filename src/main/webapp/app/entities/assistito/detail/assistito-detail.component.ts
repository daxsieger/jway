import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IAssistito } from '../assistito.model';

@Component({
  selector: 'jhi-assistito-detail',
  templateUrl: './assistito-detail.component.html',
})
export class AssistitoDetailComponent implements OnInit {
  assistito: IAssistito | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ assistito }) => {
      this.assistito = assistito;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
