import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { AssistitoDetailComponent } from './assistito-detail.component';

describe('Assistito Management Detail Component', () => {
  let comp: AssistitoDetailComponent;
  let fixture: ComponentFixture<AssistitoDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AssistitoDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ assistito: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(AssistitoDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(AssistitoDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load assistito on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.assistito).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
