import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ProduttoreDetailComponent } from './produttore-detail.component';

describe('Produttore Management Detail Component', () => {
  let comp: ProduttoreDetailComponent;
  let fixture: ComponentFixture<ProduttoreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProduttoreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ produttore: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ProduttoreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ProduttoreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load produttore on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.produttore).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
