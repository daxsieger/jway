import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IAssistito, Assistito } from '../assistito.model';

import { AssistitoService } from './assistito.service';

describe('Assistito Service', () => {
  let service: AssistitoService;
  let httpMock: HttpTestingController;
  let elemDefault: IAssistito;
  let expectedResult: IAssistito | IAssistito[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(AssistitoService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idAssistito: 0,
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

    it('should create a Assistito', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Assistito()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Assistito', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idAssistito: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Assistito', () => {
      const patchObject = Object.assign({}, new Assistito());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Assistito', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idAssistito: 1,
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

    it('should delete a Assistito', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addAssistitoToCollectionIfMissing', () => {
      it('should add a Assistito to an empty array', () => {
        const assistito: IAssistito = { id: 123 };
        expectedResult = service.addAssistitoToCollectionIfMissing([], assistito);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(assistito);
      });

      it('should not add a Assistito to an array that contains it', () => {
        const assistito: IAssistito = { id: 123 };
        const assistitoCollection: IAssistito[] = [
          {
            ...assistito,
          },
          { id: 456 },
        ];
        expectedResult = service.addAssistitoToCollectionIfMissing(assistitoCollection, assistito);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Assistito to an array that doesn't contain it", () => {
        const assistito: IAssistito = { id: 123 };
        const assistitoCollection: IAssistito[] = [{ id: 456 }];
        expectedResult = service.addAssistitoToCollectionIfMissing(assistitoCollection, assistito);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(assistito);
      });

      it('should add only unique Assistito to an array', () => {
        const assistitoArray: IAssistito[] = [{ id: 123 }, { id: 456 }, { id: 4508 }];
        const assistitoCollection: IAssistito[] = [{ id: 123 }];
        expectedResult = service.addAssistitoToCollectionIfMissing(assistitoCollection, ...assistitoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const assistito: IAssistito = { id: 123 };
        const assistito2: IAssistito = { id: 456 };
        expectedResult = service.addAssistitoToCollectionIfMissing([], assistito, assistito2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(assistito);
        expect(expectedResult).toContain(assistito2);
      });

      it('should accept null and undefined values', () => {
        const assistito: IAssistito = { id: 123 };
        expectedResult = service.addAssistitoToCollectionIfMissing([], null, assistito, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(assistito);
      });

      it('should return initial array if no Assistito is added', () => {
        const assistitoCollection: IAssistito[] = [{ id: 123 }];
        expectedResult = service.addAssistitoToCollectionIfMissing(assistitoCollection, undefined, null);
        expect(expectedResult).toEqual(assistitoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
