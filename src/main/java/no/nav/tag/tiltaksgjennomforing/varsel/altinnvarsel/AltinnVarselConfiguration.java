package no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel;

import lombok.RequiredArgsConstructor;
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile({"dev"})
public class AltinnVarselConfiguration {
    private final AltinnVarselProperties varselProperties;

    @Bean
    public INotificationAgencyExternalBasic iNotificationAgencyExternalBasic() {
        return WsClient.createPort(varselProperties.getUri().toString(), INotificationAgencyExternalBasic.class);
    }
}
