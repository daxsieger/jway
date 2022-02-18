import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { StadioDetailComponent } from './stadio-detail.component';

describe('Stadio Management Detail Component', () => {
  let comp: StadioDetailComponent;
  let fixture: ComponentFixture<StadioDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [StadioDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ stadio: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(StadioDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(StadioDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load stadio on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.stadio).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
