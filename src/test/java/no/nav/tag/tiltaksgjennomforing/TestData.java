package no.nav.tag.tiltaksgjennomforing;

import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselMelding;

public class TestData {
    public static SmsVarselMelding etSmsVarsel() {
        return new SmsVarselMelding();
    }
}
