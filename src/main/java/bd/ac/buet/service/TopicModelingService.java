package bd.ac.buet.service;

import bd.ac.buet.domain.TopicModeling;
import bd.ac.buet.repository.TopicModelingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link TopicModeling}.
 */
@Service
@Transactional
public class TopicModelingService {

    private final Logger log = LoggerFactory.getLogger(TopicModelingService.class);

    private final TopicModelingRepository topicModelingRepository;

    public TopicModelingService(TopicModelingRepository topicModelingRepository) {
        this.topicModelingRepository = topicModelingRepository;
    }

    /**
     * Save a topicModeling.
     *
     * @param topicModeling the entity to save.
     * @return the persisted entity.
     */
    public Mono<TopicModeling> save(TopicModeling topicModeling) {
        log.debug("Request to save TopicModeling : {}", topicModeling);
        return topicModelingRepository.save(topicModeling);
    }

    /**
     * Partially update a topicModeling.
     *
     * @param topicModeling the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<TopicModeling> partialUpdate(TopicModeling topicModeling) {
        log.debug("Request to partially update TopicModeling : {}", topicModeling);

        return topicModelingRepository
            .findById(topicModeling.getId())
            .map(
                existingTopicModeling -> {
                    if (topicModeling.getUserName() != null) {
                        existingTopicModeling.setUserName(topicModeling.getUserName());
                    }
                    if (topicModeling.getCategory() != null) {
                        existingTopicModeling.setCategory(topicModeling.getCategory());
                    }
                    if (topicModeling.getSubCategory() != null) {
                        existingTopicModeling.setSubCategory(topicModeling.getSubCategory());
                    }
                    if (topicModeling.getTopic() != null) {
                        existingTopicModeling.setTopic(topicModeling.getTopic());
                    }
                    if (topicModeling.getSubTopic() != null) {
                        existingTopicModeling.setSubTopic(topicModeling.getSubTopic());
                    }

                    return existingTopicModeling;
                }
            )
            .flatMap(topicModelingRepository::save);
    }

    /**
     * Get all the topicModelings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<TopicModeling> findAll(Pageable pageable) {
        log.debug("Request to get all TopicModelings");
        return topicModelingRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of topicModelings available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return topicModelingRepository.count();
    }

    /**
     * Get one topicModeling by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<TopicModeling> findOne(Long id) {
        log.debug("Request to get TopicModeling : {}", id);
        return topicModelingRepository.findById(id);
    }

    /**
     * Delete the topicModeling by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete TopicModeling : {}", id);
        return topicModelingRepository.deleteById(id);
    }
}
