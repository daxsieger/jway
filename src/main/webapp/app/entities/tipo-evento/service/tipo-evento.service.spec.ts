import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITipoEvento, TipoEvento } from '../tipo-evento.model';

import { TipoEventoService } from './tipo-evento.service';

describe('TipoEvento Service', () => {
  let service: TipoEventoService;
  let httpMock: HttpTestingController;
  let elemDefault: ITipoEvento;
  let expectedResult: ITipoEvento | ITipoEvento[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TipoEventoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idTipoEvento: 0,
      dsTipoEvento: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a TipoEvento', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new TipoEvento()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TipoEvento', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idTipoEvento: 1,
          dsTipoEvento: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TipoEvento', () => {
      const patchObject = Object.assign({}, new TipoEvento());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TipoEvento', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idTipoEvento: 1,
          dsTipoEvento: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a TipoEvento', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTipoEventoToCollectionIfMissing', () => {
      it('should add a TipoEvento to an empty array', () => {
        const tipoEvento: ITipoEvento = { id: 123 };
        expectedResult = service.addTipoEventoToCollectionIfMissing([], tipoEvento);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipoEvento);
      });

      it('should not add a TipoEvento to an array that contains it', () => {
        const tipoEvento: ITipoEvento = { id: 123 };
        const tipoEventoCollection: ITipoEvento[] = [
          {
            ...tipoEvento,
          },
          { id: 456 },
        ];
        expectedResult = service.addTipoEventoToCollectionIfMissing(tipoEventoCollection, tipoEvento);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TipoEvento to an array that doesn't contain it", () => {
        const tipoEvento: ITipoEvento = { id: 123 };
        const tipoEventoCollection: ITipoEvento[] = [{ id: 456 }];
        expectedResult = service.addTipoEventoToCollectionIfMissing(tipoEventoCollection, tipoEvento);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipoEvento);
      });

      it('should add only unique TipoEvento to an array', () => {
        const tipoEventoArray: ITipoEvento[] = [{ id: 123 }, { id: 456 }, { id: 56992 }];
        const tipoEventoCollection: ITipoEvento[] = [{ id: 123 }];
        expectedResult = service.addTipoEventoToCollectionIfMissing(tipoEventoCollection, ...tipoEventoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const tipoEvento: ITipoEvento = { id: 123 };
        const tipoEvento2: ITipoEvento = { id: 456 };
        expectedResult = service.addTipoEventoToCollectionIfMissing([], tipoEvento, tipoEvento2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(tipoEvento);
        expect(expectedResult).toContain(tipoEvento2);
      });

      it('should accept null and undefined values', () => {
        const tipoEvento: ITipoEvento = { id: 123 };
        expectedResult = service.addTipoEventoToCollectionIfMissing([], null, tipoEvento, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(tipoEvento);
      });

      it('should return initial array if no TipoEvento is added', () => {
        const tipoEventoCollection: ITipoEvento[] = [{ id: 123 }];
        expectedResult = service.addTipoEventoToCollectionIfMissing(tipoEventoCollection, undefined, null);
        expect(expectedResult).toEqual(tipoEventoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
