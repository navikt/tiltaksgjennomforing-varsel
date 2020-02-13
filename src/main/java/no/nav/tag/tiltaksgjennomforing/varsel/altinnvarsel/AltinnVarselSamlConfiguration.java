package no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel;

import lombok.RequiredArgsConstructor;
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import no.nav.tag.tiltaksgjennomforing.infrastruktur.StsProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"preprod", "prod"})
@RequiredArgsConstructor
public class AltinnVarselSamlConfiguration {
    private final AltinnVarselProperties varselProperties;
    private final StsProperties stsProperties;

    @Bean
    public INotificationAgencyExternalBasic iNotificationAgencyExternalBasic() {
        INotificationAgencyExternalBasic port = WsClient.createPort(varselProperties.getUri().toString(), INotificationAgencyExternalBasic.class, varselProperties.isDebugLog());
        STSClientConfigurer configurer = new STSClientConfigurer(stsProperties.getWsUri(), stsProperties.getUsername(), stsProperties.getPassword(), varselProperties.isDebugLog());
        configurer.configureRequestSamlToken(port);
        return port;
    }
}
