import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAssistito, Assistito } from '../assistito.model';
import { AssistitoService } from '../service/assistito.service';

@Component({
  selector: 'jhi-assistito-update',
  templateUrl: './assistito-update.component.html',
})
export class AssistitoUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    idAssistito: [],
  });

  constructor(protected assistitoService: AssistitoService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ assistito }) => {
      this.updateForm(assistito);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const assistito = this.createFromForm();
    if (assistito.id !== undefined) {
      this.subscribeToSaveResponse(this.assistitoService.update(assistito));
    } else {
      this.subscribeToSaveResponse(this.assistitoService.create(assistito));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAssistito>>): void {
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

  protected updateForm(assistito: IAssistito): void {
    this.editForm.patchValue({
      id: assistito.id,
      idAssistito: assistito.idAssistito,
    });
  }

  protected createFromForm(): IAssistito {
    return {
      ...new Assistito(),
      id: this.editForm.get(['id'])!.value,
      idAssistito: this.editForm.get(['idAssistito'])!.value,
    };
  }
}
