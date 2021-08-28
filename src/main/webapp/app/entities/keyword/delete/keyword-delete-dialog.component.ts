import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IKeyword } from '../keyword.model';
import { KeywordService } from '../service/keyword.service';

@Component({
  templateUrl: './keyword-delete-dialog.component.html',
})
export class KeywordDeleteDialogComponent {
  keyword?: IKeyword;

  constructor(protected keywordService: KeywordService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.keywordService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
