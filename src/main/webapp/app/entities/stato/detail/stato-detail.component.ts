import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStato } from '../stato.model';

@Component({
  selector: 'jhi-stato-detail',
  templateUrl: './stato-detail.component.html',
})
export class StatoDetailComponent implements OnInit {
  stato: IStato | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stato }) => {
      this.stato = stato;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
