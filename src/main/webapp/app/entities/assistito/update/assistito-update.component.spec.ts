import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AssistitoService } from '../service/assistito.service';
import { IAssistito, Assistito } from '../assistito.model';

import { AssistitoUpdateComponent } from './assistito-update.component';

describe('Assistito Management Update Component', () => {
  let comp: AssistitoUpdateComponent;
  let fixture: ComponentFixture<AssistitoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let assistitoService: AssistitoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AssistitoUpdateComponent],
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
      .overrideTemplate(AssistitoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AssistitoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    assistitoService = TestBed.inject(AssistitoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const assistito: IAssistito = { id: 456 };

      activatedRoute.data = of({ assistito });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(assistito));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assistito>>();
      const assistito = { id: 123 };
      jest.spyOn(assistitoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assistito });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assistito }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(assistitoService.update).toHaveBeenCalledWith(assistito);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assistito>>();
      const assistito = new Assistito();
      jest.spyOn(assistitoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assistito });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: assistito }));
      saveSubject.complete();

      // THEN
      expect(assistitoService.create).toHaveBeenCalledWith(assistito);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Assistito>>();
      const assistito = { id: 123 };
      jest.spyOn(assistitoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ assistito });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(assistitoService.update).toHaveBeenCalledWith(assistito);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
