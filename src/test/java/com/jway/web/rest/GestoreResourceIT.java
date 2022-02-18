package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Gestore;
import com.jway.repository.EntityManager;
import com.jway.repository.GestoreRepository;
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
 * Integration tests for the {@link GestoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class GestoreResourceIT {

    private static final Long DEFAULT_ID_GESTORE = 1L;
    private static final Long UPDATED_ID_GESTORE = 2L;

    private static final String ENTITY_API_URL = "/api/gestores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GestoreRepository gestoreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Gestore gestore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestore createEntity(EntityManager em) {
        Gestore gestore = new Gestore().idGestore(DEFAULT_ID_GESTORE);
        return gestore;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Gestore createUpdatedEntity(EntityManager em) {
        Gestore gestore = new Gestore().idGestore(UPDATED_ID_GESTORE);
        return gestore;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Gestore.class).block();
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
        gestore = createEntity(em);
    }

    @Test
    void createGestore() throws Exception {
        int databaseSizeBeforeCreate = gestoreRepository.findAll().collectList().block().size();
        // Create the Gestore
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeCreate + 1);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(DEFAULT_ID_GESTORE);
    }

    @Test
    void createGestoreWithExistingId() throws Exception {
        // Create the Gestore with an existing ID
        gestore.setId(1L);

        int databaseSizeBeforeCreate = gestoreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllGestores() {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        // Get all the gestoreList
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
            .value(hasItem(gestore.getId().intValue()))
            .jsonPath("$.[*].idGestore")
            .value(hasItem(DEFAULT_ID_GESTORE.intValue()));
    }

    @Test
    void getGestore() {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        // Get the gestore
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, gestore.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(gestore.getId().intValue()))
            .jsonPath("$.idGestore")
            .value(is(DEFAULT_ID_GESTORE.intValue()));
    }

    @Test
    void getNonExistingGestore() {
        // Get the gestore
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewGestore() throws Exception {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();

        // Update the gestore
        Gestore updatedGestore = gestoreRepository.findById(gestore.getId()).block();
        updatedGestore.idGestore(UPDATED_ID_GESTORE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedGestore.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedGestore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    void putNonExistingGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, gestore.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateGestoreWithPatch() throws Exception {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();

        // Update the gestore using partial update
        Gestore partialUpdatedGestore = new Gestore();
        partialUpdatedGestore.setId(gestore.getId());

        partialUpdatedGestore.idGestore(UPDATED_ID_GESTORE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGestore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGestore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    void fullUpdateGestoreWithPatch() throws Exception {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();

        // Update the gestore using partial update
        Gestore partialUpdatedGestore = new Gestore();
        partialUpdatedGestore.setId(gestore.getId());

        partialUpdatedGestore.idGestore(UPDATED_ID_GESTORE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedGestore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedGestore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
        Gestore testGestore = gestoreList.get(gestoreList.size() - 1);
        assertThat(testGestore.getIdGestore()).isEqualTo(UPDATED_ID_GESTORE);
    }

    @Test
    void patchNonExistingGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, gestore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamGestore() throws Exception {
        int databaseSizeBeforeUpdate = gestoreRepository.findAll().collectList().block().size();
        gestore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(gestore))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Gestore in the database
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteGestore() {
        // Initialize the database
        gestoreRepository.save(gestore).block();

        int databaseSizeBeforeDelete = gestoreRepository.findAll().collectList().block().size();

        // Delete the gestore
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, gestore.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Gestore> gestoreList = gestoreRepository.findAll().collectList().block();
        assertThat(gestoreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
