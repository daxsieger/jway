import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ITipoEvento, TipoEvento } from '../tipo-evento.model';
import { TipoEventoService } from '../service/tipo-evento.service';

@Component({
  selector: 'jhi-tipo-evento-update',
  templateUrl: './tipo-evento-update.component.html',
})
export class TipoEventoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    idTipoEvento: [],
    dsTipoEvento: [],
  });

  constructor(protected tipoEventoService: TipoEventoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ tipoEvento }) => {
      this.updateForm(tipoEvento);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const tipoEvento = this.createFromForm();
    if (tipoEvento.id !== undefined) {
      this.subscribeToSaveResponse(this.tipoEventoService.update(tipoEvento));
    } else {
      this.subscribeToSaveResponse(this.tipoEventoService.create(tipoEvento));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITipoEvento>>): void {
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

  protected updateForm(tipoEvento: ITipoEvento): void {
    this.editForm.patchValue({
      id: tipoEvento.id,
      idTipoEvento: tipoEvento.idTipoEvento,
      dsTipoEvento: tipoEvento.dsTipoEvento,
    });
  }

  protected createFromForm(): ITipoEvento {
    return {
      ...new TipoEvento(),
      id: this.editForm.get(['id'])!.value,
      idTipoEvento: this.editForm.get(['idTipoEvento'])!.value,
      dsTipoEvento: this.editForm.get(['dsTipoEvento'])!.value,
    };
  }
}
