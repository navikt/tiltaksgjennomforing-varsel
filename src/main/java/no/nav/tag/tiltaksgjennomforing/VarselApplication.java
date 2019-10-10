package no.nav.tag.tiltaksgjennomforing;

import no.nav.tag.tiltaksgjennomforing.infrastruktur.SjekkAktiveProfilerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableConfigurationProperties
public class VarselApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(VarselApplication.class)
                .initializers(new SjekkAktiveProfilerInitializer())
                .profiles("kafka")
                .build();
        application.run();
    }
}
