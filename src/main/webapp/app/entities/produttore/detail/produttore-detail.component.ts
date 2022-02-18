import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IProduttore } from '../produttore.model';

@Component({
  selector: 'jhi-produttore-detail',
  templateUrl: './produttore-detail.component.html',
})
export class ProduttoreDetailComponent implements OnInit {
  produttore: IProduttore | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produttore }) => {
      this.produttore = produttore;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
