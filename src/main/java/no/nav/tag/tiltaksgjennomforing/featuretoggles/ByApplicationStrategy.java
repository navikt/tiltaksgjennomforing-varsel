package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import no.finn.unleash.strategy.Strategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class ByApplicationStrategy implements Strategy {

    private final String springApplicationName;

    public ByApplicationStrategy(
            @Value("${spring.application.name}") String springApplicationName
    ) {
        this.springApplicationName = springApplicationName;
    }

    @Override
    public String getName() {
        return "byApplication";
    }

    @Override
    public boolean isEnabled(Map<String, String> parameters) {
        return Optional.ofNullable(parameters)
                .map(map -> map.get("app"))
                .map(app -> app.equals(springApplicationName))
                .orElse(false);
    }
}
