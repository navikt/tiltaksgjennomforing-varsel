package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselKvittering;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("kafka")
public class SmsVarselResultatProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMeldingTilKafka(VarselKvittering varselKvittering) {
        SmsVarselResultatMelding resultatMelding = SmsVarselResultatMelding.nyResultaMelding(varselKvittering);
        kafkaTemplate.send(MessageBuilder.withPayload(resultatMelding).setHeader(KafkaHeaders.TOPIC, Topics.SMS_VARSEL_RESULTAT).build())
                .addCallback(new ListenableFutureCallback<>() {
                    @Override
                    public void onFailure(Throwable ex) {
                        log.error("Kunne ikke sende melding til Kafka topic", ex);
                    }

                    @Override
                    public void onSuccess(SendResult<String, String> result) {
                        log.info("SmsVarselResultat med smsVarselId={} og status={} sendt på Kafka topic", resultatMelding.getSmsVarselId(), resultatMelding.getStatus());
                    }
                });

    }
}
