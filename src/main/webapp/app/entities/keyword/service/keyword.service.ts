import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IKeyword, getKeywordIdentifier } from '../keyword.model';

export type EntityResponseType = HttpResponse<IKeyword>;
export type EntityArrayResponseType = HttpResponse<IKeyword[]>;

@Injectable({ providedIn: 'root' })
export class KeywordService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/keywords');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(keyword: IKeyword): Observable<EntityResponseType> {
    return this.http.post<IKeyword>(this.resourceUrl, keyword, { observe: 'response' });
  }

  update(keyword: IKeyword): Observable<EntityResponseType> {
    return this.http.put<IKeyword>(`${this.resourceUrl}/${getKeywordIdentifier(keyword) as number}`, keyword, { observe: 'response' });
  }

  partialUpdate(keyword: IKeyword): Observable<EntityResponseType> {
    return this.http.patch<IKeyword>(`${this.resourceUrl}/${getKeywordIdentifier(keyword) as number}`, keyword, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IKeyword>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IKeyword[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addKeywordToCollectionIfMissing(keywordCollection: IKeyword[], ...keywordsToCheck: (IKeyword | null | undefined)[]): IKeyword[] {
    const keywords: IKeyword[] = keywordsToCheck.filter(isPresent);
    if (keywords.length > 0) {
      const keywordCollectionIdentifiers = keywordCollection.map(keywordItem => getKeywordIdentifier(keywordItem)!);
      const keywordsToAdd = keywords.filter(keywordItem => {
        const keywordIdentifier = getKeywordIdentifier(keywordItem);
        if (keywordIdentifier == null || keywordCollectionIdentifiers.includes(keywordIdentifier)) {
          return false;
        }
        keywordCollectionIdentifiers.push(keywordIdentifier);
        return true;
      });
      return [...keywordsToAdd, ...keywordCollection];
    }
    return keywordCollection;
  }
}
