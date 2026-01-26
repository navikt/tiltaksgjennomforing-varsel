package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselKvittering;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("kafka")
public class SmsVarselResultatProducer {
    private final KafkaTemplate<String, SmsVarselResultatMelding> kafkaTemplate;
    private final String resultatTopic;

    public SmsVarselResultatProducer(KafkaTemplate<String, SmsVarselResultatMelding> kafkaTemplate, @Value("${tiltaksgjennomforing.topics.sms-resultat}") String resultatTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.resultatTopic = resultatTopic;
    }

    public void sendMeldingTilKafka(VarselKvittering varselKvittering) {
        SmsVarselResultatMelding resultatMelding = SmsVarselResultatMelding.nyResultatMelding(varselKvittering);
        kafkaTemplate.send(resultatTopic, resultatMelding).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Feil ved sending av SmsVarselResultatMelding til Kafka topic", ex);
            } else {
                log.info("SmsVarselResultat med smsVarselId={} og status={} sendt på Kafka topic", resultatMelding.getSmsVarselId(), resultatMelding.getStatus());
            }
        });
    }
}
