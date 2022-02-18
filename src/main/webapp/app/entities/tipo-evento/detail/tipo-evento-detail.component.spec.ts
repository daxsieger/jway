import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TipoEventoDetailComponent } from './tipo-evento-detail.component';

describe('TipoEvento Management Detail Component', () => {
  let comp: TipoEventoDetailComponent;
  let fixture: ComponentFixture<TipoEventoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TipoEventoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ tipoEvento: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TipoEventoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TipoEventoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load tipoEvento on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.tipoEvento).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
