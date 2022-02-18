import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IGestore, Gestore } from '../gestore.model';

import { GestoreService } from './gestore.service';

describe('Gestore Service', () => {
  let service: GestoreService;
  let httpMock: HttpTestingController;
  let elemDefault: IGestore;
  let expectedResult: IGestore | IGestore[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(GestoreService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idGestore: 0,
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

    it('should create a Gestore', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Gestore()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Gestore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idGestore: 1,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Gestore', () => {
      const patchObject = Object.assign({}, new Gestore());

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Gestore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idGestore: 1,
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

    it('should delete a Gestore', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addGestoreToCollectionIfMissing', () => {
      it('should add a Gestore to an empty array', () => {
        const gestore: IGestore = { id: 123 };
        expectedResult = service.addGestoreToCollectionIfMissing([], gestore);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gestore);
      });

      it('should not add a Gestore to an array that contains it', () => {
        const gestore: IGestore = { id: 123 };
        const gestoreCollection: IGestore[] = [
          {
            ...gestore,
          },
          { id: 456 },
        ];
        expectedResult = service.addGestoreToCollectionIfMissing(gestoreCollection, gestore);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Gestore to an array that doesn't contain it", () => {
        const gestore: IGestore = { id: 123 };
        const gestoreCollection: IGestore[] = [{ id: 456 }];
        expectedResult = service.addGestoreToCollectionIfMissing(gestoreCollection, gestore);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gestore);
      });

      it('should add only unique Gestore to an array', () => {
        const gestoreArray: IGestore[] = [{ id: 123 }, { id: 456 }, { id: 28711 }];
        const gestoreCollection: IGestore[] = [{ id: 123 }];
        expectedResult = service.addGestoreToCollectionIfMissing(gestoreCollection, ...gestoreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const gestore: IGestore = { id: 123 };
        const gestore2: IGestore = { id: 456 };
        expectedResult = service.addGestoreToCollectionIfMissing([], gestore, gestore2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(gestore);
        expect(expectedResult).toContain(gestore2);
      });

      it('should accept null and undefined values', () => {
        const gestore: IGestore = { id: 123 };
        expectedResult = service.addGestoreToCollectionIfMissing([], null, gestore, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(gestore);
      });

      it('should return initial array if no Gestore is added', () => {
        const gestoreCollection: IGestore[] = [{ id: 123 }];
        expectedResult = service.addGestoreToCollectionIfMissing(gestoreCollection, undefined, null);
        expect(expectedResult).toEqual(gestoreCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
