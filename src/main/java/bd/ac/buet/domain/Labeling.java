package bd.ac.buet.domain;

import bd.ac.buet.domain.enumeration.Framework;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Labeling.
 */
@Table("labeling")
public class Labeling implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("user_name")
    private String userName;

    @Column("framework")
    private Framework framework;

    @Column("document_no")
    private Integer documentNo;

    @Column("dominant_topic")
    private Integer dominantTopic;

    @Column("topic_perc_contrib")
    private Double topicPercContrib;

    @Column("keywords")
    private String keywords;

    @Column("title")
    private String title;

    @Column("texts")
    private String texts;

    @Column("answer")
    private String answer;

    @Column("label")
    private String label;

    @Column("reason")
    private String reason;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Labeling id(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public Labeling userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Framework getFramework() {
        return this.framework;
    }

    public Labeling framework(Framework framework) {
        this.framework = framework;
        return this;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public Integer getDocumentNo() {
        return this.documentNo;
    }

    public Labeling documentNo(Integer documentNo) {
        this.documentNo = documentNo;
        return this;
    }

    public void setDocumentNo(Integer documentNo) {
        this.documentNo = documentNo;
    }

    public Integer getDominantTopic() {
        return this.dominantTopic;
    }

    public Labeling dominantTopic(Integer dominantTopic) {
        this.dominantTopic = dominantTopic;
        return this;
    }

    public void setDominantTopic(Integer dominantTopic) {
        this.dominantTopic = dominantTopic;
    }

    public Double getTopicPercContrib() {
        return this.topicPercContrib;
    }

    public Labeling topicPercContrib(Double topicPercContrib) {
        this.topicPercContrib = topicPercContrib;
        return this;
    }

    public void setTopicPercContrib(Double topicPercContrib) {
        this.topicPercContrib = topicPercContrib;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Labeling keywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getTitle() {
        return this.title;
    }

    public Labeling title(String title) {
        this.title = title;
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTexts() {
        return this.texts;
    }

    public Labeling texts(String texts) {
        this.texts = texts;
        return this;
    }

    public void setTexts(String texts) {
        this.texts = texts;
    }

    public String getAnswer() {
        return this.answer;
    }

    public Labeling answer(String answer) {
        this.answer = answer;
        return this;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getLabel() {
        return this.label;
    }

    public Labeling label(String label) {
        this.label = label;
        return this;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getReason() {
        return this.reason;
    }

    public Labeling reason(String reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Labeling)) {
            return false;
        }
        return id != null && id.equals(((Labeling) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Labeling{" +
            "id=" + getId() +
            ", userName='" + getUserName() + "'" +
            ", framework='" + getFramework() + "'" +
            ", documentNo=" + getDocumentNo() +
            ", dominantTopic=" + getDominantTopic() +
            ", topicPercContrib=" + getTopicPercContrib() +
            ", keywords='" + getKeywords() + "'" +
            ", title='" + getTitle() + "'" +
            ", texts='" + getTexts() + "'" +
            ", answer='" + getAnswer() + "'" +
            ", label='" + getLabel() + "'" +
            ", reason='" + getReason() + "'" +
            "}";
    }
}
