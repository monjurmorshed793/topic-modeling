package bd.ac.buet.web.rest;

import bd.ac.buet.domain.Labeling;
import bd.ac.buet.repository.LabelingRepository;
import bd.ac.buet.service.LabelingService;
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
 * REST controller for managing {@link bd.ac.buet.domain.Labeling}.
 */
@RestController
@RequestMapping("/api")
public class LabelingResource {

    private final Logger log = LoggerFactory.getLogger(LabelingResource.class);

    private static final String ENTITY_NAME = "labeling";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LabelingService labelingService;

    private final LabelingRepository labelingRepository;

    public LabelingResource(LabelingService labelingService, LabelingRepository labelingRepository) {
        this.labelingService = labelingService;
        this.labelingRepository = labelingRepository;
    }

    /**
     * {@code POST  /labelings} : Create a new labeling.
     *
     * @param labeling the labeling to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new labeling, or with status {@code 400 (Bad Request)} if the labeling has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/labelings")
    public Mono<ResponseEntity<Labeling>> createLabeling(@RequestBody Labeling labeling) throws URISyntaxException {
        log.debug("REST request to save Labeling : {}", labeling);
        if (labeling.getId() != null) {
            throw new BadRequestAlertException("A new labeling cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return labelingService
            .save(labeling)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/labelings/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /labelings/:id} : Updates an existing labeling.
     *
     * @param id the id of the labeling to save.
     * @param labeling the labeling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated labeling,
     * or with status {@code 400 (Bad Request)} if the labeling is not valid,
     * or with status {@code 500 (Internal Server Error)} if the labeling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/labelings/{id}")
    public Mono<ResponseEntity<Labeling>> updateLabeling(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Labeling labeling
    ) throws URISyntaxException {
        log.debug("REST request to update Labeling : {}, {}", id, labeling);
        if (labeling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, labeling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return labelingRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return labelingService
                        .save(labeling)
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
     * {@code PATCH  /labelings/:id} : Partial updates given fields of an existing labeling, field will ignore if it is null
     *
     * @param id the id of the labeling to save.
     * @param labeling the labeling to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated labeling,
     * or with status {@code 400 (Bad Request)} if the labeling is not valid,
     * or with status {@code 404 (Not Found)} if the labeling is not found,
     * or with status {@code 500 (Internal Server Error)} if the labeling couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/labelings/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Labeling>> partialUpdateLabeling(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Labeling labeling
    ) throws URISyntaxException {
        log.debug("REST request to partial update Labeling partially : {}, {}", id, labeling);
        if (labeling.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, labeling.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return labelingRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Labeling> result = labelingService.partialUpdate(labeling);

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
     * {@code GET  /labelings} : get all the labelings.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of labelings in body.
     */
    @GetMapping("/labelings")
    public Mono<ResponseEntity<List<Labeling>>> getAllLabelings(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Labelings");
        return labelingService
            .countAll()
            .zipWith(labelingService.findAll(pageable).collectList())
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
     * {@code GET  /labelings/:id} : get the "id" labeling.
     *
     * @param id the id of the labeling to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the labeling, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/labelings/{id}")
    public Mono<ResponseEntity<Labeling>> getLabeling(@PathVariable Long id) {
        log.debug("REST request to get Labeling : {}", id);
        Mono<Labeling> labeling = labelingService.findOne(id);
        return ResponseUtil.wrapOrNotFound(labeling);
    }

    /**
     * {@code DELETE  /labelings/:id} : delete the "id" labeling.
     *
     * @param id the id of the labeling to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/labelings/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteLabeling(@PathVariable Long id) {
        log.debug("REST request to delete Labeling : {}", id);
        return labelingService
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
