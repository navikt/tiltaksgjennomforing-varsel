package no.nav.tag.tiltaksgjennomforing.infrastruktur;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.net.URI;

@Data
@Component
@ConfigurationProperties(prefix = "tiltaksgjennomforing.sts")
public class StsProperties {
    private URI wsUri;
    private String username;
    private String password;
}
