package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import no.nav.tag.tiltaksgjennomforing.varsel.altinn.AltinnVarselAdapter;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselMelding;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselResultatProducer;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("kafka")
public class VarselService {
    private final VarselKvitteringRepository varselKvitteringRepository;
    private final AltinnVarselAdapter altinnVarselAdapter;
    private final SmsVarselResultatProducer resultatProducer;

    public void prosesserVarsel(SmsVarselMelding varselMelding) {
        if (!varselKvitteringRepository.existsById(varselMelding.getSmsVarselId())) {
            log.info("SmsVarsel med smsVarselId={} prosesseres", varselMelding.getSmsVarselId());
            sendVarsel(varselMelding);
        } else {
            log.warn("SmsVarsel med smsVarselId={} er allerede prosessert. Sender ikke varsel på nytt.", varselMelding.getSmsVarselId());
        }
    }

    private void sendVarsel(SmsVarselMelding varselMelding) {
        VarselKvittering varselKvittering = kallAltinnVarseltjeneste(varselMelding);
        varselKvitteringRepository.insert(varselKvittering);
        resultatProducer.sendMeldingTilKafka(varselKvittering);
    }

    private VarselKvittering kallAltinnVarseltjeneste(SmsVarselMelding varselMelding) {
        try {
            String ordreId = altinnVarselAdapter.sendVarsel(
                    varselMelding.getSmsVarselId().toString(),
                    varselMelding.getTelefonnummerMedLandskode(),
                    varselMelding.getMeldingstekst()
            );
            log.info("Varsel sendt til Altinn for smsVarselId={}, ordreId={}", varselMelding.getSmsVarselId(), ordreId);
            return new VarselKvittering(varselMelding.getSmsVarselId(), VarselStatus.SENDT);
        } catch (AltinnException e) {
            log.error("Feil ved sending av varsel for smsVarselId={}", varselMelding.getSmsVarselId(), e);
            return new VarselKvittering(varselMelding.getSmsVarselId(), VarselStatus.FEIL);
        }
    }
}
