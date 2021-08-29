import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ITopicModeling } from '../topic-modeling.model';
import { TopicModelingService } from '../service/topic-modeling.service';

@Component({
  templateUrl: './topic-modeling-delete-dialog.component.html',
})
export class TopicModelingDeleteDialogComponent {
  topicModeling?: ITopicModeling;

  constructor(protected topicModelingService: TopicModelingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.topicModelingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
