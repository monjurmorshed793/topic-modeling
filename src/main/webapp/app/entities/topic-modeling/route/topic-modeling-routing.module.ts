import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { TopicModelingComponent } from '../list/topic-modeling.component';
import { TopicModelingDetailComponent } from '../detail/topic-modeling-detail.component';
import { TopicModelingUpdateComponent } from '../update/topic-modeling-update.component';
import { TopicModelingRoutingResolveService } from './topic-modeling-routing-resolve.service';

const topicModelingRoute: Routes = [
  {
    path: '',
    component: TopicModelingComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: TopicModelingDetailComponent,
    resolve: {
      topicModeling: TopicModelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: TopicModelingUpdateComponent,
    resolve: {
      topicModeling: TopicModelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: TopicModelingUpdateComponent,
    resolve: {
      topicModeling: TopicModelingRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(topicModelingRoute)],
  exports: [RouterModule],
})
export class TopicModelingRoutingModule {}
