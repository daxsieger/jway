import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IStadio, Stadio } from '../stadio.model';

import { StadioService } from './stadio.service';

describe('Stadio Service', () => {
  let service: StadioService;
  let httpMock: HttpTestingController;
  let elemDefault: IStadio;
  let expectedResult: IStadio | IStadio[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StadioService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idStadio: 0,
      dsStadio: 'AAAAAAA',
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

    it('should create a Stadio', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Stadio()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Stadio', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idStadio: 1,
          dsStadio: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Stadio', () => {
      const patchObject = Object.assign(
        {
          idStadio: 1,
          dsStadio: 'BBBBBB',
        },
        new Stadio()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Stadio', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idStadio: 1,
          dsStadio: 'BBBBBB',
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

    it('should delete a Stadio', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addStadioToCollectionIfMissing', () => {
      it('should add a Stadio to an empty array', () => {
        const stadio: IStadio = { id: 123 };
        expectedResult = service.addStadioToCollectionIfMissing([], stadio);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stadio);
      });

      it('should not add a Stadio to an array that contains it', () => {
        const stadio: IStadio = { id: 123 };
        const stadioCollection: IStadio[] = [
          {
            ...stadio,
          },
          { id: 456 },
        ];
        expectedResult = service.addStadioToCollectionIfMissing(stadioCollection, stadio);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Stadio to an array that doesn't contain it", () => {
        const stadio: IStadio = { id: 123 };
        const stadioCollection: IStadio[] = [{ id: 456 }];
        expectedResult = service.addStadioToCollectionIfMissing(stadioCollection, stadio);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stadio);
      });

      it('should add only unique Stadio to an array', () => {
        const stadioArray: IStadio[] = [{ id: 123 }, { id: 456 }, { id: 383 }];
        const stadioCollection: IStadio[] = [{ id: 123 }];
        expectedResult = service.addStadioToCollectionIfMissing(stadioCollection, ...stadioArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stadio: IStadio = { id: 123 };
        const stadio2: IStadio = { id: 456 };
        expectedResult = service.addStadioToCollectionIfMissing([], stadio, stadio2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stadio);
        expect(expectedResult).toContain(stadio2);
      });

      it('should accept null and undefined values', () => {
        const stadio: IStadio = { id: 123 };
        expectedResult = service.addStadioToCollectionIfMissing([], null, stadio, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stadio);
      });

      it('should return initial array if no Stadio is added', () => {
        const stadioCollection: IStadio[] = [{ id: 123 }];
        expectedResult = service.addStadioToCollectionIfMissing(stadioCollection, undefined, null);
        expect(expectedResult).toEqual(stadioCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
