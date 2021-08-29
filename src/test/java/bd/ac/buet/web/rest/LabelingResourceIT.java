package bd.ac.buet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.ac.buet.IntegrationTest;
import bd.ac.buet.domain.Labeling;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.repository.LabelingRepository;
import bd.ac.buet.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link LabelingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class LabelingResourceIT {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final Framework DEFAULT_FRAMEWORK = Framework.REACT_NATIVE;
    private static final Framework UPDATED_FRAMEWORK = Framework.FLUTTER;

    private static final Integer DEFAULT_DOCUMENT_NO = 1;
    private static final Integer UPDATED_DOCUMENT_NO = 2;

    private static final Integer DEFAULT_DOMINANT_TOPIC = 1;
    private static final Integer UPDATED_DOMINANT_TOPIC = 2;

    private static final Double DEFAULT_TOPIC_PERC_CONTRIB = 1D;
    private static final Double UPDATED_TOPIC_PERC_CONTRIB = 2D;

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_TEXTS = "AAAAAAAAAA";
    private static final String UPDATED_TEXTS = "BBBBBBBBBB";

    private static final String DEFAULT_ANSWER = "AAAAAAAAAA";
    private static final String UPDATED_ANSWER = "BBBBBBBBBB";

    private static final String DEFAULT_LABEL = "AAAAAAAAAA";
    private static final String UPDATED_LABEL = "BBBBBBBBBB";

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/labelings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LabelingRepository labelingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Labeling labeling;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Labeling createEntity(EntityManager em) {
        Labeling labeling = new Labeling()
            .userName(DEFAULT_USER_NAME)
            .framework(DEFAULT_FRAMEWORK)
            .documentNo(DEFAULT_DOCUMENT_NO)
            .dominantTopic(DEFAULT_DOMINANT_TOPIC)
            .topicPercContrib(DEFAULT_TOPIC_PERC_CONTRIB)
            .keywords(DEFAULT_KEYWORDS)
            .title(DEFAULT_TITLE)
            .texts(DEFAULT_TEXTS)
            .answer(DEFAULT_ANSWER)
            .label(DEFAULT_LABEL)
            .reason(DEFAULT_REASON);
        return labeling;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Labeling createUpdatedEntity(EntityManager em) {
        Labeling labeling = new Labeling()
            .userName(UPDATED_USER_NAME)
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER)
            .label(UPDATED_LABEL)
            .reason(UPDATED_REASON);
        return labeling;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Labeling.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        labeling = createEntity(em);
    }

    @Test
    void createLabeling() throws Exception {
        int databaseSizeBeforeCreate = labelingRepository.findAll().collectList().block().size();
        // Create the Labeling
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeCreate + 1);
        Labeling testLabeling = labelingList.get(labelingList.size() - 1);
        assertThat(testLabeling.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testLabeling.getFramework()).isEqualTo(DEFAULT_FRAMEWORK);
        assertThat(testLabeling.getDocumentNo()).isEqualTo(DEFAULT_DOCUMENT_NO);
        assertThat(testLabeling.getDominantTopic()).isEqualTo(DEFAULT_DOMINANT_TOPIC);
        assertThat(testLabeling.getTopicPercContrib()).isEqualTo(DEFAULT_TOPIC_PERC_CONTRIB);
        assertThat(testLabeling.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testLabeling.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testLabeling.getTexts()).isEqualTo(DEFAULT_TEXTS);
        assertThat(testLabeling.getAnswer()).isEqualTo(DEFAULT_ANSWER);
        assertThat(testLabeling.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testLabeling.getReason()).isEqualTo(DEFAULT_REASON);
    }

    @Test
    void createLabelingWithExistingId() throws Exception {
        // Create the Labeling with an existing ID
        labeling.setId(1L);

        int databaseSizeBeforeCreate = labelingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLabelings() {
        // Initialize the database
        labelingRepository.save(labeling).block();

        // Get all the labelingList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(labeling.getId().intValue()))
            .jsonPath("$.[*].userName")
            .value(hasItem(DEFAULT_USER_NAME))
            .jsonPath("$.[*].framework")
            .value(hasItem(DEFAULT_FRAMEWORK.toString()))
            .jsonPath("$.[*].documentNo")
            .value(hasItem(DEFAULT_DOCUMENT_NO))
            .jsonPath("$.[*].dominantTopic")
            .value(hasItem(DEFAULT_DOMINANT_TOPIC))
            .jsonPath("$.[*].topicPercContrib")
            .value(hasItem(DEFAULT_TOPIC_PERC_CONTRIB.doubleValue()))
            .jsonPath("$.[*].keywords")
            .value(hasItem(DEFAULT_KEYWORDS))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE.toString()))
            .jsonPath("$.[*].texts")
            .value(hasItem(DEFAULT_TEXTS.toString()))
            .jsonPath("$.[*].answer")
            .value(hasItem(DEFAULT_ANSWER.toString()))
            .jsonPath("$.[*].label")
            .value(hasItem(DEFAULT_LABEL))
            .jsonPath("$.[*].reason")
            .value(hasItem(DEFAULT_REASON.toString()));
    }

    @Test
    void getLabeling() {
        // Initialize the database
        labelingRepository.save(labeling).block();

        // Get the labeling
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, labeling.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(labeling.getId().intValue()))
            .jsonPath("$.userName")
            .value(is(DEFAULT_USER_NAME))
            .jsonPath("$.framework")
            .value(is(DEFAULT_FRAMEWORK.toString()))
            .jsonPath("$.documentNo")
            .value(is(DEFAULT_DOCUMENT_NO))
            .jsonPath("$.dominantTopic")
            .value(is(DEFAULT_DOMINANT_TOPIC))
            .jsonPath("$.topicPercContrib")
            .value(is(DEFAULT_TOPIC_PERC_CONTRIB.doubleValue()))
            .jsonPath("$.keywords")
            .value(is(DEFAULT_KEYWORDS))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE.toString()))
            .jsonPath("$.texts")
            .value(is(DEFAULT_TEXTS.toString()))
            .jsonPath("$.answer")
            .value(is(DEFAULT_ANSWER.toString()))
            .jsonPath("$.label")
            .value(is(DEFAULT_LABEL))
            .jsonPath("$.reason")
            .value(is(DEFAULT_REASON.toString()));
    }

    @Test
    void getNonExistingLabeling() {
        // Get the labeling
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewLabeling() throws Exception {
        // Initialize the database
        labelingRepository.save(labeling).block();

        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();

        // Update the labeling
        Labeling updatedLabeling = labelingRepository.findById(labeling.getId()).block();
        updatedLabeling
            .userName(UPDATED_USER_NAME)
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER)
            .label(UPDATED_LABEL)
            .reason(UPDATED_REASON);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedLabeling.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedLabeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
        Labeling testLabeling = labelingList.get(labelingList.size() - 1);
        assertThat(testLabeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testLabeling.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testLabeling.getDocumentNo()).isEqualTo(UPDATED_DOCUMENT_NO);
        assertThat(testLabeling.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testLabeling.getTopicPercContrib()).isEqualTo(UPDATED_TOPIC_PERC_CONTRIB);
        assertThat(testLabeling.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testLabeling.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLabeling.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testLabeling.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testLabeling.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testLabeling.getReason()).isEqualTo(UPDATED_REASON);
    }

    @Test
    void putNonExistingLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, labeling.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLabelingWithPatch() throws Exception {
        // Initialize the database
        labelingRepository.save(labeling).block();

        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();

        // Update the labeling using partial update
        Labeling partialUpdatedLabeling = new Labeling();
        partialUpdatedLabeling.setId(labeling.getId());

        partialUpdatedLabeling
            .userName(UPDATED_USER_NAME)
            .framework(UPDATED_FRAMEWORK)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER)
            .reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLabeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLabeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
        Labeling testLabeling = labelingList.get(labelingList.size() - 1);
        assertThat(testLabeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testLabeling.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testLabeling.getDocumentNo()).isEqualTo(DEFAULT_DOCUMENT_NO);
        assertThat(testLabeling.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testLabeling.getTopicPercContrib()).isEqualTo(DEFAULT_TOPIC_PERC_CONTRIB);
        assertThat(testLabeling.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testLabeling.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLabeling.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testLabeling.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testLabeling.getLabel()).isEqualTo(DEFAULT_LABEL);
        assertThat(testLabeling.getReason()).isEqualTo(UPDATED_REASON);
    }

    @Test
    void fullUpdateLabelingWithPatch() throws Exception {
        // Initialize the database
        labelingRepository.save(labeling).block();

        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();

        // Update the labeling using partial update
        Labeling partialUpdatedLabeling = new Labeling();
        partialUpdatedLabeling.setId(labeling.getId());

        partialUpdatedLabeling
            .userName(UPDATED_USER_NAME)
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER)
            .label(UPDATED_LABEL)
            .reason(UPDATED_REASON);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLabeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLabeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
        Labeling testLabeling = labelingList.get(labelingList.size() - 1);
        assertThat(testLabeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testLabeling.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testLabeling.getDocumentNo()).isEqualTo(UPDATED_DOCUMENT_NO);
        assertThat(testLabeling.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testLabeling.getTopicPercContrib()).isEqualTo(UPDATED_TOPIC_PERC_CONTRIB);
        assertThat(testLabeling.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testLabeling.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testLabeling.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testLabeling.getAnswer()).isEqualTo(UPDATED_ANSWER);
        assertThat(testLabeling.getLabel()).isEqualTo(UPDATED_LABEL);
        assertThat(testLabeling.getReason()).isEqualTo(UPDATED_REASON);
    }

    @Test
    void patchNonExistingLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, labeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLabeling() throws Exception {
        int databaseSizeBeforeUpdate = labelingRepository.findAll().collectList().block().size();
        labeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(labeling))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Labeling in the database
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLabeling() {
        // Initialize the database
        labelingRepository.save(labeling).block();

        int databaseSizeBeforeDelete = labelingRepository.findAll().collectList().block().size();

        // Delete the labeling
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, labeling.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Labeling> labelingList = labelingRepository.findAll().collectList().block();
        assertThat(labelingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
