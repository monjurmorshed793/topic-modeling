package bd.ac.buet.web.rest;

import bd.ac.buet.domain.TopicModeling;
import bd.ac.buet.repository.TopicModelingRepository;
import bd.ac.buet.service.TopicModelingService;
import bd.ac.buet.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link bd.ac.buet.domain.TopicModeling}.
 */
@RestController
@RequestMapping("/api")
public class TopicModelingResource {

    private final Logger log = LoggerFactory.getLogger(TopicModelingResource.class);

    private static final String ENTITY_NAME = "topicModeling";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TopicModelingService topicModelingService;

    private final TopicModelingRepository topicModelingRepository;

    public TopicModelingResource(TopicModelingService topicModelingService, TopicModelingRepository topicModelingRepository) {
        this.topicModelingService = topicModelingService;
        this.topicModelingRepository = topicModelingRepository;
    }

    /**
     * {@code POST  /topic-modelings} : Create a new topicModeling.
     *
     * @param topicModeling the topicModeling to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new topicModeling, or with status {@code 400 (Bad Request)} if the topicModeling has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/topic-modelings")
    public Mono<ResponseEntity<TopicModeling>> createTopicModeling(@RequestBody TopicModeling topicModeling) throws URISyntaxException {
        log.debug("REST request to save TopicModeling : {}", topicModeling);
        if (topicModeling.getId() != null) {
            throw new BadRequestAlertException("A new topicModeling cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return topicModelingService
            .save(topicModeling)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/topic-modelings/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /topic-modelings/:id} : Updates an existing topicModeling.
     *
     * @param id the id of the topicModeling to save.
     * @param topicModeling the topicModeling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicModeling,
     * or with status {@code 400 (Bad Request)} if the topicModeling is not valid,
     * or with status {@code 500 (Internal Server Error)} if the topicModeling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/topic-modelings/{id}")
    public Mono<ResponseEntity<TopicModeling>> updateTopicModeling(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TopicModeling topicModeling
    ) throws URISyntaxException {
        log.debug("REST request to update TopicModeling : {}, {}", id, topicModeling);
        if (topicModeling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicModeling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return topicModelingRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return topicModelingService
                        .save(topicModeling)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /topic-modelings/:id} : Partial updates given fields of an existing topicModeling, field will ignore if it is null
     *
     * @param id the id of the topicModeling to save.
     * @param topicModeling the topicModeling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated topicModeling,
     * or with status {@code 400 (Bad Request)} if the topicModeling is not valid,
     * or with status {@code 404 (Not Found)} if the topicModeling is not found,
     * or with status {@code 500 (Internal Server Error)} if the topicModeling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/topic-modelings/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<TopicModeling>> partialUpdateTopicModeling(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody TopicModeling topicModeling
    ) throws URISyntaxException {
        log.debug("REST request to partial update TopicModeling partially : {}, {}", id, topicModeling);
        if (topicModeling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, topicModeling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return topicModelingRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<TopicModeling> result = topicModelingService.partialUpdate(topicModeling);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, res.getId().toString())
                                    )
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /topic-modelings} : get all the topicModelings.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of topicModelings in body.
     */
    @GetMapping("/topic-modelings")
    public Mono<ResponseEntity<List<TopicModeling>>> getAllTopicModelings(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of TopicModelings");
        return topicModelingService
            .countAll()
            .zipWith(topicModelingService.findAll(pageable).collectList())
            .map(
                countWithEntities -> {
                    return ResponseEntity
                        .ok()
                        .headers(
                            PaginationUtil.generatePaginationHttpHeaders(
                                UriComponentsBuilder.fromHttpRequest(request),
                                new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                            )
                        )
                        .body(countWithEntities.getT2());
                }
            );
    }

    /**
     * {@code GET  /topic-modelings/:id} : get the "id" topicModeling.
     *
     * @param id the id of the topicModeling to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the topicModeling, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/topic-modelings/{id}")
    public Mono<ResponseEntity<TopicModeling>> getTopicModeling(@PathVariable Long id) {
        log.debug("REST request to get TopicModeling : {}", id);
        Mono<TopicModeling> topicModeling = topicModelingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(topicModeling);
    }

    /**
     * {@code DELETE  /topic-modelings/:id} : delete the "id" topicModeling.
     *
     * @param id the id of the topicModeling to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/topic-modelings/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteTopicModeling(@PathVariable Long id) {
        log.debug("REST request to delete TopicModeling : {}", id);
        return topicModelingService
            .delete(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
