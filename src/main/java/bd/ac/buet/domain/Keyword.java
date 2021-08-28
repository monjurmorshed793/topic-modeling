package bd.ac.buet.domain;

import bd.ac.buet.domain.enumeration.Framework;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Keyword.
 */
@Table("keyword")
public class Keyword implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("framework")
    private Framework framework;

    @Column("topic_number")
    private Integer topicNumber;

    @Column("keywords")
    private String keywords;

    @Column("number_of_posts")
    private Integer numberOfPosts;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Keyword id(Long id) {
        this.id = id;
        return this;
    }

    public Framework getFramework() {
        return this.framework;
    }

    public Keyword framework(Framework framework) {
        this.framework = framework;
        return this;
    }

    public void setFramework(Framework framework) {
        this.framework = framework;
    }

    public Integer getTopicNumber() {
        return this.topicNumber;
    }

    public Keyword topicNumber(Integer topicNumber) {
        this.topicNumber = topicNumber;
        return this;
    }

    public void setTopicNumber(Integer topicNumber) {
        this.topicNumber = topicNumber;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Keyword keywords(String keywords) {
        this.keywords = keywords;
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public Integer getNumberOfPosts() {
        return this.numberOfPosts;
    }

    public Keyword numberOfPosts(Integer numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
        return this;
    }

    public void setNumberOfPosts(Integer numberOfPosts) {
        this.numberOfPosts = numberOfPosts;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Keyword)) {
            return false;
        }
        return id != null && id.equals(((Keyword) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Keyword{" +
            "id=" + getId() +
            ", framework='" + getFramework() + "'" +
            ", topicNumber=" + getTopicNumber() +
            ", keywords='" + getKeywords() + "'" +
            ", numberOfPosts=" + getNumberOfPosts() +
            "}";
    }
}
