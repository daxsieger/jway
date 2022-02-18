import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TransizioniService } from '../service/transizioni.service';
import { ITransizioni, Transizioni } from '../transizioni.model';
import { IProcesso } from 'app/entities/processo/processo.model';
import { ProcessoService } from 'app/entities/processo/service/processo.service';
import { IStadio } from 'app/entities/stadio/stadio.model';
import { StadioService } from 'app/entities/stadio/service/stadio.service';

import { TransizioniUpdateComponent } from './transizioni-update.component';

describe('Transizioni Management Update Component', () => {
  let comp: TransizioniUpdateComponent;
  let fixture: ComponentFixture<TransizioniUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let transizioniService: TransizioniService;
  let processoService: ProcessoService;
  let stadioService: StadioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TransizioniUpdateComponent],
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
      .overrideTemplate(TransizioniUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TransizioniUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    transizioniService = TestBed.inject(TransizioniService);
    processoService = TestBed.inject(ProcessoService);
    stadioService = TestBed.inject(StadioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Processo query and add missing value', () => {
      const transizioni: ITransizioni = { id: 456 };
      const processo: IProcesso = { id: 85999 };
      transizioni.processo = processo;

      const processoCollection: IProcesso[] = [{ id: 60603 }];
      jest.spyOn(processoService, 'query').mockReturnValue(of(new HttpResponse({ body: processoCollection })));
      const additionalProcessos = [processo];
      const expectedCollection: IProcesso[] = [...additionalProcessos, ...processoCollection];
      jest.spyOn(processoService, 'addProcessoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      expect(processoService.query).toHaveBeenCalled();
      expect(processoService.addProcessoToCollectionIfMissing).toHaveBeenCalledWith(processoCollection, ...additionalProcessos);
      expect(comp.processosSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Stadio query and add missing value', () => {
      const transizioni: ITransizioni = { id: 456 };
      const stadioIniziale: IStadio = { id: 72315 };
      transizioni.stadioIniziale = stadioIniziale;
      const stadioFinale: IStadio = { id: 12037 };
      transizioni.stadioFinale = stadioFinale;

      const stadioCollection: IStadio[] = [{ id: 17469 }];
      jest.spyOn(stadioService, 'query').mockReturnValue(of(new HttpResponse({ body: stadioCollection })));
      const additionalStadios = [stadioIniziale, stadioFinale];
      const expectedCollection: IStadio[] = [...additionalStadios, ...stadioCollection];
      jest.spyOn(stadioService, 'addStadioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      expect(stadioService.query).toHaveBeenCalled();
      expect(stadioService.addStadioToCollectionIfMissing).toHaveBeenCalledWith(stadioCollection, ...additionalStadios);
      expect(comp.stadiosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const transizioni: ITransizioni = { id: 456 };
      const processo: IProcesso = { id: 61886 };
      transizioni.processo = processo;
      const stadioIniziale: IStadio = { id: 19170 };
      transizioni.stadioIniziale = stadioIniziale;
      const stadioFinale: IStadio = { id: 74588 };
      transizioni.stadioFinale = stadioFinale;

      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(transizioni));
      expect(comp.processosSharedCollection).toContain(processo);
      expect(comp.stadiosSharedCollection).toContain(stadioIniziale);
      expect(comp.stadiosSharedCollection).toContain(stadioFinale);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transizioni>>();
      const transizioni = { id: 123 };
      jest.spyOn(transizioniService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transizioni }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(transizioniService.update).toHaveBeenCalledWith(transizioni);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transizioni>>();
      const transizioni = new Transizioni();
      jest.spyOn(transizioniService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: transizioni }));
      saveSubject.complete();

      // THEN
      expect(transizioniService.create).toHaveBeenCalledWith(transizioni);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Transizioni>>();
      const transizioni = { id: 123 };
      jest.spyOn(transizioniService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ transizioni });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(transizioniService.update).toHaveBeenCalledWith(transizioni);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackProcessoById', () => {
      it('Should return tracked Processo primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackProcessoById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });

    describe('trackStadioById', () => {
      it('Should return tracked Stadio primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackStadioById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
