jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { KeywordService } from '../service/keyword.service';
import { IKeyword, Keyword } from '../keyword.model';

import { KeywordUpdateComponent } from './keyword-update.component';

describe('Component Tests', () => {
  describe('Keyword Management Update Component', () => {
    let comp: KeywordUpdateComponent;
    let fixture: ComponentFixture<KeywordUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let keywordService: KeywordService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [KeywordUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(KeywordUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(KeywordUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      keywordService = TestBed.inject(KeywordService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const keyword: IKeyword = { id: 456 };

        activatedRoute.data = of({ keyword });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(keyword));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Keyword>>();
        const keyword = { id: 123 };
        jest.spyOn(keywordService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ keyword });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: keyword }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(keywordService.update).toHaveBeenCalledWith(keyword);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Keyword>>();
        const keyword = new Keyword();
        jest.spyOn(keywordService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ keyword });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: keyword }));
        saveSubject.complete();

        // THEN
        expect(keywordService.create).toHaveBeenCalledWith(keyword);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Keyword>>();
        const keyword = { id: 123 };
        jest.spyOn(keywordService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ keyword });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(keywordService.update).toHaveBeenCalledWith(keyword);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
