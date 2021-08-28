package bd.ac.buet.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A TopicModeling.
 */
@Table("topic_modeling")
public class TopicModeling implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("user_name")
    private String userName;

    @Column("category")
    private String category;

    @Column("sub_category")
    private String subCategory;

    @Column("topic")
    private String topic;

    @Column("sub_topic")
    private String subTopic;

    @Transient
    private Keyword keyword;

    @Column("keyword_id")
    private Long keywordId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TopicModeling id(Long id) {
        this.id = id;
        return this;
    }

    public String getUserName() {
        return this.userName;
    }

    public TopicModeling userName(String userName) {
        this.userName = userName;
        return this;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCategory() {
        return this.category;
    }

    public TopicModeling category(String category) {
        this.category = category;
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return this.subCategory;
    }

    public TopicModeling subCategory(String subCategory) {
        this.subCategory = subCategory;
        return this;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getTopic() {
        return this.topic;
    }

    public TopicModeling topic(String topic) {
        this.topic = topic;
        return this;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubTopic() {
        return this.subTopic;
    }

    public TopicModeling subTopic(String subTopic) {
        this.subTopic = subTopic;
        return this;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public Keyword getKeyword() {
        return this.keyword;
    }

    public TopicModeling keyword(Keyword keyword) {
        this.setKeyword(keyword);
        this.keywordId = keyword != null ? keyword.getId() : null;
        return this;
    }

    public void setKeyword(Keyword keyword) {
        this.keyword = keyword;
        this.keywordId = keyword != null ? keyword.getId() : null;
    }

    public Long getKeywordId() {
        return this.keywordId;
    }

    public void setKeywordId(Long keyword) {
        this.keywordId = keyword;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TopicModeling)) {
            return false;
        }
        return id != null && id.equals(((TopicModeling) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TopicModeling{" +
            "id=" + getId() +
            ", userName='" + getUserName() + "'" +
            ", category='" + getCategory() + "'" +
            ", subCategory='" + getSubCategory() + "'" +
            ", topic='" + getTopic() + "'" +
            ", subTopic='" + getSubTopic() + "'" +
            "}";
    }
}
