package bd.ac.buet.service;

import bd.ac.buet.domain.Labeling;
import bd.ac.buet.repository.LabelingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Labeling}.
 */
@Service
@Transactional
public class LabelingService {

    private final Logger log = LoggerFactory.getLogger(LabelingService.class);

    private final LabelingRepository labelingRepository;

    public LabelingService(LabelingRepository labelingRepository) {
        this.labelingRepository = labelingRepository;
    }

    /**
     * Save a labeling.
     *
     * @param labeling the entity to save.
     * @return the persisted entity.
     */
    public Mono<Labeling> save(Labeling labeling) {
        log.debug("Request to save Labeling : {}", labeling);
        return labelingRepository.save(labeling);
    }

    /**
     * Partially update a labeling.
     *
     * @param labeling the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Labeling> partialUpdate(Labeling labeling) {
        log.debug("Request to partially update Labeling : {}", labeling);

        return labelingRepository
            .findById(labeling.getId())
            .map(
                existingLabeling -> {
                    if (labeling.getUserName() != null) {
                        existingLabeling.setUserName(labeling.getUserName());
                    }
                    if (labeling.getFramework() != null) {
                        existingLabeling.setFramework(labeling.getFramework());
                    }
                    if (labeling.getDocumentNo() != null) {
                        existingLabeling.setDocumentNo(labeling.getDocumentNo());
                    }
                    if (labeling.getDominantTopic() != null) {
                        existingLabeling.setDominantTopic(labeling.getDominantTopic());
                    }
                    if (labeling.getTopicPercContrib() != null) {
                        existingLabeling.setTopicPercContrib(labeling.getTopicPercContrib());
                    }
                    if (labeling.getKeywords() != null) {
                        existingLabeling.setKeywords(labeling.getKeywords());
                    }
                    if (labeling.getTitle() != null) {
                        existingLabeling.setTitle(labeling.getTitle());
                    }
                    if (labeling.getTexts() != null) {
                        existingLabeling.setTexts(labeling.getTexts());
                    }
                    if (labeling.getAnswer() != null) {
                        existingLabeling.setAnswer(labeling.getAnswer());
                    }
                    if (labeling.getLabel() != null) {
                        existingLabeling.setLabel(labeling.getLabel());
                    }
                    if (labeling.getReason() != null) {
                        existingLabeling.setReason(labeling.getReason());
                    }

                    return existingLabeling;
                }
            )
            .flatMap(labelingRepository::save);
    }

    /**
     * Get all the labelings.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Labeling> findAll(Pageable pageable) {
        log.debug("Request to get all Labelings");
        return labelingRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of labelings available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return labelingRepository.count();
    }

    /**
     * Get one labeling by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Labeling> findOne(Long id) {
        log.debug("Request to get Labeling : {}", id);
        return labelingRepository.findById(id);
    }

    /**
     * Delete the labeling by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Labeling : {}", id);
        return labelingRepository.deleteById(id);
    }
}
