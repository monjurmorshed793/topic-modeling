package bd.ac.buet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.ac.buet.IntegrationTest;
import bd.ac.buet.domain.TopicModeling;
import bd.ac.buet.repository.TopicModelingRepository;
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

/**
 * Integration tests for the {@link TopicModelingResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class TopicModelingResourceIT {

    private static final String DEFAULT_USER_NAME = "AAAAAAAAAA";
    private static final String UPDATED_USER_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_SUB_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_TOPIC = "AAAAAAAAAA";
    private static final String UPDATED_TOPIC = "BBBBBBBBBB";

    private static final String DEFAULT_SUB_TOPIC = "AAAAAAAAAA";
    private static final String UPDATED_SUB_TOPIC = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/topic-modelings";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TopicModelingRepository topicModelingRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TopicModeling topicModeling;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TopicModeling createEntity(EntityManager em) {
        TopicModeling topicModeling = new TopicModeling()
            .userName(DEFAULT_USER_NAME)
            .category(DEFAULT_CATEGORY)
            .subCategory(DEFAULT_SUB_CATEGORY)
            .topic(DEFAULT_TOPIC)
            .subTopic(DEFAULT_SUB_TOPIC);
        return topicModeling;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TopicModeling createUpdatedEntity(EntityManager em) {
        TopicModeling topicModeling = new TopicModeling()
            .userName(UPDATED_USER_NAME)
            .category(UPDATED_CATEGORY)
            .subCategory(UPDATED_SUB_CATEGORY)
            .topic(UPDATED_TOPIC)
            .subTopic(UPDATED_SUB_TOPIC);
        return topicModeling;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TopicModeling.class).block();
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
        topicModeling = createEntity(em);
    }

    @Test
    void createTopicModeling() throws Exception {
        int databaseSizeBeforeCreate = topicModelingRepository.findAll().collectList().block().size();
        // Create the TopicModeling
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeCreate + 1);
        TopicModeling testTopicModeling = topicModelingList.get(topicModelingList.size() - 1);
        assertThat(testTopicModeling.getUserName()).isEqualTo(DEFAULT_USER_NAME);
        assertThat(testTopicModeling.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTopicModeling.getSubCategory()).isEqualTo(DEFAULT_SUB_CATEGORY);
        assertThat(testTopicModeling.getTopic()).isEqualTo(DEFAULT_TOPIC);
        assertThat(testTopicModeling.getSubTopic()).isEqualTo(DEFAULT_SUB_TOPIC);
    }

    @Test
    void createTopicModelingWithExistingId() throws Exception {
        // Create the TopicModeling with an existing ID
        topicModeling.setId(1L);

        int databaseSizeBeforeCreate = topicModelingRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTopicModelings() {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        // Get all the topicModelingList
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
            .value(hasItem(topicModeling.getId().intValue()))
            .jsonPath("$.[*].userName")
            .value(hasItem(DEFAULT_USER_NAME))
            .jsonPath("$.[*].category")
            .value(hasItem(DEFAULT_CATEGORY))
            .jsonPath("$.[*].subCategory")
            .value(hasItem(DEFAULT_SUB_CATEGORY))
            .jsonPath("$.[*].topic")
            .value(hasItem(DEFAULT_TOPIC))
            .jsonPath("$.[*].subTopic")
            .value(hasItem(DEFAULT_SUB_TOPIC));
    }

    @Test
    void getTopicModeling() {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        // Get the topicModeling
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, topicModeling.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(topicModeling.getId().intValue()))
            .jsonPath("$.userName")
            .value(is(DEFAULT_USER_NAME))
            .jsonPath("$.category")
            .value(is(DEFAULT_CATEGORY))
            .jsonPath("$.subCategory")
            .value(is(DEFAULT_SUB_CATEGORY))
            .jsonPath("$.topic")
            .value(is(DEFAULT_TOPIC))
            .jsonPath("$.subTopic")
            .value(is(DEFAULT_SUB_TOPIC));
    }

    @Test
    void getNonExistingTopicModeling() {
        // Get the topicModeling
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTopicModeling() throws Exception {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();

        // Update the topicModeling
        TopicModeling updatedTopicModeling = topicModelingRepository.findById(topicModeling.getId()).block();
        updatedTopicModeling
            .userName(UPDATED_USER_NAME)
            .category(UPDATED_CATEGORY)
            .subCategory(UPDATED_SUB_CATEGORY)
            .topic(UPDATED_TOPIC)
            .subTopic(UPDATED_SUB_TOPIC);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTopicModeling.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTopicModeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
        TopicModeling testTopicModeling = topicModelingList.get(topicModelingList.size() - 1);
        assertThat(testTopicModeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testTopicModeling.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTopicModeling.getSubCategory()).isEqualTo(UPDATED_SUB_CATEGORY);
        assertThat(testTopicModeling.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testTopicModeling.getSubTopic()).isEqualTo(UPDATED_SUB_TOPIC);
    }

    @Test
    void putNonExistingTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, topicModeling.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTopicModelingWithPatch() throws Exception {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();

        // Update the topicModeling using partial update
        TopicModeling partialUpdatedTopicModeling = new TopicModeling();
        partialUpdatedTopicModeling.setId(topicModeling.getId());

        partialUpdatedTopicModeling.userName(UPDATED_USER_NAME).topic(UPDATED_TOPIC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTopicModeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTopicModeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
        TopicModeling testTopicModeling = topicModelingList.get(topicModelingList.size() - 1);
        assertThat(testTopicModeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testTopicModeling.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testTopicModeling.getSubCategory()).isEqualTo(DEFAULT_SUB_CATEGORY);
        assertThat(testTopicModeling.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testTopicModeling.getSubTopic()).isEqualTo(DEFAULT_SUB_TOPIC);
    }

    @Test
    void fullUpdateTopicModelingWithPatch() throws Exception {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();

        // Update the topicModeling using partial update
        TopicModeling partialUpdatedTopicModeling = new TopicModeling();
        partialUpdatedTopicModeling.setId(topicModeling.getId());

        partialUpdatedTopicModeling
            .userName(UPDATED_USER_NAME)
            .category(UPDATED_CATEGORY)
            .subCategory(UPDATED_SUB_CATEGORY)
            .topic(UPDATED_TOPIC)
            .subTopic(UPDATED_SUB_TOPIC);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTopicModeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTopicModeling))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
        TopicModeling testTopicModeling = topicModelingList.get(topicModelingList.size() - 1);
        assertThat(testTopicModeling.getUserName()).isEqualTo(UPDATED_USER_NAME);
        assertThat(testTopicModeling.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testTopicModeling.getSubCategory()).isEqualTo(UPDATED_SUB_CATEGORY);
        assertThat(testTopicModeling.getTopic()).isEqualTo(UPDATED_TOPIC);
        assertThat(testTopicModeling.getSubTopic()).isEqualTo(UPDATED_SUB_TOPIC);
    }

    @Test
    void patchNonExistingTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, topicModeling.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTopicModeling() throws Exception {
        int databaseSizeBeforeUpdate = topicModelingRepository.findAll().collectList().block().size();
        topicModeling.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(topicModeling))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TopicModeling in the database
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTopicModeling() {
        // Initialize the database
        topicModelingRepository.save(topicModeling).block();

        int databaseSizeBeforeDelete = topicModelingRepository.findAll().collectList().block().size();

        // Delete the topicModeling
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, topicModeling.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TopicModeling> topicModelingList = topicModelingRepository.findAll().collectList().block();
        assertThat(topicModelingList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
