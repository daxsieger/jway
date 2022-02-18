import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { EventoService } from '../service/evento.service';
import { IEvento, Evento } from '../evento.model';
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

import { EventoUpdateComponent } from './evento-update.component';

describe('Evento Management Update Component', () => {
  let comp: EventoUpdateComponent;
  let fixture: ComponentFixture<EventoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let eventoService: EventoService;
  let assistitoService: AssistitoService;
  let tipoEventoService: TipoEventoService;
  let gestoreService: GestoreService;
  let produttoreService: ProduttoreService;
  let statoService: StatoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [EventoUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(EventoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(EventoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    eventoService = TestBed.inject(EventoService);
    assistitoService = TestBed.inject(AssistitoService);
    tipoEventoService = TestBed.inject(TipoEventoService);
    gestoreService = TestBed.inject(GestoreService);
    produttoreService = TestBed.inject(ProduttoreService);
    statoService = TestBed.inject(StatoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call assistito query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const assistito: IAssistito = { id: 40828 };
      evento.assistito = assistito;

      const assistitoCollection: IAssistito[] = [{ id: 12688 }];
      jest.spyOn(assistitoService, 'query').mockReturnValue(of(new HttpResponse({ body: assistitoCollection })));
      const expectedCollection: IAssistito[] = [assistito, ...assistitoCollection];
      jest.spyOn(assistitoService, 'addAssistitoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(assistitoService.query).toHaveBeenCalled();
      expect(assistitoService.addAssistitoToCollectionIfMissing).toHaveBeenCalledWith(assistitoCollection, assistito);
      expect(comp.assistitosCollection).toEqual(expectedCollection);
    });

    it('Should call tipo query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const tipo: ITipoEvento = { id: 26477 };
      evento.tipo = tipo;

      const tipoCollection: ITipoEvento[] = [{ id: 69947 }];
      jest.spyOn(tipoEventoService, 'query').mockReturnValue(of(new HttpResponse({ body: tipoCollection })));
      const expectedCollection: ITipoEvento[] = [tipo, ...tipoCollection];
      jest.spyOn(tipoEventoService, 'addTipoEventoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(tipoEventoService.query).toHaveBeenCalled();
      expect(tipoEventoService.addTipoEventoToCollectionIfMissing).toHaveBeenCalledWith(tipoCollection, tipo);
      expect(comp.tiposCollection).toEqual(expectedCollection);
    });

    it('Should call gestore query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const gestore: IGestore = { id: 37376 };
      evento.gestore = gestore;

      const gestoreCollection: IGestore[] = [{ id: 2624 }];
      jest.spyOn(gestoreService, 'query').mockReturnValue(of(new HttpResponse({ body: gestoreCollection })));
      const expectedCollection: IGestore[] = [gestore, ...gestoreCollection];
      jest.spyOn(gestoreService, 'addGestoreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(gestoreService.query).toHaveBeenCalled();
      expect(gestoreService.addGestoreToCollectionIfMissing).toHaveBeenCalledWith(gestoreCollection, gestore);
      expect(comp.gestoresCollection).toEqual(expectedCollection);
    });

    it('Should call Produttore query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const origine: IProduttore = { id: 47329 };
      evento.origine = origine;

      const produttoreCollection: IProduttore[] = [{ id: 48031 }];
      jest.spyOn(produttoreService, 'query').mockReturnValue(of(new HttpResponse({ body: produttoreCollection })));
      const additionalProduttores = [origine];
      const expectedCollection: IProduttore[] = [...additionalProduttores, ...produttoreCollection];
      jest.spyOn(produttoreService, 'addProduttoreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(produttoreService.query).toHaveBeenCalled();
      expect(produttoreService.addProduttoreToCollectionIfMissing).toHaveBeenCalledWith(produttoreCollection, ...additionalProduttores);
      expect(comp.produttoresSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Stato query and add missing value', () => {
      const evento: IEvento = { id: 456 };
      const statis: IStato[] = [{ id: 4886 }];
      evento.statis = statis;

      const statoCollection: IStato[] = [{ id: 12722 }];
      jest.spyOn(statoService, 'query').mockReturnValue(of(new HttpResponse({ body: statoCollection })));
      const additionalStatoes = [...statis];
      const expectedCollection: IStato[] = [...additionalStatoes, ...statoCollection];
      jest.spyOn(statoService, 'addStatoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(statoService.query).toHaveBeenCalled();
      expect(statoService.addStatoToCollectionIfMissing).toHaveBeenCalledWith(statoCollection, ...additionalStatoes);
      expect(comp.statoesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const evento: IEvento = { id: 456 };
      const assistito: IAssistito = { id: 11963 };
      evento.assistito = assistito;
      const tipo: ITipoEvento = { id: 78250 };
      evento.tipo = tipo;
      const gestore: IGestore = { id: 45022 };
      evento.gestore = gestore;
      const origine: IProduttore = { id: 18956 };
      evento.origine = origine;
      const statis: IStato = { id: 52639 };
      evento.statis = [statis];

      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(evento));
      expect(comp.assistitosCollection).toContain(assistito);
      expect(comp.tiposCollection).toContain(tipo);
      expect(comp.gestoresCollection).toContain(gestore);
      expect(comp.produttoresSharedCollection).toContain(origine);
      expect(comp.statoesSharedCollection).toContain(statis);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evento>>();
      const evento = { id: 123 };
      jest.spyOn(eventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evento }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(eventoService.update).toHaveBeenCalledWith(evento);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evento>>();
      const evento = new Evento();
      jest.spyOn(eventoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: evento }));
      saveSubject.complete();

      // THEN
      expect(eventoService.create).toHaveBeenCalledWith(evento);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Evento>>();
      const evento = { id: 123 };
      jest.spyOn(eventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ evento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(eventoService.update).toHaveBeenCalledWith(evento);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackAssistitoById', () => {
      it('Should return tracked Assistito primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackAssistitoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackTipoEventoById', () => {
      it('Should return tracked TipoEvento primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackTipoEventoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackGestoreById', () => {
      it('Should return tracked Gestore primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackGestoreById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackProduttoreById', () => {
      it('Should return tracked Produttore primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProduttoreById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackStatoById', () => {
      it('Should return tracked Stato primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackStatoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });

  describe('Getting selected relationships', () => {
    describe('getSelectedStato', () => {
      it('Should return option if no Stato is selected', () => {
        const option = { id: 123 };
        const result = comp.getSelectedStato(option);
        expect(result === option).toEqual(true);
      });

      it('Should return selected Stato for according option', () => {
        const option = { id: 123 };
        const selected = { id: 123 };
        const selected2 = { id: 456 };
        const result = comp.getSelectedStato(option, [selected2, selected]);
        expect(result === selected).toEqual(true);
        expect(result === selected2).toEqual(false);
        expect(result === option).toEqual(false);
      });

      it('Should return option if this Stato is not selected', () => {
        const option = { id: 123 };
        const selected = { id: 456 };
        const result = comp.getSelectedStato(option, [selected]);
        expect(result === option).toEqual(true);
        expect(result === selected).toEqual(false);
      });
    });
  });
});
