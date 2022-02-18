import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITransizioni, Transizioni } from '../transizioni.model';

import { TransizioniService } from './transizioni.service';

describe('Transizioni Service', () => {
  let service: TransizioniService;
  let httpMock: HttpTestingController;
  let elemDefault: ITransizioni;
  let expectedResult: ITransizioni | ITransizioni[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TransizioniService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idTransizione: 0,
      dsTransizione: 'AAAAAAA',
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

    it('should create a Transizioni', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Transizioni()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Transizioni', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idTransizione: 1,
          dsTransizione: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Transizioni', () => {
      const patchObject = Object.assign(
        {
          dsTransizione: 'BBBBBB',
        },
        new Transizioni()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Transizioni', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idTransizione: 1,
          dsTransizione: 'BBBBBB',
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

    it('should delete a Transizioni', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addTransizioniToCollectionIfMissing', () => {
      it('should add a Transizioni to an empty array', () => {
        const transizioni: ITransizioni = { id: 123 };
        expectedResult = service.addTransizioniToCollectionIfMissing([], transizioni);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transizioni);
      });

      it('should not add a Transizioni to an array that contains it', () => {
        const transizioni: ITransizioni = { id: 123 };
        const transizioniCollection: ITransizioni[] = [
          {
            ...transizioni,
          },
          { id: 456 },
        ];
        expectedResult = service.addTransizioniToCollectionIfMissing(transizioniCollection, transizioni);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Transizioni to an array that doesn't contain it", () => {
        const transizioni: ITransizioni = { id: 123 };
        const transizioniCollection: ITransizioni[] = [{ id: 456 }];
        expectedResult = service.addTransizioniToCollectionIfMissing(transizioniCollection, transizioni);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transizioni);
      });

      it('should add only unique Transizioni to an array', () => {
        const transizioniArray: ITransizioni[] = [{ id: 123 }, { id: 456 }, { id: 11684 }];
        const transizioniCollection: ITransizioni[] = [{ id: 123 }];
        expectedResult = service.addTransizioniToCollectionIfMissing(transizioniCollection, ...transizioniArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const transizioni: ITransizioni = { id: 123 };
        const transizioni2: ITransizioni = { id: 456 };
        expectedResult = service.addTransizioniToCollectionIfMissing([], transizioni, transizioni2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(transizioni);
        expect(expectedResult).toContain(transizioni2);
      });

      it('should accept null and undefined values', () => {
        const transizioni: ITransizioni = { id: 123 };
        expectedResult = service.addTransizioniToCollectionIfMissing([], null, transizioni, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(transizioni);
      });

      it('should return initial array if no Transizioni is added', () => {
        const transizioniCollection: ITransizioni[] = [{ id: 123 }];
        expectedResult = service.addTransizioniToCollectionIfMissing(transizioniCollection, undefined, null);
        expect(expectedResult).toEqual(transizioniCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
