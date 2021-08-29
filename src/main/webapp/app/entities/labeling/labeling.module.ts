import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { LabelingComponent } from './list/labeling.component';
import { LabelingDetailComponent } from './detail/labeling-detail.component';
import { LabelingUpdateComponent } from './update/labeling-update.component';
import { LabelingDeleteDialogComponent } from './delete/labeling-delete-dialog.component';
import { LabelingRoutingModule } from './route/labeling-routing.module';

@NgModule({
  imports: [SharedModule, LabelingRoutingModule],
  declarations: [LabelingComponent, LabelingDetailComponent, LabelingUpdateComponent, LabelingDeleteDialogComponent],
  entryComponents: [LabelingDeleteDialogComponent],
})
export class LabelingModule {}
