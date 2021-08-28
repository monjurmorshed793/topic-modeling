import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ILabeling, getLabelingIdentifier } from '../labeling.model';

export type EntityResponseType = HttpResponse<ILabeling>;
export type EntityArrayResponseType = HttpResponse<ILabeling[]>;

@Injectable({ providedIn: 'root' })
export class LabelingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/labelings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(labeling: ILabeling): Observable<EntityResponseType> {
    return this.http.post<ILabeling>(this.resourceUrl, labeling, { observe: 'response' });
  }

  update(labeling: ILabeling): Observable<EntityResponseType> {
    return this.http.put<ILabeling>(`${this.resourceUrl}/${getLabelingIdentifier(labeling) as number}`, labeling, { observe: 'response' });
  }

  partialUpdate(labeling: ILabeling): Observable<EntityResponseType> {
    return this.http.patch<ILabeling>(`${this.resourceUrl}/${getLabelingIdentifier(labeling) as number}`, labeling, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ILabeling>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ILabeling[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addLabelingToCollectionIfMissing(labelingCollection: ILabeling[], ...labelingsToCheck: (ILabeling | null | undefined)[]): ILabeling[] {
    const labelings: ILabeling[] = labelingsToCheck.filter(isPresent);
    if (labelings.length > 0) {
      const labelingCollectionIdentifiers = labelingCollection.map(labelingItem => getLabelingIdentifier(labelingItem)!);
      const labelingsToAdd = labelings.filter(labelingItem => {
        const labelingIdentifier = getLabelingIdentifier(labelingItem);
        if (labelingIdentifier == null || labelingCollectionIdentifiers.includes(labelingIdentifier)) {
          return false;
        }
        labelingCollectionIdentifiers.push(labelingIdentifier);
        return true;
      });
      return [...labelingsToAdd, ...labelingCollection];
    }
    return labelingCollection;
  }
}
