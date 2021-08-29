jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IKeyword, Keyword } from '../keyword.model';
import { KeywordService } from '../service/keyword.service';

import { KeywordRoutingResolveService } from './keyword-routing-resolve.service';

describe('Service Tests', () => {
  describe('Keyword routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: KeywordRoutingResolveService;
    let service: KeywordService;
    let resultKeyword: IKeyword | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(KeywordRoutingResolveService);
      service = TestBed.inject(KeywordService);
      resultKeyword = undefined;
    });

    describe('resolve', () => {
      it('should return IKeyword returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultKeyword = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultKeyword).toEqual({ id: 123 });
      });

      it('should return new IKeyword if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultKeyword = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultKeyword).toEqual(new Keyword());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Keyword })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultKeyword = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultKeyword).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
