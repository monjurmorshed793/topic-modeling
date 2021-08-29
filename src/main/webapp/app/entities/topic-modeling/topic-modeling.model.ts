import { IKeyword } from 'app/entities/keyword/keyword.model';

export interface ITopicModeling {
  id?: number;
  userName?: string | null;
  category?: string | null;
  subCategory?: string | null;
  topic?: string | null;
  subTopic?: string | null;
  keyword?: IKeyword | null;
}

export class TopicModeling implements ITopicModeling {
  constructor(
    public id?: number,
    public userName?: string | null,
    public category?: string | null,
    public subCategory?: string | null,
    public topic?: string | null,
    public subTopic?: string | null,
    public keyword?: IKeyword | null
  ) {}
}

export function getTopicModelingIdentifier(topicModeling: ITopicModeling): number | undefined {
  return topicModeling.id;
}
