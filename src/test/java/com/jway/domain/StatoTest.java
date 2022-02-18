package com.jway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StatoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stato.class);
        Stato stato1 = new Stato();
        stato1.setId(1L);
        Stato stato2 = new Stato();
        stato2.setId(stato1.getId());
        assertThat(stato1).isEqualTo(stato2);
        stato2.setId(2L);
        assertThat(stato1).isNotEqualTo(stato2);
        stato1.setId(null);
        assertThat(stato1).isNotEqualTo(stato2);
    }
}
