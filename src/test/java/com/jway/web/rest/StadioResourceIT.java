package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Stadio;
import com.jway.repository.EntityManager;
import com.jway.repository.StadioRepository;
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
 * Integration tests for the {@link StadioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StadioResourceIT {

    private static final Long DEFAULT_ID_STADIO = 1L;
    private static final Long UPDATED_ID_STADIO = 2L;

    private static final String DEFAULT_DS_STADIO = "AAAAAAAAAA";
    private static final String UPDATED_DS_STADIO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/stadios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StadioRepository stadioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Stadio stadio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stadio createEntity(EntityManager em) {
        Stadio stadio = new Stadio().idStadio(DEFAULT_ID_STADIO).dsStadio(DEFAULT_DS_STADIO);
        return stadio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stadio createUpdatedEntity(EntityManager em) {
        Stadio stadio = new Stadio().idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);
        return stadio;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Stadio.class).block();
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
        stadio = createEntity(em);
    }

    @Test
    void createStadio() throws Exception {
        int databaseSizeBeforeCreate = stadioRepository.findAll().collectList().block().size();
        // Create the Stadio
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeCreate + 1);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(DEFAULT_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
    }

    @Test
    void createStadioWithExistingId() throws Exception {
        // Create the Stadio with an existing ID
        stadio.setId(1L);

        int databaseSizeBeforeCreate = stadioRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllStadios() {
        // Initialize the database
        stadioRepository.save(stadio).block();

        // Get all the stadioList
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
            .value(hasItem(stadio.getId().intValue()))
            .jsonPath("$.[*].idStadio")
            .value(hasItem(DEFAULT_ID_STADIO.intValue()))
            .jsonPath("$.[*].dsStadio")
            .value(hasItem(DEFAULT_DS_STADIO));
    }

    @Test
    void getStadio() {
        // Initialize the database
        stadioRepository.save(stadio).block();

        // Get the stadio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stadio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stadio.getId().intValue()))
            .jsonPath("$.idStadio")
            .value(is(DEFAULT_ID_STADIO.intValue()))
            .jsonPath("$.dsStadio")
            .value(is(DEFAULT_DS_STADIO));
    }

    @Test
    void getNonExistingStadio() {
        // Get the stadio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStadio() throws Exception {
        // Initialize the database
        stadioRepository.save(stadio).block();

        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();

        // Update the stadio
        Stadio updatedStadio = stadioRepository.findById(stadio.getId()).block();
        updatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedStadio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedStadio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    void putNonExistingStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stadio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStadioWithPatch() throws Exception {
        // Initialize the database
        stadioRepository.save(stadio).block();

        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();

        // Update the stadio using partial update
        Stadio partialUpdatedStadio = new Stadio();
        partialUpdatedStadio.setId(stadio.getId());

        partialUpdatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStadio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStadio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    void fullUpdateStadioWithPatch() throws Exception {
        // Initialize the database
        stadioRepository.save(stadio).block();

        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();

        // Update the stadio using partial update
        Stadio partialUpdatedStadio = new Stadio();
        partialUpdatedStadio.setId(stadio.getId());

        partialUpdatedStadio.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStadio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStadio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
        Stadio testStadio = stadioList.get(stadioList.size() - 1);
        assertThat(testStadio.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStadio.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
    }

    @Test
    void patchNonExistingStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stadio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStadio() throws Exception {
        int databaseSizeBeforeUpdate = stadioRepository.findAll().collectList().block().size();
        stadio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stadio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stadio in the database
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStadio() {
        // Initialize the database
        stadioRepository.save(stadio).block();

        int databaseSizeBeforeDelete = stadioRepository.findAll().collectList().block().size();

        // Delete the stadio
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stadio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Stadio> stadioList = stadioRepository.findAll().collectList().block();
        assertThat(stadioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
