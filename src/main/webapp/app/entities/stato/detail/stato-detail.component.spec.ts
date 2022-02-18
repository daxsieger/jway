import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StatoDetailComponent } from './stato-detail.component';

describe('Stato Management Detail Component', () => {
  let comp: StatoDetailComponent;
  let fixture: ComponentFixture<StatoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StatoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stato: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StatoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StatoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stato on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stato).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
