import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IPosts, Posts } from '../posts.model';
import { PostsService } from '../service/posts.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-posts-update',
  templateUrl: './posts-update.component.html',
})
export class PostsUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    framework: [],
    documentNo: [],
    dominantTopic: [],
    topicPercContrib: [],
    keywords: [],
    title: [],
    texts: [],
    answer: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected postsService: PostsService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ posts }) => {
      this.updateForm(posts);
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
    const posts = this.createFromForm();
    if (posts.id !== undefined) {
      this.subscribeToSaveResponse(this.postsService.update(posts));
    } else {
      this.subscribeToSaveResponse(this.postsService.create(posts));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPosts>>): void {
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

  protected updateForm(posts: IPosts): void {
    this.editForm.patchValue({
      id: posts.id,
      framework: posts.framework,
      documentNo: posts.documentNo,
      dominantTopic: posts.dominantTopic,
      topicPercContrib: posts.topicPercContrib,
      keywords: posts.keywords,
      title: posts.title,
      texts: posts.texts,
      answer: posts.answer,
    });
  }

  protected createFromForm(): IPosts {
    return {
      ...new Posts(),
      id: this.editForm.get(['id'])!.value,
      framework: this.editForm.get(['framework'])!.value,
      documentNo: this.editForm.get(['documentNo'])!.value,
      dominantTopic: this.editForm.get(['dominantTopic'])!.value,
      topicPercContrib: this.editForm.get(['topicPercContrib'])!.value,
      keywords: this.editForm.get(['keywords'])!.value,
      title: this.editForm.get(['title'])!.value,
      texts: this.editForm.get(['texts'])!.value,
      answer: this.editForm.get(['answer'])!.value,
    };
  }
}
