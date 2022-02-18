import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StatoService } from '../service/stato.service';
import { IStato, Stato } from '../stato.model';
import { IStadio } from 'app/entities/stadio/stadio.model';
import { StadioService } from 'app/entities/stadio/service/stadio.service';

import { StatoUpdateComponent } from './stato-update.component';

describe('Stato Management Update Component', () => {
  let comp: StatoUpdateComponent;
  let fixture: ComponentFixture<StatoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let statoService: StatoService;
  let stadioService: StadioService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StatoUpdateComponent],
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
      .overrideTemplate(StatoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StatoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    statoService = TestBed.inject(StatoService);
    stadioService = TestBed.inject(StadioService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call stadio query and add missing value', () => {
      const stato: IStato = { id: 456 };
      const stadio: IStadio = { id: 91615 };
      stato.stadio = stadio;

      const stadioCollection: IStadio[] = [{ id: 3256 }];
      jest.spyOn(stadioService, 'query').mockReturnValue(of(new HttpResponse({ body: stadioCollection })));
      const expectedCollection: IStadio[] = [stadio, ...stadioCollection];
      jest.spyOn(stadioService, 'addStadioToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stato });
      comp.ngOnInit();

      expect(stadioService.query).toHaveBeenCalled();
      expect(stadioService.addStadioToCollectionIfMissing).toHaveBeenCalledWith(stadioCollection, stadio);
      expect(comp.stadiosCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stato: IStato = { id: 456 };
      const stadio: IStadio = { id: 2615 };
      stato.stadio = stadio;

      activatedRoute.data = of({ stato });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(stato));
      expect(comp.stadiosCollection).toContain(stadio);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stato>>();
      const stato = { id: 123 };
      jest.spyOn(statoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stato });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stato }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(statoService.update).toHaveBeenCalledWith(stato);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stato>>();
      const stato = new Stato();
      jest.spyOn(statoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stato });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stato }));
      saveSubject.complete();

      // THEN
      expect(statoService.create).toHaveBeenCalledWith(stato);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stato>>();
      const stato = { id: 123 };
      jest.spyOn(statoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stato });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(statoService.update).toHaveBeenCalledWith(stato);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackStadioById', () => {
      it('Should return tracked Stadio primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackStadioById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
