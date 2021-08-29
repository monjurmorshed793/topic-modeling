import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Framework } from 'app/entities/enumerations/framework.model';
import { IKeyword, Keyword } from '../keyword.model';

import { KeywordService } from './keyword.service';

describe('Service Tests', () => {
  describe('Keyword Service', () => {
    let service: KeywordService;
    let httpMock: HttpTestingController;
    let elemDefault: IKeyword;
    let expectedResult: IKeyword | IKeyword[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(KeywordService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        framework: Framework.REACT_NATIVE,
        topicNumber: 0,
        keywords: 'AAAAAAA',
        numberOfPosts: 0,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign({}, elemDefault);

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a Keyword', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Keyword()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Keyword', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            framework: 'BBBBBB',
            topicNumber: 1,
            keywords: 'BBBBBB',
            numberOfPosts: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Keyword', () => {
        const patchObject = Object.assign(
          {
            framework: 'BBBBBB',
            keywords: 'BBBBBB',
            numberOfPosts: 1,
          },
          new Keyword()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Keyword', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            framework: 'BBBBBB',
            topicNumber: 1,
            keywords: 'BBBBBB',
            numberOfPosts: 1,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a Keyword', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addKeywordToCollectionIfMissing', () => {
        it('should add a Keyword to an empty array', () => {
          const keyword: IKeyword = { id: 123 };
          expectedResult = service.addKeywordToCollectionIfMissing([], keyword);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(keyword);
        });

        it('should not add a Keyword to an array that contains it', () => {
          const keyword: IKeyword = { id: 123 };
          const keywordCollection: IKeyword[] = [
            {
              ...keyword,
            },
            { id: 456 },
          ];
          expectedResult = service.addKeywordToCollectionIfMissing(keywordCollection, keyword);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Keyword to an array that doesn't contain it", () => {
          const keyword: IKeyword = { id: 123 };
          const keywordCollection: IKeyword[] = [{ id: 456 }];
          expectedResult = service.addKeywordToCollectionIfMissing(keywordCollection, keyword);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(keyword);
        });

        it('should add only unique Keyword to an array', () => {
          const keywordArray: IKeyword[] = [{ id: 123 }, { id: 456 }, { id: 1369 }];
          const keywordCollection: IKeyword[] = [{ id: 123 }];
          expectedResult = service.addKeywordToCollectionIfMissing(keywordCollection, ...keywordArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const keyword: IKeyword = { id: 123 };
          const keyword2: IKeyword = { id: 456 };
          expectedResult = service.addKeywordToCollectionIfMissing([], keyword, keyword2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(keyword);
          expect(expectedResult).toContain(keyword2);
        });

        it('should accept null and undefined values', () => {
          const keyword: IKeyword = { id: 123 };
          expectedResult = service.addKeywordToCollectionIfMissing([], null, keyword, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(keyword);
        });

        it('should return initial array if no Keyword is added', () => {
          const keywordCollection: IKeyword[] = [{ id: 123 }];
          expectedResult = service.addKeywordToCollectionIfMissing(keywordCollection, undefined, null);
          expect(expectedResult).toEqual(keywordCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
