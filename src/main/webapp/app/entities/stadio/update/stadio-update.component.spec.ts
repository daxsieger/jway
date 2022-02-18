import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { StadioService } from '../service/stadio.service';
import { IStadio, Stadio } from '../stadio.model';
import { IProcesso } from 'app/entities/processo/processo.model';
import { ProcessoService } from 'app/entities/processo/service/processo.service';

import { StadioUpdateComponent } from './stadio-update.component';

describe('Stadio Management Update Component', () => {
  let comp: StadioUpdateComponent;
  let fixture: ComponentFixture<StadioUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let stadioService: StadioService;
  let processoService: ProcessoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [StadioUpdateComponent],
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
      .overrideTemplate(StadioUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(StadioUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    stadioService = TestBed.inject(StadioService);
    processoService = TestBed.inject(ProcessoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Processo query and add missing value', () => {
      const stadio: IStadio = { id: 456 };
      const processo: IProcesso = { id: 57538 };
      stadio.processo = processo;

      const processoCollection: IProcesso[] = [{ id: 82157 }];
      jest.spyOn(processoService, 'query').mockReturnValue(of(new HttpResponse({ body: processoCollection })));
      const additionalProcessos = [processo];
      const expectedCollection: IProcesso[] = [...additionalProcessos, ...processoCollection];
      jest.spyOn(processoService, 'addProcessoToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ stadio });
      comp.ngOnInit();

      expect(processoService.query).toHaveBeenCalled();
      expect(processoService.addProcessoToCollectionIfMissing).toHaveBeenCalledWith(processoCollection, ...additionalProcessos);
      expect(comp.processosSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const stadio: IStadio = { id: 456 };
      const processo: IProcesso = { id: 95134 };
      stadio.processo = processo;

      activatedRoute.data = of({ stadio });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(stadio));
      expect(comp.processosSharedCollection).toContain(processo);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stadio>>();
      const stadio = { id: 123 };
      jest.spyOn(stadioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stadio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stadio }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(stadioService.update).toHaveBeenCalledWith(stadio);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stadio>>();
      const stadio = new Stadio();
      jest.spyOn(stadioService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stadio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: stadio }));
      saveSubject.complete();

      // THEN
      expect(stadioService.create).toHaveBeenCalledWith(stadio);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Stadio>>();
      const stadio = { id: 123 };
      jest.spyOn(stadioService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ stadio });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(stadioService.update).toHaveBeenCalledWith(stadio);
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
  });
});
