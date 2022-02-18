import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IGestore, Gestore } from '../gestore.model';
import { GestoreService } from '../service/gestore.service';

@Component({
  selector: 'jhi-gestore-update',
  templateUrl: './gestore-update.component.html',
})
export class GestoreUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    idGestore: [],
  });

  constructor(protected gestoreService: GestoreService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ gestore }) => {
      this.updateForm(gestore);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const gestore = this.createFromForm();
    if (gestore.id !== undefined) {
      this.subscribeToSaveResponse(this.gestoreService.update(gestore));
    } else {
      this.subscribeToSaveResponse(this.gestoreService.create(gestore));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGestore>>): void {
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

  protected updateForm(gestore: IGestore): void {
    this.editForm.patchValue({
      id: gestore.id,
      idGestore: gestore.idGestore,
    });
  }

  protected createFromForm(): IGestore {
    return {
      ...new Gestore(),
      id: this.editForm.get(['id'])!.value,
      idGestore: this.editForm.get(['idGestore'])!.value,
    };
  }
}
