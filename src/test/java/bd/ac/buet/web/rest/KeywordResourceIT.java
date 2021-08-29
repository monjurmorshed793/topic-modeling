package bd.ac.buet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.ac.buet.IntegrationTest;
import bd.ac.buet.domain.Keyword;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.repository.KeywordRepository;
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
 * Integration tests for the {@link KeywordResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class KeywordResourceIT {

    private static final Framework DEFAULT_FRAMEWORK = Framework.REACT_NATIVE;
    private static final Framework UPDATED_FRAMEWORK = Framework.FLUTTER;

    private static final Integer DEFAULT_TOPIC_NUMBER = 1;
    private static final Integer UPDATED_TOPIC_NUMBER = 2;

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMBER_OF_POSTS = 1;
    private static final Integer UPDATED_NUMBER_OF_POSTS = 2;

    private static final String ENTITY_API_URL = "/api/keywords";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Keyword keyword;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Keyword createEntity(EntityManager em) {
        Keyword keyword = new Keyword()
            .framework(DEFAULT_FRAMEWORK)
            .topicNumber(DEFAULT_TOPIC_NUMBER)
            .keywords(DEFAULT_KEYWORDS)
            .numberOfPosts(DEFAULT_NUMBER_OF_POSTS);
        return keyword;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Keyword createUpdatedEntity(EntityManager em) {
        Keyword keyword = new Keyword()
            .framework(UPDATED_FRAMEWORK)
            .topicNumber(UPDATED_TOPIC_NUMBER)
            .keywords(UPDATED_KEYWORDS)
            .numberOfPosts(UPDATED_NUMBER_OF_POSTS);
        return keyword;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Keyword.class).block();
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
        keyword = createEntity(em);
    }

    @Test
    void createKeyword() throws Exception {
        int databaseSizeBeforeCreate = keywordRepository.findAll().collectList().block().size();
        // Create the Keyword
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeCreate + 1);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getFramework()).isEqualTo(DEFAULT_FRAMEWORK);
        assertThat(testKeyword.getTopicNumber()).isEqualTo(DEFAULT_TOPIC_NUMBER);
        assertThat(testKeyword.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testKeyword.getNumberOfPosts()).isEqualTo(DEFAULT_NUMBER_OF_POSTS);
    }

    @Test
    void createKeywordWithExistingId() throws Exception {
        // Create the Keyword with an existing ID
        keyword.setId(1L);

        int databaseSizeBeforeCreate = keywordRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllKeywords() {
        // Initialize the database
        keywordRepository.save(keyword).block();

        // Get all the keywordList
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
            .value(hasItem(keyword.getId().intValue()))
            .jsonPath("$.[*].framework")
            .value(hasItem(DEFAULT_FRAMEWORK.toString()))
            .jsonPath("$.[*].topicNumber")
            .value(hasItem(DEFAULT_TOPIC_NUMBER))
            .jsonPath("$.[*].keywords")
            .value(hasItem(DEFAULT_KEYWORDS))
            .jsonPath("$.[*].numberOfPosts")
            .value(hasItem(DEFAULT_NUMBER_OF_POSTS));
    }

    @Test
    void getKeyword() {
        // Initialize the database
        keywordRepository.save(keyword).block();

        // Get the keyword
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, keyword.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(keyword.getId().intValue()))
            .jsonPath("$.framework")
            .value(is(DEFAULT_FRAMEWORK.toString()))
            .jsonPath("$.topicNumber")
            .value(is(DEFAULT_TOPIC_NUMBER))
            .jsonPath("$.keywords")
            .value(is(DEFAULT_KEYWORDS))
            .jsonPath("$.numberOfPosts")
            .value(is(DEFAULT_NUMBER_OF_POSTS));
    }

    @Test
    void getNonExistingKeyword() {
        // Get the keyword
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewKeyword() throws Exception {
        // Initialize the database
        keywordRepository.save(keyword).block();

        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();

        // Update the keyword
        Keyword updatedKeyword = keywordRepository.findById(keyword.getId()).block();
        updatedKeyword
            .framework(UPDATED_FRAMEWORK)
            .topicNumber(UPDATED_TOPIC_NUMBER)
            .keywords(UPDATED_KEYWORDS)
            .numberOfPosts(UPDATED_NUMBER_OF_POSTS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedKeyword.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedKeyword))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testKeyword.getTopicNumber()).isEqualTo(UPDATED_TOPIC_NUMBER);
        assertThat(testKeyword.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testKeyword.getNumberOfPosts()).isEqualTo(UPDATED_NUMBER_OF_POSTS);
    }

    @Test
    void putNonExistingKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, keyword.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateKeywordWithPatch() throws Exception {
        // Initialize the database
        keywordRepository.save(keyword).block();

        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();

        // Update the keyword using partial update
        Keyword partialUpdatedKeyword = new Keyword();
        partialUpdatedKeyword.setId(keyword.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKeyword.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKeyword))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getFramework()).isEqualTo(DEFAULT_FRAMEWORK);
        assertThat(testKeyword.getTopicNumber()).isEqualTo(DEFAULT_TOPIC_NUMBER);
        assertThat(testKeyword.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testKeyword.getNumberOfPosts()).isEqualTo(DEFAULT_NUMBER_OF_POSTS);
    }

    @Test
    void fullUpdateKeywordWithPatch() throws Exception {
        // Initialize the database
        keywordRepository.save(keyword).block();

        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();

        // Update the keyword using partial update
        Keyword partialUpdatedKeyword = new Keyword();
        partialUpdatedKeyword.setId(keyword.getId());

        partialUpdatedKeyword
            .framework(UPDATED_FRAMEWORK)
            .topicNumber(UPDATED_TOPIC_NUMBER)
            .keywords(UPDATED_KEYWORDS)
            .numberOfPosts(UPDATED_NUMBER_OF_POSTS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedKeyword.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedKeyword))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
        Keyword testKeyword = keywordList.get(keywordList.size() - 1);
        assertThat(testKeyword.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testKeyword.getTopicNumber()).isEqualTo(UPDATED_TOPIC_NUMBER);
        assertThat(testKeyword.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testKeyword.getNumberOfPosts()).isEqualTo(UPDATED_NUMBER_OF_POSTS);
    }

    @Test
    void patchNonExistingKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, keyword.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamKeyword() throws Exception {
        int databaseSizeBeforeUpdate = keywordRepository.findAll().collectList().block().size();
        keyword.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(keyword))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Keyword in the database
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteKeyword() {
        // Initialize the database
        keywordRepository.save(keyword).block();

        int databaseSizeBeforeDelete = keywordRepository.findAll().collectList().block().size();

        // Delete the keyword
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, keyword.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Keyword> keywordList = keywordRepository.findAll().collectList().block();
        assertThat(keywordList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
