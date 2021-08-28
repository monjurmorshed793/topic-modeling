import { Framework } from 'app/entities/enumerations/framework.model';

export interface IKeyword {
  id?: number;
  framework?: Framework | null;
  topicNumber?: number | null;
  keywords?: string | null;
  numberOfPosts?: number | null;
}

export class Keyword implements IKeyword {
  constructor(
    public id?: number,
    public framework?: Framework | null,
    public topicNumber?: number | null,
    public keywords?: string | null,
    public numberOfPosts?: number | null
  ) {}
}

export function getKeywordIdentifier(keyword: IKeyword): number | undefined {
  return keyword.id;
}
