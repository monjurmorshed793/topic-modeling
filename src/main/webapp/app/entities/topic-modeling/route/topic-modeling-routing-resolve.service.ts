import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ITopicModeling, TopicModeling } from '../topic-modeling.model';
import { TopicModelingService } from '../service/topic-modeling.service';

@Injectable({ providedIn: 'root' })
export class TopicModelingRoutingResolveService implements Resolve<ITopicModeling> {
  constructor(protected service: TopicModelingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ITopicModeling> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((topicModeling: HttpResponse<TopicModeling>) => {
          if (topicModeling.body) {
            return of(topicModeling.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new TopicModeling());
  }
}
