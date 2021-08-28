package bd.ac.buet.service;

import bd.ac.buet.domain.Keyword;
import bd.ac.buet.repository.KeywordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Keyword}.
 */
@Service
@Transactional
public class KeywordService {

    private final Logger log = LoggerFactory.getLogger(KeywordService.class);

    private final KeywordRepository keywordRepository;

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    /**
     * Save a keyword.
     *
     * @param keyword the entity to save.
     * @return the persisted entity.
     */
    public Mono<Keyword> save(Keyword keyword) {
        log.debug("Request to save Keyword : {}", keyword);
        return keywordRepository.save(keyword);
    }

    /**
     * Partially update a keyword.
     *
     * @param keyword the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Keyword> partialUpdate(Keyword keyword) {
        log.debug("Request to partially update Keyword : {}", keyword);

        return keywordRepository
            .findById(keyword.getId())
            .map(
                existingKeyword -> {
                    if (keyword.getFramework() != null) {
                        existingKeyword.setFramework(keyword.getFramework());
                    }
                    if (keyword.getTopicNumber() != null) {
                        existingKeyword.setTopicNumber(keyword.getTopicNumber());
                    }
                    if (keyword.getKeywords() != null) {
                        existingKeyword.setKeywords(keyword.getKeywords());
                    }
                    if (keyword.getNumberOfPosts() != null) {
                        existingKeyword.setNumberOfPosts(keyword.getNumberOfPosts());
                    }

                    return existingKeyword;
                }
            )
            .flatMap(keywordRepository::save);
    }

    /**
     * Get all the keywords.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Keyword> findAll(Pageable pageable) {
        log.debug("Request to get all Keywords");
        return keywordRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of keywords available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return keywordRepository.count();
    }

    /**
     * Get one keyword by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Keyword> findOne(Long id) {
        log.debug("Request to get Keyword : {}", id);
        return keywordRepository.findById(id);
    }

    /**
     * Delete the keyword by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Keyword : {}", id);
        return keywordRepository.deleteById(id);
    }
}
