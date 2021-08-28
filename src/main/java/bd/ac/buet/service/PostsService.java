package bd.ac.buet.service;

import bd.ac.buet.domain.Posts;
import bd.ac.buet.repository.PostsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Posts}.
 */
@Service
@Transactional
public class PostsService {

    private final Logger log = LoggerFactory.getLogger(PostsService.class);

    private final PostsRepository postsRepository;

    public PostsService(PostsRepository postsRepository) {
        this.postsRepository = postsRepository;
    }

    /**
     * Save a posts.
     *
     * @param posts the entity to save.
     * @return the persisted entity.
     */
    public Mono<Posts> save(Posts posts) {
        log.debug("Request to save Posts : {}", posts);
        return postsRepository.save(posts);
    }

    /**
     * Partially update a posts.
     *
     * @param posts the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Posts> partialUpdate(Posts posts) {
        log.debug("Request to partially update Posts : {}", posts);

        return postsRepository
            .findById(posts.getId())
            .map(
                existingPosts -> {
                    if (posts.getFramework() != null) {
                        existingPosts.setFramework(posts.getFramework());
                    }
                    if (posts.getDocumentNo() != null) {
                        existingPosts.setDocumentNo(posts.getDocumentNo());
                    }
                    if (posts.getDominantTopic() != null) {
                        existingPosts.setDominantTopic(posts.getDominantTopic());
                    }
                    if (posts.getTopicPercContrib() != null) {
                        existingPosts.setTopicPercContrib(posts.getTopicPercContrib());
                    }
                    if (posts.getKeywords() != null) {
                        existingPosts.setKeywords(posts.getKeywords());
                    }
                    if (posts.getTitle() != null) {
                        existingPosts.setTitle(posts.getTitle());
                    }
                    if (posts.getTexts() != null) {
                        existingPosts.setTexts(posts.getTexts());
                    }
                    if (posts.getAnswer() != null) {
                        existingPosts.setAnswer(posts.getAnswer());
                    }

                    return existingPosts;
                }
            )
            .flatMap(postsRepository::save);
    }

    /**
     * Get all the posts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Posts> findAll(Pageable pageable) {
        log.debug("Request to get all Posts");
        return postsRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of posts available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return postsRepository.count();
    }

    /**
     * Get one posts by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Posts> findOne(Long id) {
        log.debug("Request to get Posts : {}", id);
        return postsRepository.findById(id);
    }

    /**
     * Delete the posts by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Posts : {}", id);
        return postsRepository.deleteById(id);
    }
}
