import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'keyword',
        data: { pageTitle: 'Keywords' },
        loadChildren: () => import('./keyword/keyword.module').then(m => m.KeywordModule),
      },
      {
        path: 'topic-modeling',
        data: { pageTitle: 'TopicModelings' },
        loadChildren: () => import('./topic-modeling/topic-modeling.module').then(m => m.TopicModelingModule),
      },
      {
        path: 'posts',
        data: { pageTitle: 'Posts' },
        loadChildren: () => import('./posts/posts.module').then(m => m.PostsModule),
      },
      {
        path: 'labeling',
        data: { pageTitle: 'Labelings' },
        loadChildren: () => import('./labeling/labeling.module').then(m => m.LabelingModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
