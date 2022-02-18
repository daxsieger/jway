import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IStadio } from '../stadio.model';

@Component({
  selector: 'jhi-stadio-detail',
  templateUrl: './stadio-detail.component.html',
})
export class StadioDetailComponent implements OnInit {
  stadio: IStadio | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stadio }) => {
      this.stadio = stadio;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
