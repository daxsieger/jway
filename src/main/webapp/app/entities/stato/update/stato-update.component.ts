import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IStato, Stato } from '../stato.model';
import { StatoService } from '../service/stato.service';
import { IStadio } from 'app/entities/stadio/stadio.model';
import { StadioService } from 'app/entities/stadio/service/stadio.service';

@Component({
  selector: 'jhi-stato-update',
  templateUrl: './stato-update.component.html',
})
export class StatoUpdateComponent implements OnInit {
  isSaving = false;

  stadiosCollection: IStadio[] = [];

  editForm = this.fb.group({
    id: [],
    idStadio: [],
    dsStadio: [],
    tsCambioStato: [],
    stadio: [],
  });

  constructor(
    protected statoService: StatoService,
    protected stadioService: StadioService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stato }) => {
      if (stato.id === undefined) {
        const today = dayjs().startOf('day');
        stato.tsCambioStato = today;
      }

      this.updateForm(stato);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stato = this.createFromForm();
    if (stato.id !== undefined) {
      this.subscribeToSaveResponse(this.statoService.update(stato));
    } else {
      this.subscribeToSaveResponse(this.statoService.create(stato));
    }
  }

  trackStadioById(index: number, item: IStadio): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStato>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(stato: IStato): void {
    this.editForm.patchValue({
      id: stato.id,
      idStadio: stato.idStadio,
      dsStadio: stato.dsStadio,
      tsCambioStato: stato.tsCambioStato ? stato.tsCambioStato.format(DATE_TIME_FORMAT) : null,
      stadio: stato.stadio,
    });

    this.stadiosCollection = this.stadioService.addStadioToCollectionIfMissing(this.stadiosCollection, stato.stadio);
  }

  protected loadRelationshipsOptions(): void {
    this.stadioService
      .query({ filter: 'stato-is-null' })
      .pipe(map((res: HttpResponse<IStadio[]>) => res.body ?? []))
      .pipe(map((stadios: IStadio[]) => this.stadioService.addStadioToCollectionIfMissing(stadios, this.editForm.get('stadio')!.value)))
      .subscribe((stadios: IStadio[]) => (this.stadiosCollection = stadios));
  }

  protected createFromForm(): IStato {
    return {
      ...new Stato(),
      id: this.editForm.get(['id'])!.value,
      idStadio: this.editForm.get(['idStadio'])!.value,
      dsStadio: this.editForm.get(['dsStadio'])!.value,
      tsCambioStato: this.editForm.get(['tsCambioStato'])!.value
        ? dayjs(this.editForm.get(['tsCambioStato'])!.value, DATE_TIME_FORMAT)
        : undefined,
      stadio: this.editForm.get(['stadio'])!.value,
    };
  }
}
