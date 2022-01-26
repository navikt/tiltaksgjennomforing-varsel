package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsVarselMelding {
    private UUID smsVarselId;
    private String identifikator;
    private String telefonnummer;
    private String meldingstekst;
    private String avsenderApplikasjon;
}
