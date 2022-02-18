import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { TipoEventoService } from '../service/tipo-evento.service';

import { TipoEventoComponent } from './tipo-evento.component';

describe('TipoEvento Management Component', () => {
  let comp: TipoEventoComponent;
  let fixture: ComponentFixture<TipoEventoComponent>;
  let service: TipoEventoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [TipoEventoComponent],
    })
      .overrideTemplate(TipoEventoComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(TipoEventoComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(TipoEventoService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.tipoEventos?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });
});
