package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Assistito;
import com.jway.repository.AssistitoRepository;
import com.jway.repository.EntityManager;
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
 * Integration tests for the {@link AssistitoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AssistitoResourceIT {

    private static final Long DEFAULT_ID_ASSISTITO = 1L;
    private static final Long UPDATED_ID_ASSISTITO = 2L;

    private static final String ENTITY_API_URL = "/api/assistitos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AssistitoRepository assistitoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Assistito assistito;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assistito createEntity(EntityManager em) {
        Assistito assistito = new Assistito().idAssistito(DEFAULT_ID_ASSISTITO);
        return assistito;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assistito createUpdatedEntity(EntityManager em) {
        Assistito assistito = new Assistito().idAssistito(UPDATED_ID_ASSISTITO);
        return assistito;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Assistito.class).block();
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
        assistito = createEntity(em);
    }

    @Test
    void createAssistito() throws Exception {
        int databaseSizeBeforeCreate = assistitoRepository.findAll().collectList().block().size();
        // Create the Assistito
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeCreate + 1);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(DEFAULT_ID_ASSISTITO);
    }

    @Test
    void createAssistitoWithExistingId() throws Exception {
        // Create the Assistito with an existing ID
        assistito.setId(1L);

        int databaseSizeBeforeCreate = assistitoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAssistitos() {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        // Get all the assistitoList
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
            .value(hasItem(assistito.getId().intValue()))
            .jsonPath("$.[*].idAssistito")
            .value(hasItem(DEFAULT_ID_ASSISTITO.intValue()));
    }

    @Test
    void getAssistito() {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        // Get the assistito
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, assistito.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(assistito.getId().intValue()))
            .jsonPath("$.idAssistito")
            .value(is(DEFAULT_ID_ASSISTITO.intValue()));
    }

    @Test
    void getNonExistingAssistito() {
        // Get the assistito
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAssistito() throws Exception {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();

        // Update the assistito
        Assistito updatedAssistito = assistitoRepository.findById(assistito.getId()).block();
        updatedAssistito.idAssistito(UPDATED_ID_ASSISTITO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAssistito.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAssistito))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(UPDATED_ID_ASSISTITO);
    }

    @Test
    void putNonExistingAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, assistito.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAssistitoWithPatch() throws Exception {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();

        // Update the assistito using partial update
        Assistito partialUpdatedAssistito = new Assistito();
        partialUpdatedAssistito.setId(assistito.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssistito.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAssistito))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(DEFAULT_ID_ASSISTITO);
    }

    @Test
    void fullUpdateAssistitoWithPatch() throws Exception {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();

        // Update the assistito using partial update
        Assistito partialUpdatedAssistito = new Assistito();
        partialUpdatedAssistito.setId(assistito.getId());

        partialUpdatedAssistito.idAssistito(UPDATED_ID_ASSISTITO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssistito.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAssistito))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
        Assistito testAssistito = assistitoList.get(assistitoList.size() - 1);
        assertThat(testAssistito.getIdAssistito()).isEqualTo(UPDATED_ID_ASSISTITO);
    }

    @Test
    void patchNonExistingAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, assistito.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAssistito() throws Exception {
        int databaseSizeBeforeUpdate = assistitoRepository.findAll().collectList().block().size();
        assistito.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assistito))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Assistito in the database
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAssistito() {
        // Initialize the database
        assistitoRepository.save(assistito).block();

        int databaseSizeBeforeDelete = assistitoRepository.findAll().collectList().block().size();

        // Delete the assistito
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, assistito.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Assistito> assistitoList = assistitoRepository.findAll().collectList().block();
        assertThat(assistitoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
