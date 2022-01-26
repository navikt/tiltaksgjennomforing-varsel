package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.infrastruktur.CorrelationIdSupplier;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselService;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("kafka")
public class SmsVarselConsumer {
    private final VarselService varselService;

    @KafkaListener(topics = "${tiltaksgjennomforing.topics.sms}")
    public void consume(SmsVarselMelding varselMelding) {
        CorrelationIdSupplier.set(varselMelding.getSmsVarselId().toString());
        varselService.prosesserVarsel(varselMelding);
        CorrelationIdSupplier.remove();
    }
}
