package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Processo;
import com.jway.repository.EntityManager;
import com.jway.repository.ProcessoRepository;
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
 * Integration tests for the {@link ProcessoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProcessoResourceIT {

    private static final Long DEFAULT_ID_PROCESSO = 1L;
    private static final Long UPDATED_ID_PROCESSO = 2L;

    private static final String DEFAULT_DS_PROCESSO = "AAAAAAAAAA";
    private static final String UPDATED_DS_PROCESSO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/processos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProcessoRepository processoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Processo processo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Processo createEntity(EntityManager em) {
        Processo processo = new Processo().idProcesso(DEFAULT_ID_PROCESSO).dsProcesso(DEFAULT_DS_PROCESSO);
        return processo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Processo createUpdatedEntity(EntityManager em) {
        Processo processo = new Processo().idProcesso(UPDATED_ID_PROCESSO).dsProcesso(UPDATED_DS_PROCESSO);
        return processo;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Processo.class).block();
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
        processo = createEntity(em);
    }

    @Test
    void createProcesso() throws Exception {
        int databaseSizeBeforeCreate = processoRepository.findAll().collectList().block().size();
        // Create the Processo
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeCreate + 1);
        Processo testProcesso = processoList.get(processoList.size() - 1);
        assertThat(testProcesso.getIdProcesso()).isEqualTo(DEFAULT_ID_PROCESSO);
        assertThat(testProcesso.getDsProcesso()).isEqualTo(DEFAULT_DS_PROCESSO);
    }

    @Test
    void createProcessoWithExistingId() throws Exception {
        // Create the Processo with an existing ID
        processo.setId(1L);

        int databaseSizeBeforeCreate = processoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProcessos() {
        // Initialize the database
        processoRepository.save(processo).block();

        // Get all the processoList
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
            .value(hasItem(processo.getId().intValue()))
            .jsonPath("$.[*].idProcesso")
            .value(hasItem(DEFAULT_ID_PROCESSO.intValue()))
            .jsonPath("$.[*].dsProcesso")
            .value(hasItem(DEFAULT_DS_PROCESSO));
    }

    @Test
    void getProcesso() {
        // Initialize the database
        processoRepository.save(processo).block();

        // Get the processo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, processo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(processo.getId().intValue()))
            .jsonPath("$.idProcesso")
            .value(is(DEFAULT_ID_PROCESSO.intValue()))
            .jsonPath("$.dsProcesso")
            .value(is(DEFAULT_DS_PROCESSO));
    }

    @Test
    void getNonExistingProcesso() {
        // Get the processo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProcesso() throws Exception {
        // Initialize the database
        processoRepository.save(processo).block();

        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();

        // Update the processo
        Processo updatedProcesso = processoRepository.findById(processo.getId()).block();
        updatedProcesso.idProcesso(UPDATED_ID_PROCESSO).dsProcesso(UPDATED_DS_PROCESSO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProcesso.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProcesso))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
        Processo testProcesso = processoList.get(processoList.size() - 1);
        assertThat(testProcesso.getIdProcesso()).isEqualTo(UPDATED_ID_PROCESSO);
        assertThat(testProcesso.getDsProcesso()).isEqualTo(UPDATED_DS_PROCESSO);
    }

    @Test
    void putNonExistingProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, processo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProcessoWithPatch() throws Exception {
        // Initialize the database
        processoRepository.save(processo).block();

        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();

        // Update the processo using partial update
        Processo partialUpdatedProcesso = new Processo();
        partialUpdatedProcesso.setId(processo.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProcesso.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProcesso))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
        Processo testProcesso = processoList.get(processoList.size() - 1);
        assertThat(testProcesso.getIdProcesso()).isEqualTo(DEFAULT_ID_PROCESSO);
        assertThat(testProcesso.getDsProcesso()).isEqualTo(DEFAULT_DS_PROCESSO);
    }

    @Test
    void fullUpdateProcessoWithPatch() throws Exception {
        // Initialize the database
        processoRepository.save(processo).block();

        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();

        // Update the processo using partial update
        Processo partialUpdatedProcesso = new Processo();
        partialUpdatedProcesso.setId(processo.getId());

        partialUpdatedProcesso.idProcesso(UPDATED_ID_PROCESSO).dsProcesso(UPDATED_DS_PROCESSO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProcesso.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProcesso))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
        Processo testProcesso = processoList.get(processoList.size() - 1);
        assertThat(testProcesso.getIdProcesso()).isEqualTo(UPDATED_ID_PROCESSO);
        assertThat(testProcesso.getDsProcesso()).isEqualTo(UPDATED_DS_PROCESSO);
    }

    @Test
    void patchNonExistingProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, processo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProcesso() throws Exception {
        int databaseSizeBeforeUpdate = processoRepository.findAll().collectList().block().size();
        processo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(processo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Processo in the database
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProcesso() {
        // Initialize the database
        processoRepository.save(processo).block();

        int databaseSizeBeforeDelete = processoRepository.findAll().collectList().block().size();

        // Delete the processo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, processo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Processo> processoList = processoRepository.findAll().collectList().block();
        assertThat(processoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
