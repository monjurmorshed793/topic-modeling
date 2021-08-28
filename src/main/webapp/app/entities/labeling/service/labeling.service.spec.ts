import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { Framework } from 'app/entities/enumerations/framework.model';
import { ILabeling, Labeling } from '../labeling.model';

import { LabelingService } from './labeling.service';

describe('Service Tests', () => {
  describe('Labeling Service', () => {
    let service: LabelingService;
    let httpMock: HttpTestingController;
    let elemDefault: ILabeling;
    let expectedResult: ILabeling | ILabeling[] | boolean | null;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(LabelingService);
      httpMock = TestBed.inject(HttpTestingController);

      elemDefault = {
        id: 0,
        userName: 'AAAAAAA',
        framework: Framework.REACT_NATIVE,
        documentNo: 0,
        dominantTopic: 0,
        topicPercContrib: 0,
        keywords: 'AAAAAAA',
        title: 'AAAAAAA',
        texts: 'AAAAAAA',
        answer: 'AAAAAAA',
        label: 'AAAAAAA',
        reason: 'AAAAAAA',
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

      it('should create a Labeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.create(new Labeling()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a Labeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            userName: 'BBBBBB',
            framework: 'BBBBBB',
            documentNo: 1,
            dominantTopic: 1,
            topicPercContrib: 1,
            keywords: 'BBBBBB',
            title: 'BBBBBB',
            texts: 'BBBBBB',
            answer: 'BBBBBB',
            label: 'BBBBBB',
            reason: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign({}, returnedFromService);

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a Labeling', () => {
        const patchObject = Object.assign(
          {
            userName: 'BBBBBB',
            documentNo: 1,
            topicPercContrib: 1,
            texts: 'BBBBBB',
            label: 'BBBBBB',
          },
          new Labeling()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign({}, returnedFromService);

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of Labeling', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            userName: 'BBBBBB',
            framework: 'BBBBBB',
            documentNo: 1,
            dominantTopic: 1,
            topicPercContrib: 1,
            keywords: 'BBBBBB',
            title: 'BBBBBB',
            texts: 'BBBBBB',
            answer: 'BBBBBB',
            label: 'BBBBBB',
            reason: 'BBBBBB',
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

      it('should delete a Labeling', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addLabelingToCollectionIfMissing', () => {
        it('should add a Labeling to an empty array', () => {
          const labeling: ILabeling = { id: 123 };
          expectedResult = service.addLabelingToCollectionIfMissing([], labeling);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(labeling);
        });

        it('should not add a Labeling to an array that contains it', () => {
          const labeling: ILabeling = { id: 123 };
          const labelingCollection: ILabeling[] = [
            {
              ...labeling,
            },
            { id: 456 },
          ];
          expectedResult = service.addLabelingToCollectionIfMissing(labelingCollection, labeling);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a Labeling to an array that doesn't contain it", () => {
          const labeling: ILabeling = { id: 123 };
          const labelingCollection: ILabeling[] = [{ id: 456 }];
          expectedResult = service.addLabelingToCollectionIfMissing(labelingCollection, labeling);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(labeling);
        });

        it('should add only unique Labeling to an array', () => {
          const labelingArray: ILabeling[] = [{ id: 123 }, { id: 456 }, { id: 2471 }];
          const labelingCollection: ILabeling[] = [{ id: 123 }];
          expectedResult = service.addLabelingToCollectionIfMissing(labelingCollection, ...labelingArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const labeling: ILabeling = { id: 123 };
          const labeling2: ILabeling = { id: 456 };
          expectedResult = service.addLabelingToCollectionIfMissing([], labeling, labeling2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(labeling);
          expect(expectedResult).toContain(labeling2);
        });

        it('should accept null and undefined values', () => {
          const labeling: ILabeling = { id: 123 };
          expectedResult = service.addLabelingToCollectionIfMissing([], null, labeling, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(labeling);
        });

        it('should return initial array if no Labeling is added', () => {
          const labelingCollection: ILabeling[] = [{ id: 123 }];
          expectedResult = service.addLabelingToCollectionIfMissing(labelingCollection, undefined, null);
          expect(expectedResult).toEqual(labelingCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
