package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Import(KafkaAutoConfiguration.class)
@Configuration
@Profile("kafka")
public class KafkaConfiguration {
}
