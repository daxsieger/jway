import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IProduttore, Produttore } from '../produttore.model';

import { ProduttoreService } from './produttore.service';

describe('Produttore Service', () => {
  let service: ProduttoreService;
  let httpMock: HttpTestingController;
  let elemDefault: IProduttore;
  let expectedResult: IProduttore | IProduttore[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ProduttoreService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      idProduttore: 0,
      dsProduttore: 'AAAAAAA',
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

    it('should create a Produttore', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Produttore()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Produttore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idProduttore: 1,
          dsProduttore: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Produttore', () => {
      const patchObject = Object.assign(
        {
          idProduttore: 1,
          dsProduttore: 'BBBBBB',
        },
        new Produttore()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Produttore', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idProduttore: 1,
          dsProduttore: 'BBBBBB',
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

    it('should delete a Produttore', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addProduttoreToCollectionIfMissing', () => {
      it('should add a Produttore to an empty array', () => {
        const produttore: IProduttore = { id: 123 };
        expectedResult = service.addProduttoreToCollectionIfMissing([], produttore);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(produttore);
      });

      it('should not add a Produttore to an array that contains it', () => {
        const produttore: IProduttore = { id: 123 };
        const produttoreCollection: IProduttore[] = [
          {
            ...produttore,
          },
          { id: 456 },
        ];
        expectedResult = service.addProduttoreToCollectionIfMissing(produttoreCollection, produttore);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Produttore to an array that doesn't contain it", () => {
        const produttore: IProduttore = { id: 123 };
        const produttoreCollection: IProduttore[] = [{ id: 456 }];
        expectedResult = service.addProduttoreToCollectionIfMissing(produttoreCollection, produttore);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(produttore);
      });

      it('should add only unique Produttore to an array', () => {
        const produttoreArray: IProduttore[] = [{ id: 123 }, { id: 456 }, { id: 27825 }];
        const produttoreCollection: IProduttore[] = [{ id: 123 }];
        expectedResult = service.addProduttoreToCollectionIfMissing(produttoreCollection, ...produttoreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const produttore: IProduttore = { id: 123 };
        const produttore2: IProduttore = { id: 456 };
        expectedResult = service.addProduttoreToCollectionIfMissing([], produttore, produttore2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(produttore);
        expect(expectedResult).toContain(produttore2);
      });

      it('should accept null and undefined values', () => {
        const produttore: IProduttore = { id: 123 };
        expectedResult = service.addProduttoreToCollectionIfMissing([], null, produttore, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(produttore);
      });

      it('should return initial array if no Produttore is added', () => {
        const produttoreCollection: IProduttore[] = [{ id: 123 }];
        expectedResult = service.addProduttoreToCollectionIfMissing(produttoreCollection, undefined, null);
        expect(expectedResult).toEqual(produttoreCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
