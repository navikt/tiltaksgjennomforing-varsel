package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselKvittering;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselStatus;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SmsVarselResultatMelding {
    private UUID smsVarselId;
    private VarselStatus status;

    public static SmsVarselResultatMelding nyResultaMelding(VarselKvittering varselKvittering) {
        return new SmsVarselResultatMelding(varselKvittering.getId(), varselKvittering.getStatus());
    }
}
