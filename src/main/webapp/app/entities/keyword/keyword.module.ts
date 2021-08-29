import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { KeywordComponent } from './list/keyword.component';
import { KeywordDetailComponent } from './detail/keyword-detail.component';
import { KeywordUpdateComponent } from './update/keyword-update.component';
import { KeywordDeleteDialogComponent } from './delete/keyword-delete-dialog.component';
import { KeywordRoutingModule } from './route/keyword-routing.module';

@NgModule({
  imports: [SharedModule, KeywordRoutingModule],
  declarations: [KeywordComponent, KeywordDetailComponent, KeywordUpdateComponent, KeywordDeleteDialogComponent],
  entryComponents: [KeywordDeleteDialogComponent],
})
export class KeywordModule {}
