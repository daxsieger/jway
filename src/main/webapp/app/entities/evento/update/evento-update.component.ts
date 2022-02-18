import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IEvento, Evento } from '../evento.model';
import { EventoService } from '../service/evento.service';
import { IAssistito } from 'app/entities/assistito/assistito.model';
import { AssistitoService } from 'app/entities/assistito/service/assistito.service';
import { ITipoEvento } from 'app/entities/tipo-evento/tipo-evento.model';
import { TipoEventoService } from 'app/entities/tipo-evento/service/tipo-evento.service';
import { IGestore } from 'app/entities/gestore/gestore.model';
import { GestoreService } from 'app/entities/gestore/service/gestore.service';
import { IProduttore } from 'app/entities/produttore/produttore.model';
import { ProduttoreService } from 'app/entities/produttore/service/produttore.service';
import { IStato } from 'app/entities/stato/stato.model';
import { StatoService } from 'app/entities/stato/service/stato.service';

@Component({
  selector: 'jhi-evento-update',
  templateUrl: './evento-update.component.html',
})
export class EventoUpdateComponent implements OnInit {
  isSaving = false;

  assistitosCollection: IAssistito[] = [];
  tiposCollection: ITipoEvento[] = [];
  gestoresCollection: IGestore[] = [];
  produttoresSharedCollection: IProduttore[] = [];
  statoesSharedCollection: IStato[] = [];

  editForm = this.fb.group({
    id: [],
    idEvento: [],
    tsEvento: [],
    note: [],
    assistito: [],
    tipo: [],
    gestore: [],
    origine: [],
    statis: [],
  });

  constructor(
    protected eventoService: EventoService,
    protected assistitoService: AssistitoService,
    protected tipoEventoService: TipoEventoService,
    protected gestoreService: GestoreService,
    protected produttoreService: ProduttoreService,
    protected statoService: StatoService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ evento }) => {
      if (evento.id === undefined) {
        const today = dayjs().startOf('day');
        evento.tsEvento = today;
      }

      this.updateForm(evento);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const evento = this.createFromForm();
    if (evento.id !== undefined) {
      this.subscribeToSaveResponse(this.eventoService.update(evento));
    } else {
      this.subscribeToSaveResponse(this.eventoService.create(evento));
    }
  }

  trackAssistitoById(index: number, item: IAssistito): number {
    return item.id!;
  }

  trackTipoEventoById(index: number, item: ITipoEvento): number {
    return item.id!;
  }

  trackGestoreById(index: number, item: IGestore): number {
    return item.id!;
  }

  trackProduttoreById(index: number, item: IProduttore): number {
    return item.id!;
  }

  trackStatoById(index: number, item: IStato): number {
    return item.id!;
  }

  getSelectedStato(option: IStato, selectedVals?: IStato[]): IStato {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IEvento>>): void {
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

  protected updateForm(evento: IEvento): void {
    this.editForm.patchValue({
      id: evento.id,
      idEvento: evento.idEvento,
      tsEvento: evento.tsEvento ? evento.tsEvento.format(DATE_TIME_FORMAT) : null,
      note: evento.note,
      assistito: evento.assistito,
      tipo: evento.tipo,
      gestore: evento.gestore,
      origine: evento.origine,
      statis: evento.statis,
    });

    this.assistitosCollection = this.assistitoService.addAssistitoToCollectionIfMissing(this.assistitosCollection, evento.assistito);
    this.tiposCollection = this.tipoEventoService.addTipoEventoToCollectionIfMissing(this.tiposCollection, evento.tipo);
    this.gestoresCollection = this.gestoreService.addGestoreToCollectionIfMissing(this.gestoresCollection, evento.gestore);
    this.produttoresSharedCollection = this.produttoreService.addProduttoreToCollectionIfMissing(
      this.produttoresSharedCollection,
      evento.origine
    );
    this.statoesSharedCollection = this.statoService.addStatoToCollectionIfMissing(this.statoesSharedCollection, ...(evento.statis ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.assistitoService
      .query({ filter: 'evento-is-null' })
      .pipe(map((res: HttpResponse<IAssistito[]>) => res.body ?? []))
      .pipe(
        map((assistitos: IAssistito[]) =>
          this.assistitoService.addAssistitoToCollectionIfMissing(assistitos, this.editForm.get('assistito')!.value)
        )
      )
      .subscribe((assistitos: IAssistito[]) => (this.assistitosCollection = assistitos));

    this.tipoEventoService
      .query({ filter: 'evento-is-null' })
      .pipe(map((res: HttpResponse<ITipoEvento[]>) => res.body ?? []))
      .pipe(
        map((tipoEventos: ITipoEvento[]) =>
          this.tipoEventoService.addTipoEventoToCollectionIfMissing(tipoEventos, this.editForm.get('tipo')!.value)
        )
      )
      .subscribe((tipoEventos: ITipoEvento[]) => (this.tiposCollection = tipoEventos));

    this.gestoreService
      .query({ filter: 'evento-is-null' })
      .pipe(map((res: HttpResponse<IGestore[]>) => res.body ?? []))
      .pipe(
        map((gestores: IGestore[]) => this.gestoreService.addGestoreToCollectionIfMissing(gestores, this.editForm.get('gestore')!.value))
      )
      .subscribe((gestores: IGestore[]) => (this.gestoresCollection = gestores));

    this.produttoreService
      .query()
      .pipe(map((res: HttpResponse<IProduttore[]>) => res.body ?? []))
      .pipe(
        map((produttores: IProduttore[]) =>
          this.produttoreService.addProduttoreToCollectionIfMissing(produttores, this.editForm.get('origine')!.value)
        )
      )
      .subscribe((produttores: IProduttore[]) => (this.produttoresSharedCollection = produttores));

    this.statoService
      .query()
      .pipe(map((res: HttpResponse<IStato[]>) => res.body ?? []))
      .pipe(
        map((statoes: IStato[]) => this.statoService.addStatoToCollectionIfMissing(statoes, ...(this.editForm.get('statis')!.value ?? [])))
      )
      .subscribe((statoes: IStato[]) => (this.statoesSharedCollection = statoes));
  }

  protected createFromForm(): IEvento {
    return {
      ...new Evento(),
      id: this.editForm.get(['id'])!.value,
      idEvento: this.editForm.get(['idEvento'])!.value,
      tsEvento: this.editForm.get(['tsEvento'])!.value ? dayjs(this.editForm.get(['tsEvento'])!.value, DATE_TIME_FORMAT) : undefined,
      note: this.editForm.get(['note'])!.value,
      assistito: this.editForm.get(['assistito'])!.value,
      tipo: this.editForm.get(['tipo'])!.value,
      gestore: this.editForm.get(['gestore'])!.value,
      origine: this.editForm.get(['origine'])!.value,
      statis: this.editForm.get(['statis'])!.value,
    };
  }
}
