import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IProduttore, Produttore } from '../produttore.model';
import { ProduttoreService } from '../service/produttore.service';

@Component({
  selector: 'jhi-produttore-update',
  templateUrl: './produttore-update.component.html',
})
export class ProduttoreUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    idProduttore: [],
    dsProduttore: [],
  });

  constructor(protected produttoreService: ProduttoreService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produttore }) => {
      this.updateForm(produttore);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const produttore = this.createFromForm();
    if (produttore.id !== undefined) {
      this.subscribeToSaveResponse(this.produttoreService.update(produttore));
    } else {
      this.subscribeToSaveResponse(this.produttoreService.create(produttore));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduttore>>): void {
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

  protected updateForm(produttore: IProduttore): void {
    this.editForm.patchValue({
      id: produttore.id,
      idProduttore: produttore.idProduttore,
      dsProduttore: produttore.dsProduttore,
    });
  }

  protected createFromForm(): IProduttore {
    return {
      ...new Produttore(),
      id: this.editForm.get(['id'])!.value,
      idProduttore: this.editForm.get(['idProduttore'])!.value,
      dsProduttore: this.editForm.get(['dsProduttore'])!.value,
    };
  }
}
