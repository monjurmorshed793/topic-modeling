import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Framework } from 'app/entities/enumerations/framework.model';
import { IPosts, Posts } from '../posts.model';

import { PostsService } from './posts.service';

describe('Service Tests', () => {
  describe('Posts Service', () => {
    let service: PostsService;
    let httpMock: HttpTestingController;
    let elemDefault: IPosts;
    let expectedResult: IPosts | IPosts[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(PostsService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        framework: Framework.REACT_NATIVE,
        documentNo: 0,
        dominantTopic: 0,
        topicPercContrib: 0,
        keywords: 'AAAAAAA',
        title: 'AAAAAAA',
        texts: 'AAAAAAA',
        answer: 'AAAAAAA',
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

      it('should create a Posts', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Posts()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Posts', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            framework: 'BBBBBB',
            documentNo: 1,
            dominantTopic: 1,
            topicPercContrib: 1,
            keywords: 'BBBBBB',
            title: 'BBBBBB',
            texts: 'BBBBBB',
            answer: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Posts', () => {
        const patchObject = Object.assign(
          {
            framework: 'BBBBBB',
            dominantTopic: 1,
            topicPercContrib: 1,
            keywords: 'BBBBBB',
            texts: 'BBBBBB',
            answer: 'BBBBBB',
          },
          new Posts()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Posts', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            framework: 'BBBBBB',
            documentNo: 1,
            dominantTopic: 1,
            topicPercContrib: 1,
            keywords: 'BBBBBB',
            title: 'BBBBBB',
            texts: 'BBBBBB',
            answer: 'BBBBBB',
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

      it('should delete a Posts', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addPostsToCollectionIfMissing', () => {
        it('should add a Posts to an empty array', () => {
          const posts: IPosts = { id: 123 };
          expectedResult = service.addPostsToCollectionIfMissing([], posts);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(posts);
        });

        it('should not add a Posts to an array that contains it', () => {
          const posts: IPosts = { id: 123 };
          const postsCollection: IPosts[] = [
            {
              ...posts,
            },
            { id: 456 },
          ];
          expectedResult = service.addPostsToCollectionIfMissing(postsCollection, posts);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Posts to an array that doesn't contain it", () => {
          const posts: IPosts = { id: 123 };
          const postsCollection: IPosts[] = [{ id: 456 }];
          expectedResult = service.addPostsToCollectionIfMissing(postsCollection, posts);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(posts);
        });

        it('should add only unique Posts to an array', () => {
          const postsArray: IPosts[] = [{ id: 123 }, { id: 456 }, { id: 22934 }];
          const postsCollection: IPosts[] = [{ id: 123 }];
          expectedResult = service.addPostsToCollectionIfMissing(postsCollection, ...postsArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const posts: IPosts = { id: 123 };
          const posts2: IPosts = { id: 456 };
          expectedResult = service.addPostsToCollectionIfMissing([], posts, posts2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(posts);
          expect(expectedResult).toContain(posts2);
        });

        it('should accept null and undefined values', () => {
          const posts: IPosts = { id: 123 };
          expectedResult = service.addPostsToCollectionIfMissing([], null, posts, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(posts);
        });

        it('should return initial array if no Posts is added', () => {
          const postsCollection: IPosts[] = [{ id: 123 }];
          expectedResult = service.addPostsToCollectionIfMissing(postsCollection, undefined, null);
          expect(expectedResult).toEqual(postsCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
