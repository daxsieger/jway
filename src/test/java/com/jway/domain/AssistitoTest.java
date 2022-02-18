package com.jway.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.jway.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssistitoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assistito.class);
        Assistito assistito1 = new Assistito();
        assistito1.setId(1L);
        Assistito assistito2 = new Assistito();
        assistito2.setId(assistito1.getId());
        assertThat(assistito1).isEqualTo(assistito2);
        assistito2.setId(2L);
        assertThat(assistito1).isNotEqualTo(assistito2);
        assistito1.setId(null);
        assertThat(assistito1).isNotEqualTo(assistito2);
    }
}
