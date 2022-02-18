import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITransizioni } from '../transizioni.model';

@Component({
  selector: 'jhi-transizioni-detail',
  templateUrl: './transizioni-detail.component.html',
})
export class TransizioniDetailComponent implements OnInit {
  transizioni: ITransizioni | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transizioni }) => {
      this.transizioni = transizioni;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
