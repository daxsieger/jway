import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { GestoreService } from '../service/gestore.service';
import { IGestore, Gestore } from '../gestore.model';

import { GestoreUpdateComponent } from './gestore-update.component';

describe('Gestore Management Update Component', () => {
  let comp: GestoreUpdateComponent;
  let fixture: ComponentFixture<GestoreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let gestoreService: GestoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [GestoreUpdateComponent],
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
      .overrideTemplate(GestoreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(GestoreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    gestoreService = TestBed.inject(GestoreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const gestore: IGestore = { id: 456 };

      activatedRoute.data = of({ gestore });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(gestore));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Gestore>>();
      const gestore = { id: 123 };
      jest.spyOn(gestoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gestore }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(gestoreService.update).toHaveBeenCalledWith(gestore);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Gestore>>();
      const gestore = new Gestore();
      jest.spyOn(gestoreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: gestore }));
      saveSubject.complete();

      // THEN
      expect(gestoreService.create).toHaveBeenCalledWith(gestore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Gestore>>();
      const gestore = { id: 123 };
      jest.spyOn(gestoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ gestore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(gestoreService.update).toHaveBeenCalledWith(gestore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
