import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IStadio, Stadio } from '../stadio.model';
import { StadioService } from '../service/stadio.service';
import { IProcesso } from 'app/entities/processo/processo.model';
import { ProcessoService } from 'app/entities/processo/service/processo.service';

@Component({
  selector: 'jhi-stadio-update',
  templateUrl: './stadio-update.component.html',
})
export class StadioUpdateComponent implements OnInit {
  isSaving = false;

  processosSharedCollection: IProcesso[] = [];

  editForm = this.fb.group({
    id: [],
    idStadio: [],
    dsStadio: [],
    processo: [],
  });

  constructor(
    protected stadioService: StadioService,
    protected processoService: ProcessoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ stadio }) => {
      this.updateForm(stadio);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const stadio = this.createFromForm();
    if (stadio.id !== undefined) {
      this.subscribeToSaveResponse(this.stadioService.update(stadio));
    } else {
      this.subscribeToSaveResponse(this.stadioService.create(stadio));
    }
  }

  trackProcessoById(index: number, item: IProcesso): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IStadio>>): void {
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

  protected updateForm(stadio: IStadio): void {
    this.editForm.patchValue({
      id: stadio.id,
      idStadio: stadio.idStadio,
      dsStadio: stadio.dsStadio,
      processo: stadio.processo,
    });

    this.processosSharedCollection = this.processoService.addProcessoToCollectionIfMissing(this.processosSharedCollection, stadio.processo);
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
  }

  protected createFromForm(): IStadio {
    return {
      ...new Stadio(),
      id: this.editForm.get(['id'])!.value,
      idStadio: this.editForm.get(['idStadio'])!.value,
      dsStadio: this.editForm.get(['dsStadio'])!.value,
      processo: this.editForm.get(['processo'])!.value,
    };
  }
}
