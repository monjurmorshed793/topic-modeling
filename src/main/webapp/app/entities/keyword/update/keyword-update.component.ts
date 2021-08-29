import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IKeyword, Keyword } from '../keyword.model';
import { KeywordService } from '../service/keyword.service';

@Component({
  selector: 'jhi-keyword-update',
  templateUrl: './keyword-update.component.html',
})
export class KeywordUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    framework: [],
    topicNumber: [],
    keywords: [],
    numberOfPosts: [],
  });

  constructor(protected keywordService: KeywordService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ keyword }) => {
      this.updateForm(keyword);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const keyword = this.createFromForm();
    if (keyword.id !== undefined) {
      this.subscribeToSaveResponse(this.keywordService.update(keyword));
    } else {
      this.subscribeToSaveResponse(this.keywordService.create(keyword));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IKeyword>>): void {
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

  protected updateForm(keyword: IKeyword): void {
    this.editForm.patchValue({
      id: keyword.id,
      framework: keyword.framework,
      topicNumber: keyword.topicNumber,
      keywords: keyword.keywords,
      numberOfPosts: keyword.numberOfPosts,
    });
  }

  protected createFromForm(): IKeyword {
    return {
      ...new Keyword(),
      id: this.editForm.get(['id'])!.value,
      framework: this.editForm.get(['framework'])!.value,
      topicNumber: this.editForm.get(['topicNumber'])!.value,
      keywords: this.editForm.get(['keywords'])!.value,
      numberOfPosts: this.editForm.get(['numberOfPosts'])!.value,
    };
  }
}
