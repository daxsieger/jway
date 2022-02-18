import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { TipoEventoService } from '../service/tipo-evento.service';
import { ITipoEvento, TipoEvento } from '../tipo-evento.model';

import { TipoEventoUpdateComponent } from './tipo-evento-update.component';

describe('TipoEvento Management Update Component', () => {
  let comp: TipoEventoUpdateComponent;
  let fixture: ComponentFixture<TipoEventoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let tipoEventoService: TipoEventoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [TipoEventoUpdateComponent],
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
      .overrideTemplate(TipoEventoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TipoEventoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    tipoEventoService = TestBed.inject(TipoEventoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const tipoEvento: ITipoEvento = { id: 456 };

      activatedRoute.data = of({ tipoEvento });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(tipoEvento));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TipoEvento>>();
      const tipoEvento = { id: 123 };
      jest.spyOn(tipoEventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoEvento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipoEvento }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(tipoEventoService.update).toHaveBeenCalledWith(tipoEvento);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TipoEvento>>();
      const tipoEvento = new TipoEvento();
      jest.spyOn(tipoEventoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoEvento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: tipoEvento }));
      saveSubject.complete();

      // THEN
      expect(tipoEventoService.create).toHaveBeenCalledWith(tipoEvento);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<TipoEvento>>();
      const tipoEvento = { id: 123 };
      jest.spyOn(tipoEventoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ tipoEvento });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(tipoEventoService.update).toHaveBeenCalledWith(tipoEvento);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
