import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IKeyword } from '../keyword.model';

@Component({
  selector: 'jhi-keyword-detail',
  templateUrl: './keyword-detail.component.html',
})
export class KeywordDetailComponent implements OnInit {
  keyword: IKeyword | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ keyword }) => {
      this.keyword = keyword;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
