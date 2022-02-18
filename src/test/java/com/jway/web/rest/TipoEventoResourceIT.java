package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.TipoEvento;
import com.jway.repository.EntityManager;
import com.jway.repository.TipoEventoRepository;
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
 * Integration tests for the {@link TipoEventoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TipoEventoResourceIT {

    private static final Long DEFAULT_ID_TIPO_EVENTO = 1L;
    private static final Long UPDATED_ID_TIPO_EVENTO = 2L;

    private static final String DEFAULT_DS_TIPO_EVENTO = "AAAAAAAAAA";
    private static final String UPDATED_DS_TIPO_EVENTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tipo-eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TipoEventoRepository tipoEventoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private TipoEvento tipoEvento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoEvento createEntity(EntityManager em) {
        TipoEvento tipoEvento = new TipoEvento().idTipoEvento(DEFAULT_ID_TIPO_EVENTO).dsTipoEvento(DEFAULT_DS_TIPO_EVENTO);
        return tipoEvento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TipoEvento createUpdatedEntity(EntityManager em) {
        TipoEvento tipoEvento = new TipoEvento().idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);
        return tipoEvento;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(TipoEvento.class).block();
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
        tipoEvento = createEntity(em);
    }

    @Test
    void createTipoEvento() throws Exception {
        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().collectList().block().size();
        // Create the TipoEvento
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate + 1);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(DEFAULT_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(DEFAULT_DS_TIPO_EVENTO);
    }

    @Test
    void createTipoEventoWithExistingId() throws Exception {
        // Create the TipoEvento with an existing ID
        tipoEvento.setId(1L);

        int databaseSizeBeforeCreate = tipoEventoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTipoEventosAsStream() {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        List<TipoEvento> tipoEventoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(TipoEvento.class)
            .getResponseBody()
            .filter(tipoEvento::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tipoEventoList).isNotNull();
        assertThat(tipoEventoList).hasSize(1);
        TipoEvento testTipoEvento = tipoEventoList.get(0);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(DEFAULT_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(DEFAULT_DS_TIPO_EVENTO);
    }

    @Test
    void getAllTipoEventos() {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        // Get all the tipoEventoList
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
            .value(hasItem(tipoEvento.getId().intValue()))
            .jsonPath("$.[*].idTipoEvento")
            .value(hasItem(DEFAULT_ID_TIPO_EVENTO.intValue()))
            .jsonPath("$.[*].dsTipoEvento")
            .value(hasItem(DEFAULT_DS_TIPO_EVENTO));
    }

    @Test
    void getTipoEvento() {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        // Get the tipoEvento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tipoEvento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tipoEvento.getId().intValue()))
            .jsonPath("$.idTipoEvento")
            .value(is(DEFAULT_ID_TIPO_EVENTO.intValue()))
            .jsonPath("$.dsTipoEvento")
            .value(is(DEFAULT_DS_TIPO_EVENTO));
    }

    @Test
    void getNonExistingTipoEvento() {
        // Get the tipoEvento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTipoEvento() throws Exception {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();

        // Update the tipoEvento
        TipoEvento updatedTipoEvento = tipoEventoRepository.findById(tipoEvento.getId()).block();
        updatedTipoEvento.idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTipoEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTipoEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(UPDATED_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(UPDATED_DS_TIPO_EVENTO);
    }

    @Test
    void putNonExistingTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tipoEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTipoEventoWithPatch() throws Exception {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();

        // Update the tipoEvento using partial update
        TipoEvento partialUpdatedTipoEvento = new TipoEvento();
        partialUpdatedTipoEvento.setId(tipoEvento.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTipoEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(DEFAULT_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(DEFAULT_DS_TIPO_EVENTO);
    }

    @Test
    void fullUpdateTipoEventoWithPatch() throws Exception {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();

        // Update the tipoEvento using partial update
        TipoEvento partialUpdatedTipoEvento = new TipoEvento();
        partialUpdatedTipoEvento.setId(tipoEvento.getId());

        partialUpdatedTipoEvento.idTipoEvento(UPDATED_ID_TIPO_EVENTO).dsTipoEvento(UPDATED_DS_TIPO_EVENTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTipoEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTipoEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
        TipoEvento testTipoEvento = tipoEventoList.get(tipoEventoList.size() - 1);
        assertThat(testTipoEvento.getIdTipoEvento()).isEqualTo(UPDATED_ID_TIPO_EVENTO);
        assertThat(testTipoEvento.getDsTipoEvento()).isEqualTo(UPDATED_DS_TIPO_EVENTO);
    }

    @Test
    void patchNonExistingTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tipoEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTipoEvento() throws Exception {
        int databaseSizeBeforeUpdate = tipoEventoRepository.findAll().collectList().block().size();
        tipoEvento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(tipoEvento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the TipoEvento in the database
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTipoEvento() {
        // Initialize the database
        tipoEventoRepository.save(tipoEvento).block();

        int databaseSizeBeforeDelete = tipoEventoRepository.findAll().collectList().block().size();

        // Delete the tipoEvento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tipoEvento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<TipoEvento> tipoEventoList = tipoEventoRepository.findAll().collectList().block();
        assertThat(tipoEventoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
