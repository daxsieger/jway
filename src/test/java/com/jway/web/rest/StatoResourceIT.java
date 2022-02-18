package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Stato;
import com.jway.repository.EntityManager;
import com.jway.repository.StatoRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link StatoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class StatoResourceIT {

    private static final Long DEFAULT_ID_STADIO = 1L;
    private static final Long UPDATED_ID_STADIO = 2L;

    private static final String DEFAULT_DS_STADIO = "AAAAAAAAAA";
    private static final String UPDATED_DS_STADIO = "BBBBBBBBBB";

    private static final Instant DEFAULT_TS_CAMBIO_STATO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TS_CAMBIO_STATO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/statoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private StatoRepository statoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Stato stato;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stato createEntity(EntityManager em) {
        Stato stato = new Stato().idStadio(DEFAULT_ID_STADIO).dsStadio(DEFAULT_DS_STADIO).tsCambioStato(DEFAULT_TS_CAMBIO_STATO);
        return stato;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Stato createUpdatedEntity(EntityManager em) {
        Stato stato = new Stato().idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);
        return stato;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Stato.class).block();
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
        stato = createEntity(em);
    }

    @Test
    void createStato() throws Exception {
        int databaseSizeBeforeCreate = statoRepository.findAll().collectList().block().size();
        // Create the Stato
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeCreate + 1);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(DEFAULT_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(DEFAULT_TS_CAMBIO_STATO);
    }

    @Test
    void createStatoWithExistingId() throws Exception {
        // Create the Stato with an existing ID
        stato.setId(1L);

        int databaseSizeBeforeCreate = statoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllStatoes() {
        // Initialize the database
        statoRepository.save(stato).block();

        // Get all the statoList
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
            .value(hasItem(stato.getId().intValue()))
            .jsonPath("$.[*].idStadio")
            .value(hasItem(DEFAULT_ID_STADIO.intValue()))
            .jsonPath("$.[*].dsStadio")
            .value(hasItem(DEFAULT_DS_STADIO))
            .jsonPath("$.[*].tsCambioStato")
            .value(hasItem(DEFAULT_TS_CAMBIO_STATO.toString()));
    }

    @Test
    void getStato() {
        // Initialize the database
        statoRepository.save(stato).block();

        // Get the stato
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, stato.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(stato.getId().intValue()))
            .jsonPath("$.idStadio")
            .value(is(DEFAULT_ID_STADIO.intValue()))
            .jsonPath("$.dsStadio")
            .value(is(DEFAULT_DS_STADIO))
            .jsonPath("$.tsCambioStato")
            .value(is(DEFAULT_TS_CAMBIO_STATO.toString()));
    }

    @Test
    void getNonExistingStato() {
        // Get the stato
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewStato() throws Exception {
        // Initialize the database
        statoRepository.save(stato).block();

        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();

        // Update the stato
        Stato updatedStato = statoRepository.findById(stato.getId()).block();
        updatedStato.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedStato.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedStato))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    void putNonExistingStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, stato.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateStatoWithPatch() throws Exception {
        // Initialize the database
        statoRepository.save(stato).block();

        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();

        // Update the stato using partial update
        Stato partialUpdatedStato = new Stato();
        partialUpdatedStato.setId(stato.getId());

        partialUpdatedStato.idStadio(UPDATED_ID_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStato.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStato))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(DEFAULT_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    void fullUpdateStatoWithPatch() throws Exception {
        // Initialize the database
        statoRepository.save(stato).block();

        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();

        // Update the stato using partial update
        Stato partialUpdatedStato = new Stato();
        partialUpdatedStato.setId(stato.getId());

        partialUpdatedStato.idStadio(UPDATED_ID_STADIO).dsStadio(UPDATED_DS_STADIO).tsCambioStato(UPDATED_TS_CAMBIO_STATO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedStato.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedStato))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
        Stato testStato = statoList.get(statoList.size() - 1);
        assertThat(testStato.getIdStadio()).isEqualTo(UPDATED_ID_STADIO);
        assertThat(testStato.getDsStadio()).isEqualTo(UPDATED_DS_STADIO);
        assertThat(testStato.getTsCambioStato()).isEqualTo(UPDATED_TS_CAMBIO_STATO);
    }

    @Test
    void patchNonExistingStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, stato.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamStato() throws Exception {
        int databaseSizeBeforeUpdate = statoRepository.findAll().collectList().block().size();
        stato.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(stato))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Stato in the database
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteStato() {
        // Initialize the database
        statoRepository.save(stato).block();

        int databaseSizeBeforeDelete = statoRepository.findAll().collectList().block().size();

        // Delete the stato
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, stato.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Stato> statoList = statoRepository.findAll().collectList().block();
        assertThat(statoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
