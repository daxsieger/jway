import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ProduttoreService } from '../service/produttore.service';
import { IProduttore, Produttore } from '../produttore.model';

import { ProduttoreUpdateComponent } from './produttore-update.component';

describe('Produttore Management Update Component', () => {
  let comp: ProduttoreUpdateComponent;
  let fixture: ComponentFixture<ProduttoreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let produttoreService: ProduttoreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ProduttoreUpdateComponent],
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
      .overrideTemplate(ProduttoreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ProduttoreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    produttoreService = TestBed.inject(ProduttoreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const produttore: IProduttore = { id: 456 };

      activatedRoute.data = of({ produttore });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(produttore));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Produttore>>();
      const produttore = { id: 123 };
      jest.spyOn(produttoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produttore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: produttore }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(produttoreService.update).toHaveBeenCalledWith(produttore);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Produttore>>();
      const produttore = new Produttore();
      jest.spyOn(produttoreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produttore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: produttore }));
      saveSubject.complete();

      // THEN
      expect(produttoreService.create).toHaveBeenCalledWith(produttore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Produttore>>();
      const produttore = { id: 123 };
      jest.spyOn(produttoreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ produttore });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(produttoreService.update).toHaveBeenCalledWith(produttore);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
