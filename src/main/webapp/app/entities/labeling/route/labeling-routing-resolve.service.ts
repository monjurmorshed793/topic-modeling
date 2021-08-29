import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ILabeling, Labeling } from '../labeling.model';
import { LabelingService } from '../service/labeling.service';

@Injectable({ providedIn: 'root' })
export class LabelingRoutingResolveService implements Resolve<ILabeling> {
  constructor(protected service: LabelingService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ILabeling> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((labeling: HttpResponse<Labeling>) => {
          if (labeling.body) {
            return of(labeling.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Labeling());
  }
}
