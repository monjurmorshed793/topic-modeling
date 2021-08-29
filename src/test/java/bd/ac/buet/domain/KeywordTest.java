package bd.ac.buet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.ac.buet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class KeywordTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Keyword.class);
        Keyword keyword1 = new Keyword();
        keyword1.setId(1L);
        Keyword keyword2 = new Keyword();
        keyword2.setId(keyword1.getId());
        assertThat(keyword1).isEqualTo(keyword2);
        keyword2.setId(2L);
        assertThat(keyword1).isNotEqualTo(keyword2);
        keyword1.setId(null);
        assertThat(keyword1).isNotEqualTo(keyword2);
    }
}
