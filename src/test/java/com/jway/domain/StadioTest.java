package com.jway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class StadioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Stadio.class);
        Stadio stadio1 = new Stadio();
        stadio1.setId(1L);
        Stadio stadio2 = new Stadio();
        stadio2.setId(stadio1.getId());
        assertThat(stadio1).isEqualTo(stadio2);
        stadio2.setId(2L);
        assertThat(stadio1).isNotEqualTo(stadio2);
        stadio1.setId(null);
        assertThat(stadio1).isNotEqualTo(stadio2);
    }
}
