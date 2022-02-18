import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IStato, Stato } from '../stato.model';

import { StatoService } from './stato.service';

describe('Stato Service', () => {
  let service: StatoService;
  let httpMock: HttpTestingController;
  let elemDefault: IStato;
  let expectedResult: IStato | IStato[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(StatoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      idStadio: 0,
      dsStadio: 'AAAAAAA',
      tsCambioStato: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          tsCambioStato: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Stato', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          tsCambioStato: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          tsCambioStato: currentDate,
        },
        returnedFromService
      );

      service.create(new Stato()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Stato', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idStadio: 1,
          dsStadio: 'BBBBBB',
          tsCambioStato: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          tsCambioStato: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Stato', () => {
      const patchObject = Object.assign(
        {
          dsStadio: 'BBBBBB',
        },
        new Stato()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          tsCambioStato: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Stato', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          idStadio: 1,
          dsStadio: 'BBBBBB',
          tsCambioStato: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          tsCambioStato: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Stato', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addStatoToCollectionIfMissing', () => {
      it('should add a Stato to an empty array', () => {
        const stato: IStato = { id: 123 };
        expectedResult = service.addStatoToCollectionIfMissing([], stato);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stato);
      });

      it('should not add a Stato to an array that contains it', () => {
        const stato: IStato = { id: 123 };
        const statoCollection: IStato[] = [
          {
            ...stato,
          },
          { id: 456 },
        ];
        expectedResult = service.addStatoToCollectionIfMissing(statoCollection, stato);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Stato to an array that doesn't contain it", () => {
        const stato: IStato = { id: 123 };
        const statoCollection: IStato[] = [{ id: 456 }];
        expectedResult = service.addStatoToCollectionIfMissing(statoCollection, stato);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stato);
      });

      it('should add only unique Stato to an array', () => {
        const statoArray: IStato[] = [{ id: 123 }, { id: 456 }, { id: 95171 }];
        const statoCollection: IStato[] = [{ id: 123 }];
        expectedResult = service.addStatoToCollectionIfMissing(statoCollection, ...statoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const stato: IStato = { id: 123 };
        const stato2: IStato = { id: 456 };
        expectedResult = service.addStatoToCollectionIfMissing([], stato, stato2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(stato);
        expect(expectedResult).toContain(stato2);
      });

      it('should accept null and undefined values', () => {
        const stato: IStato = { id: 123 };
        expectedResult = service.addStatoToCollectionIfMissing([], null, stato, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(stato);
      });

      it('should return initial array if no Stato is added', () => {
        const statoCollection: IStato[] = [{ id: 123 }];
        expectedResult = service.addStatoToCollectionIfMissing(statoCollection, undefined, null);
        expect(expectedResult).toEqual(statoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
