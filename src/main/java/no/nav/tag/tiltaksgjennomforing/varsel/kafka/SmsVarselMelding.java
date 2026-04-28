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

    public String getTelefonnummerMedLandskode() {
        if (telefonnummer == null || telefonnummer.isBlank()) {
            throw new IllegalArgumentException("Telefonnummer kan ikke være null eller tomt");
        }
        if (telefonnummer.startsWith("+") || telefonnummer.startsWith("00")) {
            return telefonnummer;
        }
        return "+47" + telefonnummer;
    }
}
