package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselKvittering;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("kafka")
public class SmsVarselResultatProducer {
    private final KafkaTemplate<String, SmsVarselResultatMelding> kafkaTemplate;

    public void sendMeldingTilKafka(VarselKvittering varselKvittering) {
        SmsVarselResultatMelding resultatMelding = SmsVarselResultatMelding.nyResultatMelding(varselKvittering);
        kafkaTemplate.send(Topics.SMS_VARSEL_RESULTAT, resultatMelding).addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Kunne ikke sende melding til Kafka topic", ex);
            }

            @Override
            public void onSuccess(SendResult<String, SmsVarselResultatMelding> result) {
                log.info("SmsVarselResultat med smsVarselId={} og status={} sendt på Kafka topic", resultatMelding.getSmsVarselId(), resultatMelding.getStatus());
            }
        })
        ;
    }
}
