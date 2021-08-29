jest.mock('@angular/router');

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';

import { LabelingService } from '../service/labeling.service';
import { ILabeling, Labeling } from '../labeling.model';

import { LabelingUpdateComponent } from './labeling-update.component';

describe('Component Tests', () => {
  describe('Labeling Management Update Component', () => {
    let comp: LabelingUpdateComponent;
    let fixture: ComponentFixture<LabelingUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let labelingService: LabelingService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [LabelingUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(LabelingUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(LabelingUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      labelingService = TestBed.inject(LabelingService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const labeling: ILabeling = { id: 456 };

        activatedRoute.data = of({ labeling });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(labeling));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Labeling>>();
        const labeling = { id: 123 };
        jest.spyOn(labelingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ labeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: labeling }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(labelingService.update).toHaveBeenCalledWith(labeling);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Labeling>>();
        const labeling = new Labeling();
        jest.spyOn(labelingService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ labeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: labeling }));
        saveSubject.complete();

        // THEN
        expect(labelingService.create).toHaveBeenCalledWith(labeling);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<Labeling>>();
        const labeling = { id: 123 };
        jest.spyOn(labelingService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ labeling });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(labelingService.update).toHaveBeenCalledWith(labeling);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
