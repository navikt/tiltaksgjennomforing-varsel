package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "tiltaksgjennomforing.altinn")
public class AltinnVarselProperties {
    private String baseUrl;
    private String sendingTimePolicy = "Daytime";
}
