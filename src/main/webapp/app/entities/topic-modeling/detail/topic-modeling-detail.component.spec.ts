import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { TopicModelingDetailComponent } from './topic-modeling-detail.component';

describe('Component Tests', () => {
  describe('TopicModeling Management Detail Component', () => {
    let comp: TopicModelingDetailComponent;
    let fixture: ComponentFixture<TopicModelingDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [TopicModelingDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ topicModeling: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(TopicModelingDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TopicModelingDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load topicModeling on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.topicModeling).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
