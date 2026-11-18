package no.nav.tag.tiltaksgjennomforing;

import no.nav.tag.tiltaksgjennomforing.infrastruktur.SjekkAktiveProfilerInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = KafkaAutoConfiguration.class)
@EnableConfigurationProperties
@EnableCaching
public class VarselApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplicationBuilder(VarselApplication.class)
                .initializers(new SjekkAktiveProfilerInitializer())
                .build();
        application.run();
    }
}
