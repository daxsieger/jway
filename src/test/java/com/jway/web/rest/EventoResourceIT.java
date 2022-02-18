package com.jway.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.jway.IntegrationTest;
import com.jway.domain.Evento;
import com.jway.repository.EntityManager;
import com.jway.repository.EventoRepository;
import com.jway.service.EventoService;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link EventoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EventoResourceIT {

    private static final Long DEFAULT_ID_EVENTO = 1L;
    private static final Long UPDATED_ID_EVENTO = 2L;

    private static final Instant DEFAULT_TS_EVENTO = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_TS_EVENTO = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/eventos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EventoRepository eventoRepository;

    @Mock
    private EventoRepository eventoRepositoryMock;

    @Mock
    private EventoService eventoServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Evento evento;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createEntity(EntityManager em) {
        Evento evento = new Evento().idEvento(DEFAULT_ID_EVENTO).tsEvento(DEFAULT_TS_EVENTO).note(DEFAULT_NOTE);
        return evento;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Evento createUpdatedEntity(EntityManager em) {
        Evento evento = new Evento().idEvento(UPDATED_ID_EVENTO).tsEvento(UPDATED_TS_EVENTO).note(UPDATED_NOTE);
        return evento;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_evento__stati").block();
            em.deleteAll(Evento.class).block();
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
        evento = createEntity(em);
    }

    @Test
    void createEvento() throws Exception {
        int databaseSizeBeforeCreate = eventoRepository.findAll().collectList().block().size();
        // Create the Evento
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeCreate + 1);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getIdEvento()).isEqualTo(DEFAULT_ID_EVENTO);
        assertThat(testEvento.getTsEvento()).isEqualTo(DEFAULT_TS_EVENTO);
        assertThat(testEvento.getNote()).isEqualTo(DEFAULT_NOTE);
    }

    @Test
    void createEventoWithExistingId() throws Exception {
        // Create the Evento with an existing ID
        evento.setId(1L);

        int databaseSizeBeforeCreate = eventoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEventos() {
        // Initialize the database
        eventoRepository.save(evento).block();

        // Get all the eventoList
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
            .value(hasItem(evento.getId().intValue()))
            .jsonPath("$.[*].idEvento")
            .value(hasItem(DEFAULT_ID_EVENTO.intValue()))
            .jsonPath("$.[*].tsEvento")
            .value(hasItem(DEFAULT_TS_EVENTO.toString()))
            .jsonPath("$.[*].note")
            .value(hasItem(DEFAULT_NOTE));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsEnabled() {
        when(eventoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(eventoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEventosWithEagerRelationshipsIsNotEnabled() {
        when(eventoServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(eventoServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEvento() {
        // Initialize the database
        eventoRepository.save(evento).block();

        // Get the evento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(evento.getId().intValue()))
            .jsonPath("$.idEvento")
            .value(is(DEFAULT_ID_EVENTO.intValue()))
            .jsonPath("$.tsEvento")
            .value(is(DEFAULT_TS_EVENTO.toString()))
            .jsonPath("$.note")
            .value(is(DEFAULT_NOTE));
    }

    @Test
    void getNonExistingEvento() {
        // Get the evento
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEvento() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento
        Evento updatedEvento = eventoRepository.findById(evento.getId()).block();
        updatedEvento.idEvento(UPDATED_ID_EVENTO).tsEvento(UPDATED_TS_EVENTO).note(UPDATED_NOTE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEvento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getIdEvento()).isEqualTo(UPDATED_ID_EVENTO);
        assertThat(testEvento.getTsEvento()).isEqualTo(UPDATED_TS_EVENTO);
        assertThat(testEvento.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void putNonExistingEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento.tsEvento(UPDATED_TS_EVENTO).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getIdEvento()).isEqualTo(DEFAULT_ID_EVENTO);
        assertThat(testEvento.getTsEvento()).isEqualTo(UPDATED_TS_EVENTO);
        assertThat(testEvento.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void fullUpdateEventoWithPatch() throws Exception {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();

        // Update the evento using partial update
        Evento partialUpdatedEvento = new Evento();
        partialUpdatedEvento.setId(evento.getId());

        partialUpdatedEvento.idEvento(UPDATED_ID_EVENTO).tsEvento(UPDATED_TS_EVENTO).note(UPDATED_NOTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEvento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEvento))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
        Evento testEvento = eventoList.get(eventoList.size() - 1);
        assertThat(testEvento.getIdEvento()).isEqualTo(UPDATED_ID_EVENTO);
        assertThat(testEvento.getTsEvento()).isEqualTo(UPDATED_TS_EVENTO);
        assertThat(testEvento.getNote()).isEqualTo(UPDATED_NOTE);
    }

    @Test
    void patchNonExistingEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEvento() throws Exception {
        int databaseSizeBeforeUpdate = eventoRepository.findAll().collectList().block().size();
        evento.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(evento))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Evento in the database
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEvento() {
        // Initialize the database
        eventoRepository.save(evento).block();

        int databaseSizeBeforeDelete = eventoRepository.findAll().collectList().block().size();

        // Delete the evento
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, evento.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Evento> eventoList = eventoRepository.findAll().collectList().block();
        assertThat(eventoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
