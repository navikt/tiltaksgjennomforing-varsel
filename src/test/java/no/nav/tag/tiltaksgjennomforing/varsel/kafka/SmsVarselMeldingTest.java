package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SmsVarselMeldingTest {

    private SmsVarselMelding meldingMedTlf(String telefonnummer) {
        return new SmsVarselMelding(UUID.randomUUID(), "identifikator", telefonnummer, "tekst", null);
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_naar_pluss_prefiks() {
        assertThat(meldingMedTlf("+4712345678").getTelefonnummerMedLandskode()).isEqualTo("+4712345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_for_annet_land_med_pluss() {
        assertThat(meldingMedTlf("+4612345678").getTelefonnummerMedLandskode()).isEqualTo("+4612345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_med_00_prefiks() {
        assertThat(meldingMedTlf("004712345678").getTelefonnummerMedLandskode()).isEqualTo("004712345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__legger_til_norsk_landkode_naar_ingen_prefiks() {
        assertThat(meldingMedTlf("12345678").getTelefonnummerMedLandskode()).isEqualTo("+4712345678");
    }
}
