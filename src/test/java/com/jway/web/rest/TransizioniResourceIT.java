package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Transizioni;
import com.jway.repository.EntityManager;
import com.jway.repository.TransizioniRepository;
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
 * Integration tests for the {@link TransizioniResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TransizioniResourceIT {

    private static final Long DEFAULT_ID_TRANSIZIONE = 1L;
    private static final Long UPDATED_ID_TRANSIZIONE = 2L;

    private static final String DEFAULT_DS_TRANSIZIONE = "AAAAAAAAAA";
    private static final String UPDATED_DS_TRANSIZIONE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/transizionis";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TransizioniRepository transizioniRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Transizioni transizioni;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transizioni createEntity(EntityManager em) {
        Transizioni transizioni = new Transizioni().idTransizione(DEFAULT_ID_TRANSIZIONE).dsTransizione(DEFAULT_DS_TRANSIZIONE);
        return transizioni;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Transizioni createUpdatedEntity(EntityManager em) {
        Transizioni transizioni = new Transizioni().idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);
        return transizioni;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Transizioni.class).block();
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
        transizioni = createEntity(em);
    }

    @Test
    void createTransizioni() throws Exception {
        int databaseSizeBeforeCreate = transizioniRepository.findAll().collectList().block().size();
        // Create the Transizioni
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeCreate + 1);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(DEFAULT_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(DEFAULT_DS_TRANSIZIONE);
    }

    @Test
    void createTransizioniWithExistingId() throws Exception {
        // Create the Transizioni with an existing ID
        transizioni.setId(1L);

        int databaseSizeBeforeCreate = transizioniRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTransizionis() {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        // Get all the transizioniList
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
            .value(hasItem(transizioni.getId().intValue()))
            .jsonPath("$.[*].idTransizione")
            .value(hasItem(DEFAULT_ID_TRANSIZIONE.intValue()))
            .jsonPath("$.[*].dsTransizione")
            .value(hasItem(DEFAULT_DS_TRANSIZIONE));
    }

    @Test
    void getTransizioni() {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        // Get the transizioni
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, transizioni.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(transizioni.getId().intValue()))
            .jsonPath("$.idTransizione")
            .value(is(DEFAULT_ID_TRANSIZIONE.intValue()))
            .jsonPath("$.dsTransizione")
            .value(is(DEFAULT_DS_TRANSIZIONE));
    }

    @Test
    void getNonExistingTransizioni() {
        // Get the transizioni
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTransizioni() throws Exception {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();

        // Update the transizioni
        Transizioni updatedTransizioni = transizioniRepository.findById(transizioni.getId()).block();
        updatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTransizioni.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTransizioni))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(UPDATED_DS_TRANSIZIONE);
    }

    @Test
    void putNonExistingTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, transizioni.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTransizioniWithPatch() throws Exception {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();

        // Update the transizioni using partial update
        Transizioni partialUpdatedTransizioni = new Transizioni();
        partialUpdatedTransizioni.setId(transizioni.getId());

        partialUpdatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransizioni.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransizioni))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(DEFAULT_DS_TRANSIZIONE);
    }

    @Test
    void fullUpdateTransizioniWithPatch() throws Exception {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();

        // Update the transizioni using partial update
        Transizioni partialUpdatedTransizioni = new Transizioni();
        partialUpdatedTransizioni.setId(transizioni.getId());

        partialUpdatedTransizioni.idTransizione(UPDATED_ID_TRANSIZIONE).dsTransizione(UPDATED_DS_TRANSIZIONE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTransizioni.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTransizioni))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
        Transizioni testTransizioni = transizioniList.get(transizioniList.size() - 1);
        assertThat(testTransizioni.getIdTransizione()).isEqualTo(UPDATED_ID_TRANSIZIONE);
        assertThat(testTransizioni.getDsTransizione()).isEqualTo(UPDATED_DS_TRANSIZIONE);
    }

    @Test
    void patchNonExistingTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, transizioni.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTransizioni() throws Exception {
        int databaseSizeBeforeUpdate = transizioniRepository.findAll().collectList().block().size();
        transizioni.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(transizioni))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Transizioni in the database
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTransizioni() {
        // Initialize the database
        transizioniRepository.save(transizioni).block();

        int databaseSizeBeforeDelete = transizioniRepository.findAll().collectList().block().size();

        // Delete the transizioni
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, transizioni.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Transizioni> transizioniList = transizioniRepository.findAll().collectList().block();
        assertThat(transizioniList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
