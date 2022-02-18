package com.jway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TransizioniTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Transizioni.class);
        Transizioni transizioni1 = new Transizioni();
        transizioni1.setId(1L);
        Transizioni transizioni2 = new Transizioni();
        transizioni2.setId(transizioni1.getId());
        assertThat(transizioni1).isEqualTo(transizioni2);
        transizioni2.setId(2L);
        assertThat(transizioni1).isNotEqualTo(transizioni2);
        transizioni1.setId(null);
        assertThat(transizioni1).isNotEqualTo(transizioni2);
    }
}
