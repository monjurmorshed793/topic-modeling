import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { KeywordComponent } from '../list/keyword.component';
import { KeywordDetailComponent } from '../detail/keyword-detail.component';
import { KeywordUpdateComponent } from '../update/keyword-update.component';
import { KeywordRoutingResolveService } from './keyword-routing-resolve.service';

const keywordRoute: Routes = [
  {
    path: '',
    component: KeywordComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: KeywordDetailComponent,
    resolve: {
      keyword: KeywordRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: KeywordUpdateComponent,
    resolve: {
      keyword: KeywordRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: KeywordUpdateComponent,
    resolve: {
      keyword: KeywordRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(keywordRoute)],
  exports: [RouterModule],
})
export class KeywordRoutingModule {}
