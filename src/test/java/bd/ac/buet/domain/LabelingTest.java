package bd.ac.buet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import bd.ac.buet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LabelingTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Labeling.class);
        Labeling labeling1 = new Labeling();
        labeling1.setId(1L);
        Labeling labeling2 = new Labeling();
        labeling2.setId(labeling1.getId());
        assertThat(labeling1).isEqualTo(labeling2);
        labeling2.setId(2L);
        assertThat(labeling1).isNotEqualTo(labeling2);
        labeling1.setId(null);
        assertThat(labeling1).isNotEqualTo(labeling2);
    }
}
