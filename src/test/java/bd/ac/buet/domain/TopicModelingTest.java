package bd.ac.buet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.ac.buet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TopicModelingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TopicModeling.class);
        TopicModeling topicModeling1 = new TopicModeling();
        topicModeling1.setId(1L);
        TopicModeling topicModeling2 = new TopicModeling();
        topicModeling2.setId(topicModeling1.getId());
        assertThat(topicModeling1).isEqualTo(topicModeling2);
        topicModeling2.setId(2L);
        assertThat(topicModeling1).isNotEqualTo(topicModeling2);
        topicModeling1.setId(null);
        assertThat(topicModeling1).isNotEqualTo(topicModeling2);
    }
}
