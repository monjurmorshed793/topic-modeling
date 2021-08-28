import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { KeywordDetailComponent } from './keyword-detail.component';

describe('Component Tests', () => {
  describe('Keyword Management Detail Component', () => {
    let comp: KeywordDetailComponent;
    let fixture: ComponentFixture<KeywordDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [KeywordDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ keyword: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(KeywordDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(KeywordDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load keyword on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.keyword).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
