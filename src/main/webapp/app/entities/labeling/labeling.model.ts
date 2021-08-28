import { Framework } from 'app/entities/enumerations/framework.model';

export interface ILabeling {
  id?: number;
  userName?: string | null;
  framework?: Framework | null;
  documentNo?: number | null;
  dominantTopic?: number | null;
  topicPercContrib?: number | null;
  keywords?: string | null;
  title?: string | null;
  texts?: string | null;
  answer?: string | null;
  label?: string | null;
  reason?: string | null;
}

export class Labeling implements ILabeling {
  constructor(
    public id?: number,
    public userName?: string | null,
    public framework?: Framework | null,
    public documentNo?: number | null,
    public dominantTopic?: number | null,
    public topicPercContrib?: number | null,
    public keywords?: string | null,
    public title?: string | null,
    public texts?: string | null,
    public answer?: string | null,
    public label?: string | null,
    public reason?: string | null
  ) {}
}

export function getLabelingIdentifier(labeling: ILabeling): number | undefined {
  return labeling.id;
}
