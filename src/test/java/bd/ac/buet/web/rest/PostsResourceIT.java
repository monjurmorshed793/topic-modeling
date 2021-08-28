package bd.ac.buet.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import bd.ac.buet.IntegrationTest;
import bd.ac.buet.domain.Posts;
import bd.ac.buet.domain.enumeration.Framework;
import bd.ac.buet.repository.PostsRepository;
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
 * Integration tests for the {@link PostsResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PostsResourceIT {

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

    private static final String ENTITY_API_URL = "/api/posts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Posts posts;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createEntity(EntityManager em) {
        Posts posts = new Posts()
            .framework(DEFAULT_FRAMEWORK)
            .documentNo(DEFAULT_DOCUMENT_NO)
            .dominantTopic(DEFAULT_DOMINANT_TOPIC)
            .topicPercContrib(DEFAULT_TOPIC_PERC_CONTRIB)
            .keywords(DEFAULT_KEYWORDS)
            .title(DEFAULT_TITLE)
            .texts(DEFAULT_TEXTS)
            .answer(DEFAULT_ANSWER);
        return posts;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Posts createUpdatedEntity(EntityManager em) {
        Posts posts = new Posts()
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER);
        return posts;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Posts.class).block();
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
        posts = createEntity(em);
    }

    @Test
    void createPosts() throws Exception {
        int databaseSizeBeforeCreate = postsRepository.findAll().collectList().block().size();
        // Create the Posts
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeCreate + 1);
        Posts testPosts = postsList.get(postsList.size() - 1);
        assertThat(testPosts.getFramework()).isEqualTo(DEFAULT_FRAMEWORK);
        assertThat(testPosts.getDocumentNo()).isEqualTo(DEFAULT_DOCUMENT_NO);
        assertThat(testPosts.getDominantTopic()).isEqualTo(DEFAULT_DOMINANT_TOPIC);
        assertThat(testPosts.getTopicPercContrib()).isEqualTo(DEFAULT_TOPIC_PERC_CONTRIB);
        assertThat(testPosts.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testPosts.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPosts.getTexts()).isEqualTo(DEFAULT_TEXTS);
        assertThat(testPosts.getAnswer()).isEqualTo(DEFAULT_ANSWER);
    }

    @Test
    void createPostsWithExistingId() throws Exception {
        // Create the Posts with an existing ID
        posts.setId(1L);

        int databaseSizeBeforeCreate = postsRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPosts() {
        // Initialize the database
        postsRepository.save(posts).block();

        // Get all the postsList
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
            .value(hasItem(posts.getId().intValue()))
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
            .value(hasItem(DEFAULT_ANSWER.toString()));
    }

    @Test
    void getPosts() {
        // Initialize the database
        postsRepository.save(posts).block();

        // Get the posts
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, posts.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(posts.getId().intValue()))
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
            .value(is(DEFAULT_ANSWER.toString()));
    }

    @Test
    void getNonExistingPosts() {
        // Get the posts
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPosts() throws Exception {
        // Initialize the database
        postsRepository.save(posts).block();

        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();

        // Update the posts
        Posts updatedPosts = postsRepository.findById(posts.getId()).block();
        updatedPosts
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPosts.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPosts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
        Posts testPosts = postsList.get(postsList.size() - 1);
        assertThat(testPosts.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testPosts.getDocumentNo()).isEqualTo(UPDATED_DOCUMENT_NO);
        assertThat(testPosts.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testPosts.getTopicPercContrib()).isEqualTo(UPDATED_TOPIC_PERC_CONTRIB);
        assertThat(testPosts.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testPosts.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPosts.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testPosts.getAnswer()).isEqualTo(UPDATED_ANSWER);
    }

    @Test
    void putNonExistingPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, posts.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        postsRepository.save(posts).block();

        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setId(posts.getId());

        partialUpdatedPosts
            .framework(UPDATED_FRAMEWORK)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPosts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPosts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
        Posts testPosts = postsList.get(postsList.size() - 1);
        assertThat(testPosts.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testPosts.getDocumentNo()).isEqualTo(DEFAULT_DOCUMENT_NO);
        assertThat(testPosts.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testPosts.getTopicPercContrib()).isEqualTo(UPDATED_TOPIC_PERC_CONTRIB);
        assertThat(testPosts.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testPosts.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testPosts.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testPosts.getAnswer()).isEqualTo(UPDATED_ANSWER);
    }

    @Test
    void fullUpdatePostsWithPatch() throws Exception {
        // Initialize the database
        postsRepository.save(posts).block();

        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();

        // Update the posts using partial update
        Posts partialUpdatedPosts = new Posts();
        partialUpdatedPosts.setId(posts.getId());

        partialUpdatedPosts
            .framework(UPDATED_FRAMEWORK)
            .documentNo(UPDATED_DOCUMENT_NO)
            .dominantTopic(UPDATED_DOMINANT_TOPIC)
            .topicPercContrib(UPDATED_TOPIC_PERC_CONTRIB)
            .keywords(UPDATED_KEYWORDS)
            .title(UPDATED_TITLE)
            .texts(UPDATED_TEXTS)
            .answer(UPDATED_ANSWER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPosts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPosts))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
        Posts testPosts = postsList.get(postsList.size() - 1);
        assertThat(testPosts.getFramework()).isEqualTo(UPDATED_FRAMEWORK);
        assertThat(testPosts.getDocumentNo()).isEqualTo(UPDATED_DOCUMENT_NO);
        assertThat(testPosts.getDominantTopic()).isEqualTo(UPDATED_DOMINANT_TOPIC);
        assertThat(testPosts.getTopicPercContrib()).isEqualTo(UPDATED_TOPIC_PERC_CONTRIB);
        assertThat(testPosts.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testPosts.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testPosts.getTexts()).isEqualTo(UPDATED_TEXTS);
        assertThat(testPosts.getAnswer()).isEqualTo(UPDATED_ANSWER);
    }

    @Test
    void patchNonExistingPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, posts.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPosts() throws Exception {
        int databaseSizeBeforeUpdate = postsRepository.findAll().collectList().block().size();
        posts.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(posts))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Posts in the database
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePosts() {
        // Initialize the database
        postsRepository.save(posts).block();

        int databaseSizeBeforeDelete = postsRepository.findAll().collectList().block().size();

        // Delete the posts
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, posts.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Posts> postsList = postsRepository.findAll().collectList().block();
        assertThat(postsList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
