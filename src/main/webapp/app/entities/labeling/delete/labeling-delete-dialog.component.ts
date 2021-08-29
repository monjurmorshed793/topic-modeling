import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { ILabeling } from '../labeling.model';
import { LabelingService } from '../service/labeling.service';

@Component({
  templateUrl: './labeling-delete-dialog.component.html',
})
export class LabelingDeleteDialogComponent {
  labeling?: ILabeling;

  constructor(protected labelingService: LabelingService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.labelingService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
