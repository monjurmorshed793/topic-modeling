import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { TopicModelingComponent } from './list/topic-modeling.component';
import { TopicModelingDetailComponent } from './detail/topic-modeling-detail.component';
import { TopicModelingUpdateComponent } from './update/topic-modeling-update.component';
import { TopicModelingDeleteDialogComponent } from './delete/topic-modeling-delete-dialog.component';
import { TopicModelingRoutingModule } from './route/topic-modeling-routing.module';

@NgModule({
  imports: [SharedModule, TopicModelingRoutingModule],
  declarations: [TopicModelingComponent, TopicModelingDetailComponent, TopicModelingUpdateComponent, TopicModelingDeleteDialogComponent],
  entryComponents: [TopicModelingDeleteDialogComponent],
})
export class TopicModelingModule {}
