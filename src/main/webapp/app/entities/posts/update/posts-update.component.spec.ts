jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { PostsService } from '../service/posts.service';
import { IPosts, Posts } from '../posts.model';

import { PostsUpdateComponent } from './posts-update.component';

describe('Component Tests', () => {
  describe('Posts Management Update Component', () => {
    let comp: PostsUpdateComponent;
    let fixture: ComponentFixture<PostsUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let postsService: PostsService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [PostsUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(PostsUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(PostsUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      postsService = TestBed.inject(PostsService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const posts: IPosts = { id: 456 };

        activatedRoute.data = of({ posts });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(posts));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Posts>>();
        const posts = { id: 123 };
        jest.spyOn(postsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ posts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: posts }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(postsService.update).toHaveBeenCalledWith(posts);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Posts>>();
        const posts = new Posts();
        jest.spyOn(postsService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ posts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: posts }));
        saveSubject.complete();

        // THEN
        expect(postsService.create).toHaveBeenCalledWith(posts);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Posts>>();
        const posts = { id: 123 };
        jest.spyOn(postsService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ posts });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(postsService.update).toHaveBeenCalledWith(posts);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
