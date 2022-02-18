import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ITipoEvento, TipoEvento } from '../tipo-evento.model';
import { TipoEventoService } from '../service/tipo-evento.service';

import { TipoEventoRoutingResolveService } from './tipo-evento-routing-resolve.service';

describe('TipoEvento routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: TipoEventoRoutingResolveService;
  let service: TipoEventoService;
  let resultTipoEvento: ITipoEvento | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(TipoEventoRoutingResolveService);
    service = TestBed.inject(TipoEventoService);
    resultTipoEvento = undefined;
  });

  describe('resolve', () => {
    it('should return ITipoEvento returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTipoEvento = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTipoEvento).toEqual({ id: 123 });
    });

    it('should return new ITipoEvento if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTipoEvento = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultTipoEvento).toEqual(new TipoEvento());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TipoEvento })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultTipoEvento = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultTipoEvento).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
