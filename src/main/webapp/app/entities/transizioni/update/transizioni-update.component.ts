import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITransizioni, Transizioni } from '../transizioni.model';
import { TransizioniService } from '../service/transizioni.service';
import { IProcesso } from 'app/entities/processo/processo.model';
import { ProcessoService } from 'app/entities/processo/service/processo.service';
import { IStadio } from 'app/entities/stadio/stadio.model';
import { StadioService } from 'app/entities/stadio/service/stadio.service';

@Component({
  selector: 'jhi-transizioni-update',
  templateUrl: './transizioni-update.component.html',
})
export class TransizioniUpdateComponent implements OnInit {
  isSaving = false;

  processosSharedCollection: IProcesso[] = [];
  stadiosSharedCollection: IStadio[] = [];

  editForm = this.fb.group({
    id: [],
    idTransizione: [],
    dsTransizione: [],
    processo: [],
    stadioIniziale: [],
    stadioFinale: [],
  });

  constructor(
    protected transizioniService: TransizioniService,
    protected processoService: ProcessoService,
    protected stadioService: StadioService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ transizioni }) => {
      this.updateForm(transizioni);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const transizioni = this.createFromForm();
    if (transizioni.id !== undefined) {
      this.subscribeToSaveResponse(this.transizioniService.update(transizioni));
    } else {
      this.subscribeToSaveResponse(this.transizioniService.create(transizioni));
    }
  }

  trackProcessoById(index: number, item: IProcesso): number {
    return item.id!;
  }

  trackStadioById(index: number, item: IStadio): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITransizioni>>): void {
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

  protected updateForm(transizioni: ITransizioni): void {
    this.editForm.patchValue({
      id: transizioni.id,
      idTransizione: transizioni.idTransizione,
      dsTransizione: transizioni.dsTransizione,
      processo: transizioni.processo,
      stadioIniziale: transizioni.stadioIniziale,
      stadioFinale: transizioni.stadioFinale,
    });

    this.processosSharedCollection = this.processoService.addProcessoToCollectionIfMissing(
      this.processosSharedCollection,
      transizioni.processo
    );
    this.stadiosSharedCollection = this.stadioService.addStadioToCollectionIfMissing(
      this.stadiosSharedCollection,
      transizioni.stadioIniziale,
      transizioni.stadioFinale
    );
  }

  protected loadRelationshipsOptions(): void {
    this.processoService
      .query()
      .pipe(map((res: HttpResponse<IProcesso[]>) => res.body ?? []))
      .pipe(
        map((processos: IProcesso[]) =>
          this.processoService.addProcessoToCollectionIfMissing(processos, this.editForm.get('processo')!.value)
        )
      )
      .subscribe((processos: IProcesso[]) => (this.processosSharedCollection = processos));

    this.stadioService
      .query()
      .pipe(map((res: HttpResponse<IStadio[]>) => res.body ?? []))
      .pipe(
        map((stadios: IStadio[]) =>
          this.stadioService.addStadioToCollectionIfMissing(
            stadios,
            this.editForm.get('stadioIniziale')!.value,
            this.editForm.get('stadioFinale')!.value
          )
        )
      )
      .subscribe((stadios: IStadio[]) => (this.stadiosSharedCollection = stadios));
  }

  protected createFromForm(): ITransizioni {
    return {
      ...new Transizioni(),
      id: this.editForm.get(['id'])!.value,
      idTransizione: this.editForm.get(['idTransizione'])!.value,
      dsTransizione: this.editForm.get(['dsTransizione'])!.value,
      processo: this.editForm.get(['processo'])!.value,
      stadioIniziale: this.editForm.get(['stadioIniziale'])!.value,
      stadioFinale: this.editForm.get(['stadioFinale'])!.value,
    };
  }
}
