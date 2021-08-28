package bd.ac.buet.web.rest;

import bd.ac.buet.domain.Posts;
import bd.ac.buet.repository.PostsRepository;
import bd.ac.buet.service.PostsService;
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
 * REST controller for managing {@link bd.ac.buet.domain.Posts}.
 */
@RestController
@RequestMapping("/api")
public class PostsResource {

    private final Logger log = LoggerFactory.getLogger(PostsResource.class);

    private static final String ENTITY_NAME = "posts";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PostsService postsService;

    private final PostsRepository postsRepository;

    public PostsResource(PostsService postsService, PostsRepository postsRepository) {
        this.postsService = postsService;
        this.postsRepository = postsRepository;
    }

    /**
     * {@code POST  /posts} : Create a new posts.
     *
     * @param posts the posts to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new posts, or with status {@code 400 (Bad Request)} if the posts has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/posts")
    public Mono<ResponseEntity<Posts>> createPosts(@RequestBody Posts posts) throws URISyntaxException {
        log.debug("REST request to save Posts : {}", posts);
        if (posts.getId() != null) {
            throw new BadRequestAlertException("A new posts cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return postsService
            .save(posts)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/posts/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /posts/:id} : Updates an existing posts.
     *
     * @param id the id of the posts to save.
     * @param posts the posts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posts,
     * or with status {@code 400 (Bad Request)} if the posts is not valid,
     * or with status {@code 500 (Internal Server Error)} if the posts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/posts/{id}")
    public Mono<ResponseEntity<Posts>> updatePosts(@PathVariable(value = "id", required = false) final Long id, @RequestBody Posts posts)
        throws URISyntaxException {
        log.debug("REST request to update Posts : {}, {}", id, posts);
        if (posts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return postsService
                        .save(posts)
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
     * {@code PATCH  /posts/:id} : Partial updates given fields of an existing posts, field will ignore if it is null
     *
     * @param id the id of the posts to save.
     * @param posts the posts to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated posts,
     * or with status {@code 400 (Bad Request)} if the posts is not valid,
     * or with status {@code 404 (Not Found)} if the posts is not found,
     * or with status {@code 500 (Internal Server Error)} if the posts couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/posts/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<Posts>> partialUpdatePosts(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Posts posts
    ) throws URISyntaxException {
        log.debug("REST request to partial update Posts partially : {}, {}", id, posts);
        if (posts.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, posts.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return postsRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<Posts> result = postsService.partialUpdate(posts);

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
     * {@code GET  /posts} : get all the posts.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of posts in body.
     */
    @GetMapping("/posts")
    public Mono<ResponseEntity<List<Posts>>> getAllPosts(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Posts");
        return postsService
            .countAll()
            .zipWith(postsService.findAll(pageable).collectList())
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
     * {@code GET  /posts/:id} : get the "id" posts.
     *
     * @param id the id of the posts to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the posts, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/posts/{id}")
    public Mono<ResponseEntity<Posts>> getPosts(@PathVariable Long id) {
        log.debug("REST request to get Posts : {}", id);
        Mono<Posts> posts = postsService.findOne(id);
        return ResponseUtil.wrapOrNotFound(posts);
    }

    /**
     * {@code DELETE  /posts/:id} : delete the "id" posts.
     *
     * @param id the id of the posts to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/posts/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePosts(@PathVariable Long id) {
        log.debug("REST request to delete Posts : {}", id);
        return postsService
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
