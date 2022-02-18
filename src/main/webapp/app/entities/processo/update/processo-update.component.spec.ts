import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProcessoService } from '../service/processo.service';
import { IProcesso, Processo } from '../processo.model';

import { ProcessoUpdateComponent } from './processo-update.component';

describe('Processo Management Update Component', () => {
  let comp: ProcessoUpdateComponent;
  let fixture: ComponentFixture<ProcessoUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let processoService: ProcessoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProcessoUpdateComponent],
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
      .overrideTemplate(ProcessoUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProcessoUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    processoService = TestBed.inject(ProcessoService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const processo: IProcesso = { id: 456 };

      activatedRoute.data = of({ processo });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(processo));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Processo>>();
      const processo = { id: 123 };
      jest.spyOn(processoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: processo }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(processoService.update).toHaveBeenCalledWith(processo);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Processo>>();
      const processo = new Processo();
      jest.spyOn(processoService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: processo }));
      saveSubject.complete();

      // THEN
      expect(processoService.create).toHaveBeenCalledWith(processo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Processo>>();
      const processo = { id: 123 };
      jest.spyOn(processoService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ processo });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(processoService.update).toHaveBeenCalledWith(processo);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
