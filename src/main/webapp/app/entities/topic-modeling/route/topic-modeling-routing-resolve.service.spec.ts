jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { ITopicModeling, TopicModeling } from '../topic-modeling.model';
import { TopicModelingService } from '../service/topic-modeling.service';

import { TopicModelingRoutingResolveService } from './topic-modeling-routing-resolve.service';

describe('Service Tests', () => {
  describe('TopicModeling routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: TopicModelingRoutingResolveService;
    let service: TopicModelingService;
    let resultTopicModeling: ITopicModeling | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(TopicModelingRoutingResolveService);
      service = TestBed.inject(TopicModelingService);
      resultTopicModeling = undefined;
    });

    describe('resolve', () => {
      it('should return ITopicModeling returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTopicModeling = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTopicModeling).toEqual({ id: 123 });
      });

      it('should return new ITopicModeling if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTopicModeling = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultTopicModeling).toEqual(new TopicModeling());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as TopicModeling })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultTopicModeling = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultTopicModeling).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
