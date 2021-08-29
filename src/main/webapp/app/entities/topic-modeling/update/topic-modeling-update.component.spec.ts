jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { TopicModelingService } from '../service/topic-modeling.service';
import { ITopicModeling, TopicModeling } from '../topic-modeling.model';
import { IKeyword } from 'app/entities/keyword/keyword.model';
import { KeywordService } from 'app/entities/keyword/service/keyword.service';

import { TopicModelingUpdateComponent } from './topic-modeling-update.component';

describe('Component Tests', () => {
  describe('TopicModeling Management Update Component', () => {
    let comp: TopicModelingUpdateComponent;
    let fixture: ComponentFixture<TopicModelingUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let topicModelingService: TopicModelingService;
    let keywordService: KeywordService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [TopicModelingUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(TopicModelingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TopicModelingUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      topicModelingService = TestBed.inject(TopicModelingService);
      keywordService = TestBed.inject(KeywordService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call Keyword query and add missing value', () => {
        const topicModeling: ITopicModeling = { id: 456 };
        const keyword: IKeyword = { id: 20247 };
        topicModeling.keyword = keyword;

        const keywordCollection: IKeyword[] = [{ id: 21465 }];
        jest.spyOn(keywordService, 'query').mockReturnValue(of(new HttpResponse({ body: keywordCollection })));
        const additionalKeywords = [keyword];
        const expectedCollection: IKeyword[] = [...additionalKeywords, ...keywordCollection];
        jest.spyOn(keywordService, 'addKeywordToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ topicModeling });
        comp.ngOnInit();

        expect(keywordService.query).toHaveBeenCalled();
        expect(keywordService.addKeywordToCollectionIfMissing).toHaveBeenCalledWith(keywordCollection, ...additionalKeywords);
        expect(comp.keywordsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const topicModeling: ITopicModeling = { id: 456 };
        const keyword: IKeyword = { id: 63346 };
        topicModeling.keyword = keyword;

        activatedRoute.data = of({ topicModeling });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(topicModeling));
        expect(comp.keywordsSharedCollection).toContain(keyword);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TopicModeling>>();
        const topicModeling = { id: 123 };
        jest.spyOn(topicModelingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ topicModeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: topicModeling }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(topicModelingService.update).toHaveBeenCalledWith(topicModeling);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TopicModeling>>();
        const topicModeling = new TopicModeling();
        jest.spyOn(topicModelingService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ topicModeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: topicModeling }));
        saveSubject.complete();

        // THEN
        expect(topicModelingService.create).toHaveBeenCalledWith(topicModeling);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<TopicModeling>>();
        const topicModeling = { id: 123 };
        jest.spyOn(topicModelingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ topicModeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(topicModelingService.update).toHaveBeenCalledWith(topicModeling);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackKeywordById', () => {
        it('Should return tracked Keyword primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackKeywordById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
