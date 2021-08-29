import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { LabelingComponent } from '../list/labeling.component';
import { LabelingDetailComponent } from '../detail/labeling-detail.component';
import { LabelingUpdateComponent } from '../update/labeling-update.component';
import { LabelingRoutingResolveService } from './labeling-routing-resolve.service';

const labelingRoute: Routes = [
  {
    path: '',
    component: LabelingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: LabelingDetailComponent,
    resolve: {
      labeling: LabelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: LabelingUpdateComponent,
    resolve: {
      labeling: LabelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: LabelingUpdateComponent,
    resolve: {
      labeling: LabelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(labelingRoute)],
  exports: [RouterModule],
})
export class LabelingRoutingModule {}
