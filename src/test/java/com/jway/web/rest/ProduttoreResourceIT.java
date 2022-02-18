package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.jway.IntegrationTest;
import com.jway.domain.Produttore;
import com.jway.repository.EntityManager;
import com.jway.repository.ProduttoreRepository;
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
 * Integration tests for the {@link ProduttoreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProduttoreResourceIT {

    private static final Long DEFAULT_ID_PRODUTTORE = 1L;
    private static final Long UPDATED_ID_PRODUTTORE = 2L;

    private static final String DEFAULT_DS_PRODUTTORE = "AAAAAAAAAA";
    private static final String UPDATED_DS_PRODUTTORE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/produttores";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProduttoreRepository produttoreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Produttore produttore;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produttore createEntity(EntityManager em) {
        Produttore produttore = new Produttore().idProduttore(DEFAULT_ID_PRODUTTORE).dsProduttore(DEFAULT_DS_PRODUTTORE);
        return produttore;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Produttore createUpdatedEntity(EntityManager em) {
        Produttore produttore = new Produttore().idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);
        return produttore;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Produttore.class).block();
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
        produttore = createEntity(em);
    }

    @Test
    void createProduttore() throws Exception {
        int databaseSizeBeforeCreate = produttoreRepository.findAll().collectList().block().size();
        // Create the Produttore
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeCreate + 1);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(DEFAULT_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(DEFAULT_DS_PRODUTTORE);
    }

    @Test
    void createProduttoreWithExistingId() throws Exception {
        // Create the Produttore with an existing ID
        produttore.setId(1L);

        int databaseSizeBeforeCreate = produttoreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProduttores() {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        // Get all the produttoreList
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
            .value(hasItem(produttore.getId().intValue()))
            .jsonPath("$.[*].idProduttore")
            .value(hasItem(DEFAULT_ID_PRODUTTORE.intValue()))
            .jsonPath("$.[*].dsProduttore")
            .value(hasItem(DEFAULT_DS_PRODUTTORE));
    }

    @Test
    void getProduttore() {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        // Get the produttore
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, produttore.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(produttore.getId().intValue()))
            .jsonPath("$.idProduttore")
            .value(is(DEFAULT_ID_PRODUTTORE.intValue()))
            .jsonPath("$.dsProduttore")
            .value(is(DEFAULT_DS_PRODUTTORE));
    }

    @Test
    void getNonExistingProduttore() {
        // Get the produttore
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProduttore() throws Exception {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();

        // Update the produttore
        Produttore updatedProduttore = produttoreRepository.findById(produttore.getId()).block();
        updatedProduttore.idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProduttore.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProduttore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(UPDATED_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    void putNonExistingProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, produttore.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProduttoreWithPatch() throws Exception {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();

        // Update the produttore using partial update
        Produttore partialUpdatedProduttore = new Produttore();
        partialUpdatedProduttore.setId(produttore.getId());

        partialUpdatedProduttore.dsProduttore(UPDATED_DS_PRODUTTORE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduttore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduttore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(DEFAULT_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    void fullUpdateProduttoreWithPatch() throws Exception {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();

        // Update the produttore using partial update
        Produttore partialUpdatedProduttore = new Produttore();
        partialUpdatedProduttore.setId(produttore.getId());

        partialUpdatedProduttore.idProduttore(UPDATED_ID_PRODUTTORE).dsProduttore(UPDATED_DS_PRODUTTORE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProduttore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProduttore))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
        Produttore testProduttore = produttoreList.get(produttoreList.size() - 1);
        assertThat(testProduttore.getIdProduttore()).isEqualTo(UPDATED_ID_PRODUTTORE);
        assertThat(testProduttore.getDsProduttore()).isEqualTo(UPDATED_DS_PRODUTTORE);
    }

    @Test
    void patchNonExistingProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, produttore.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProduttore() throws Exception {
        int databaseSizeBeforeUpdate = produttoreRepository.findAll().collectList().block().size();
        produttore.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(produttore))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Produttore in the database
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProduttore() {
        // Initialize the database
        produttoreRepository.save(produttore).block();

        int databaseSizeBeforeDelete = produttoreRepository.findAll().collectList().block().size();

        // Delete the produttore
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, produttore.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Produttore> produttoreList = produttoreRepository.findAll().collectList().block();
        assertThat(produttoreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
