import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TransizioniDetailComponent } from './transizioni-detail.component';

describe('Transizioni Management Detail Component', () => {
  let comp: TransizioniDetailComponent;
  let fixture: ComponentFixture<TransizioniDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransizioniDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ transizioni: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(TransizioniDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(TransizioniDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load transizioni on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.transizioni).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
