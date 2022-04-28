package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import no.finn.unleash.DefaultUnleash;
import no.finn.unleash.Unleash;
import no.finn.unleash.util.UnleashConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile(value = {"prod", "prod-aiven", "preprod", "preprod-aiven"})
public class FeatureToggleConfig {

    @Bean
    public Unleash initializeUnleash(@Value("${spring.application.name}") String applicationName, @Value(
            "${tiltaksgjennomforing.unleash.unleash-uri}") String unleashUrl, ByEnvironmentStrategy byEnvironmentStrategy, ByApplicationStrategy byApplicationStrategy) {
        UnleashConfig config = UnleashConfig.builder()
                .appName(applicationName)
                .instanceId(applicationName)
                .unleashAPI(unleashUrl)
                .build();

        return new DefaultUnleash(
                config,
                byEnvironmentStrategy,
                byApplicationStrategy
        );
    }
}
