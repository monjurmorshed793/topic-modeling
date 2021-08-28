import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITopicModeling, TopicModeling } from '../topic-modeling.model';

import { TopicModelingService } from './topic-modeling.service';

describe('Service Tests', () => {
  describe('TopicModeling Service', () => {
    let service: TopicModelingService;
    let httpMock: HttpTestingController;
    let elemDefault: ITopicModeling;
    let expectedResult: ITopicModeling | ITopicModeling[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(TopicModelingService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        userName: 'AAAAAAA',
        category: 'AAAAAAA',
        subCategory: 'AAAAAAA',
        topic: 'AAAAAAA',
        subTopic: 'AAAAAAA',
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

      it('should create a TopicModeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new TopicModeling()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a TopicModeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            userName: 'BBBBBB',
            category: 'BBBBBB',
            subCategory: 'BBBBBB',
            topic: 'BBBBBB',
            subTopic: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a TopicModeling', () => {
        const patchObject = Object.assign(
          {
            userName: 'BBBBBB',
            category: 'BBBBBB',
            topic: 'BBBBBB',
            subTopic: 'BBBBBB',
          },
          new TopicModeling()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of TopicModeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            userName: 'BBBBBB',
            category: 'BBBBBB',
            subCategory: 'BBBBBB',
            topic: 'BBBBBB',
            subTopic: 'BBBBBB',
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

      it('should delete a TopicModeling', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addTopicModelingToCollectionIfMissing', () => {
        it('should add a TopicModeling to an empty array', () => {
          const topicModeling: ITopicModeling = { id: 123 };
          expectedResult = service.addTopicModelingToCollectionIfMissing([], topicModeling);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(topicModeling);
        });

        it('should not add a TopicModeling to an array that contains it', () => {
          const topicModeling: ITopicModeling = { id: 123 };
          const topicModelingCollection: ITopicModeling[] = [
            {
              ...topicModeling,
            },
            { id: 456 },
          ];
          expectedResult = service.addTopicModelingToCollectionIfMissing(topicModelingCollection, topicModeling);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a TopicModeling to an array that doesn't contain it", () => {
          const topicModeling: ITopicModeling = { id: 123 };
          const topicModelingCollection: ITopicModeling[] = [{ id: 456 }];
          expectedResult = service.addTopicModelingToCollectionIfMissing(topicModelingCollection, topicModeling);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(topicModeling);
        });

        it('should add only unique TopicModeling to an array', () => {
          const topicModelingArray: ITopicModeling[] = [{ id: 123 }, { id: 456 }, { id: 62952 }];
          const topicModelingCollection: ITopicModeling[] = [{ id: 123 }];
          expectedResult = service.addTopicModelingToCollectionIfMissing(topicModelingCollection, ...topicModelingArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const topicModeling: ITopicModeling = { id: 123 };
          const topicModeling2: ITopicModeling = { id: 456 };
          expectedResult = service.addTopicModelingToCollectionIfMissing([], topicModeling, topicModeling2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(topicModeling);
          expect(expectedResult).toContain(topicModeling2);
        });

        it('should accept null and undefined values', () => {
          const topicModeling: ITopicModeling = { id: 123 };
          expectedResult = service.addTopicModelingToCollectionIfMissing([], null, topicModeling, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(topicModeling);
        });

        it('should return initial array if no TopicModeling is added', () => {
          const topicModelingCollection: ITopicModeling[] = [{ id: 123 }];
          expectedResult = service.addTopicModelingToCollectionIfMissing(topicModelingCollection, undefined, null);
          expect(expectedResult).toEqual(topicModelingCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
