import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ITopicModeling } from '../topic-modeling.model';

@Component({
  selector: 'jhi-topic-modeling-detail',
  templateUrl: './topic-modeling-detail.component.html',
})
export class TopicModelingDetailComponent implements OnInit {
  topicModeling: ITopicModeling | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ topicModeling }) => {
      this.topicModeling = topicModeling;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
