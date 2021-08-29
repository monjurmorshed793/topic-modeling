import { Framework } from 'app/entities/enumerations/framework.model';

export interface IPosts {
  id?: number;
  framework?: Framework | null;
  documentNo?: number | null;
  dominantTopic?: number | null;
  topicPercContrib?: number | null;
  keywords?: string | null;
  title?: string | null;
  texts?: string | null;
  answer?: string | null;
}

export class Posts implements IPosts {
  constructor(
    public id?: number,
    public framework?: Framework | null,
    public documentNo?: number | null,
    public dominantTopic?: number | null,
    public topicPercContrib?: number | null,
    public keywords?: string | null,
    public title?: string | null,
    public texts?: string | null,
    public answer?: string | null
  ) {}
}

export function getPostsIdentifier(posts: IPosts): number | undefined {
  return posts.id;
}
