import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { ILabeling, Labeling } from '../labeling.model';
import { LabelingService } from '../service/labeling.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-labeling-update',
  templateUrl: './labeling-update.component.html',
})
export class LabelingUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    userName: [],
    framework: [],
    documentNo: [],
    dominantTopic: [],
    topicPercContrib: [],
    keywords: [],
    title: [],
    texts: [],
    answer: [],
    label: [],
    reason: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected labelingService: LabelingService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ labeling }) => {
      this.updateForm(labeling);
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('topicModelingApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const labeling = this.createFromForm();
    if (labeling.id !== undefined) {
      this.subscribeToSaveResponse(this.labelingService.update(labeling));
    } else {
      this.subscribeToSaveResponse(this.labelingService.create(labeling));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ILabeling>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(labeling: ILabeling): void {
    this.editForm.patchValue({
      id: labeling.id,
      userName: labeling.userName,
      framework: labeling.framework,
      documentNo: labeling.documentNo,
      dominantTopic: labeling.dominantTopic,
      topicPercContrib: labeling.topicPercContrib,
      keywords: labeling.keywords,
      title: labeling.title,
      texts: labeling.texts,
      answer: labeling.answer,
      label: labeling.label,
      reason: labeling.reason,
    });
  }

  protected createFromForm(): ILabeling {
    return {
      ...new Labeling(),
      id: this.editForm.get(['id'])!.value,
      userName: this.editForm.get(['userName'])!.value,
      framework: this.editForm.get(['framework'])!.value,
      documentNo: this.editForm.get(['documentNo'])!.value,
      dominantTopic: this.editForm.get(['dominantTopic'])!.value,
      topicPercContrib: this.editForm.get(['topicPercContrib'])!.value,
      keywords: this.editForm.get(['keywords'])!.value,
      title: this.editForm.get(['title'])!.value,
      texts: this.editForm.get(['texts'])!.value,
      answer: this.editForm.get(['answer'])!.value,
      label: this.editForm.get(['label'])!.value,
      reason: this.editForm.get(['reason'])!.value,
    };
  }
}
