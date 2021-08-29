import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ITopicModeling, TopicModeling } from '../topic-modeling.model';
import { TopicModelingService } from '../service/topic-modeling.service';
import { IKeyword } from 'app/entities/keyword/keyword.model';
import { KeywordService } from 'app/entities/keyword/service/keyword.service';

@Component({
  selector: 'jhi-topic-modeling-update',
  templateUrl: './topic-modeling-update.component.html',
})
export class TopicModelingUpdateComponent implements OnInit {
  isSaving = false;

  keywordsSharedCollection: IKeyword[] = [];

  editForm = this.fb.group({
    id: [],
    userName: [],
    category: [],
    subCategory: [],
    topic: [],
    subTopic: [],
    keyword: [],
  });

  constructor(
    protected topicModelingService: TopicModelingService,
    protected keywordService: KeywordService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topicModeling }) => {
      this.updateForm(topicModeling);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const topicModeling = this.createFromForm();
    if (topicModeling.id !== undefined) {
      this.subscribeToSaveResponse(this.topicModelingService.update(topicModeling));
    } else {
      this.subscribeToSaveResponse(this.topicModelingService.create(topicModeling));
    }
  }

  trackKeywordById(index: number, item: IKeyword): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITopicModeling>>): void {
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

  protected updateForm(topicModeling: ITopicModeling): void {
    this.editForm.patchValue({
      id: topicModeling.id,
      userName: topicModeling.userName,
      category: topicModeling.category,
      subCategory: topicModeling.subCategory,
      topic: topicModeling.topic,
      subTopic: topicModeling.subTopic,
      keyword: topicModeling.keyword,
    });

    this.keywordsSharedCollection = this.keywordService.addKeywordToCollectionIfMissing(
      this.keywordsSharedCollection,
      topicModeling.keyword
    );
  }

  protected loadRelationshipsOptions(): void {
    this.keywordService
      .query()
      .pipe(map((res: HttpResponse<IKeyword[]>) => res.body ?? []))
      .pipe(
        map((keywords: IKeyword[]) => this.keywordService.addKeywordToCollectionIfMissing(keywords, this.editForm.get('keyword')!.value))
      )
      .subscribe((keywords: IKeyword[]) => (this.keywordsSharedCollection = keywords));
  }

  protected createFromForm(): ITopicModeling {
    return {
      ...new TopicModeling(),
      id: this.editForm.get(['id'])!.value,
      userName: this.editForm.get(['userName'])!.value,
      category: this.editForm.get(['category'])!.value,
      subCategory: this.editForm.get(['subCategory'])!.value,
      topic: this.editForm.get(['topic'])!.value,
      subTopic: this.editForm.get(['subTopic'])!.value,
      keyword: this.editForm.get(['keyword'])!.value,
    };
  }
}
