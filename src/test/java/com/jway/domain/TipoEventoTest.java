package com.jway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TipoEventoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TipoEvento.class);
        TipoEvento tipoEvento1 = new TipoEvento();
        tipoEvento1.setId(1L);
        TipoEvento tipoEvento2 = new TipoEvento();
        tipoEvento2.setId(tipoEvento1.getId());
        assertThat(tipoEvento1).isEqualTo(tipoEvento2);
        tipoEvento2.setId(2L);
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);
        tipoEvento1.setId(null);
        assertThat(tipoEvento1).isNotEqualTo(tipoEvento2);
    }
}
