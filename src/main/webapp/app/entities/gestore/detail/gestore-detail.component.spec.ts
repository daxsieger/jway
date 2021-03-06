import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { GestoreDetailComponent } from './gestore-detail.component';

describe('Gestore Management Detail Component', () => {
  let comp: GestoreDetailComponent;
  let fixture: ComponentFixture<GestoreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GestoreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ gestore: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(GestoreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(GestoreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load gestore on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.gestore).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
