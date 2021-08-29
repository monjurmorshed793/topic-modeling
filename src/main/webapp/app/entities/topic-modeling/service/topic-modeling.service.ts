import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITopicModeling, getTopicModelingIdentifier } from '../topic-modeling.model';

export type EntityResponseType = HttpResponse<ITopicModeling>;
export type EntityArrayResponseType = HttpResponse<ITopicModeling[]>;

@Injectable({ providedIn: 'root' })
export class TopicModelingService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/topic-modelings');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(topicModeling: ITopicModeling): Observable<EntityResponseType> {
    return this.http.post<ITopicModeling>(this.resourceUrl, topicModeling, { observe: 'response' });
  }

  update(topicModeling: ITopicModeling): Observable<EntityResponseType> {
    return this.http.put<ITopicModeling>(`${this.resourceUrl}/${getTopicModelingIdentifier(topicModeling) as number}`, topicModeling, {
      observe: 'response',
    });
  }

  partialUpdate(topicModeling: ITopicModeling): Observable<EntityResponseType> {
    return this.http.patch<ITopicModeling>(`${this.resourceUrl}/${getTopicModelingIdentifier(topicModeling) as number}`, topicModeling, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITopicModeling>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITopicModeling[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addTopicModelingToCollectionIfMissing(
    topicModelingCollection: ITopicModeling[],
    ...topicModelingsToCheck: (ITopicModeling | null | undefined)[]
  ): ITopicModeling[] {
    const topicModelings: ITopicModeling[] = topicModelingsToCheck.filter(isPresent);
    if (topicModelings.length > 0) {
      const topicModelingCollectionIdentifiers = topicModelingCollection.map(
        topicModelingItem => getTopicModelingIdentifier(topicModelingItem)!
      );
      const topicModelingsToAdd = topicModelings.filter(topicModelingItem => {
        const topicModelingIdentifier = getTopicModelingIdentifier(topicModelingItem);
        if (topicModelingIdentifier == null || topicModelingCollectionIdentifiers.includes(topicModelingIdentifier)) {
          return false;
        }
        topicModelingCollectionIdentifiers.push(topicModelingIdentifier);
        return true;
      });
      return [...topicModelingsToAdd, ...topicModelingCollection];
    }
    return topicModelingCollection;
  }
}
