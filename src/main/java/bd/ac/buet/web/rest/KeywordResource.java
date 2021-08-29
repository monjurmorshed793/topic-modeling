package bd.ac.buet.web.rest;

import bd.ac.buet.domain.Keyword;
import bd.ac.buet.repository.KeywordRepository;
import bd.ac.buet.service.KeywordService;
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
 * REST controller for managing {@link bd.ac.buet.domain.Keyword}.
 */
@RestController
@RequestMapping("/api")
public class KeywordResource {

    private final Logger log = LoggerFactory.getLogger(KeywordResource.class);

    private static final String ENTITY_NAME = "keyword";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final KeywordService keywordService;

    private final KeywordRepository keywordRepository;

    public KeywordResource(KeywordService keywordService, KeywordRepository keywordRepository) {
        this.keywordService = keywordService;
        this.keywordRepository = keywordRepository;
    }

    /**
     * {@code POST  /keywords} : Create a new keyword.
     *
     * @param keyword the keyword to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new keyword, or with status {@code 400 (Bad Request)} if the keyword has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/keywords")
    public Mono<ResponseEntity<Keyword>> createKeyword(@RequestBody Keyword keyword) throws URISyntaxException {
        log.debug("REST request to save Keyword : {}", keyword);
        if (keyword.getId() != null) {
            throw new BadRequestAlertException("A new keyword cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return keywordService
            .save(keyword)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/keywords/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /keywords/:id} : Updates an existing keyword.
     *
     * @param id the id of the keyword to save.
     * @param keyword the keyword to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated keyword,
     * or with status {@code 400 (Bad Request)} if the keyword is not valid,
     * or with status {@code 500 (Internal Server Error)} if the keyword couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/keywords/{id}")
    public Mono<ResponseEntity<Keyword>> updateKeyword(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Keyword keyword
    ) throws URISyntaxException {
        log.debug("REST request to update Keyword : {}, {}", id, keyword);
        if (keyword.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, keyword.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return keywordRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return keywordService
                        .save(keyword)
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
     * {@code PATCH  /keywords/:id} : Partial updates given fields of an existing keyword, field will ignore if it is null
     *
     * @param id the id of the keyword to save.
     * @param keyword the keyword to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated keyword,
     * or with status {@code 400 (Bad Request)} if the keyword is not valid,
     * or with status {@code 404 (Not Found)} if the keyword is not found,
     * or with status {@code 500 (Internal Server Error)} if the keyword couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/keywords/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Keyword>> partialUpdateKeyword(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Keyword keyword
    ) throws URISyntaxException {
        log.debug("REST request to partial update Keyword partially : {}, {}", id, keyword);
        if (keyword.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, keyword.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return keywordRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Keyword> result = keywordService.partialUpdate(keyword);

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
     * {@code GET  /keywords} : get all the keywords.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of keywords in body.
     */
    @GetMapping("/keywords")
    public Mono<ResponseEntity<List<Keyword>>> getAllKeywords(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Keywords");
        return keywordService
            .countAll()
            .zipWith(keywordService.findAll(pageable).collectList())
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
     * {@code GET  /keywords/:id} : get the "id" keyword.
     *
     * @param id the id of the keyword to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the keyword, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/keywords/{id}")
    public Mono<ResponseEntity<Keyword>> getKeyword(@PathVariable Long id) {
        log.debug("REST request to get Keyword : {}", id);
        Mono<Keyword> keyword = keywordService.findOne(id);
        return ResponseUtil.wrapOrNotFound(keyword);
    }

    /**
     * {@code DELETE  /keywords/:id} : delete the "id" keyword.
     *
     * @param id the id of the keyword to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/keywords/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteKeyword(@PathVariable Long id) {
        log.debug("REST request to delete Keyword : {}", id);
        return keywordService
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
