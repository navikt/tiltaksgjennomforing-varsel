package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import lombok.extern.slf4j.Slf4j;
import no.finn.unleash.strategy.Strategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static java.util.Arrays.asList;
import static no.nav.tag.tiltaksgjennomforing.infrastruktur.SjekkAktiveProfilerInitializer.MILJOER;

@Component
@Slf4j
public class ByEnvironmentStrategy implements Strategy {

    private final String environment;

    public ByEnvironmentStrategy(@Value("${spring.profiles.active:}") String environmentList) {
        this.environment = Arrays.stream(environmentList.split(",")).filter(MILJOER::contains).findFirst().orElse("dev");
        log.info("environment set {}", this.environment);
    }

    @Override
    public String getName() {
        return "byEnvironment";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters)
                .map(map -> map.get("miljø"))
                .map(env -> asList(env.split(",")).contains(environment))
                .orElse(false);
    }
    String getEnvironment() {
        return environment;
    }
}
