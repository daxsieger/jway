jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { TipoEventoService } from '../service/tipo-evento.service';

import { TipoEventoDeleteDialogComponent } from './tipo-evento-delete-dialog.component';

describe('TipoEvento Management Delete Component', () => {
  let comp: TipoEventoDeleteDialogComponent;
  let fixture: ComponentFixture<TipoEventoDeleteDialogComponent>;
  let service: TipoEventoService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TipoEventoDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(TipoEventoDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TipoEventoDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TipoEventoService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
