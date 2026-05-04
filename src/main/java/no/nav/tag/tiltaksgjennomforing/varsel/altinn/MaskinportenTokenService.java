package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class MaskinportenTokenService {
    private static final String IDENTITY_PROVIDER = "maskinporten";

    @Value("${NAIS_TOKEN_ENDPOINT:#{null}}")
    private String naisTokenEndpoint;

    @Value("${MASKINPORTEN_SCOPES:#{null}}")
    private String maskinportenScopes;

    private final RestTemplate restTemplate;

    public MaskinportenTokenService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String hentToken() {
        log.info("Henter nytt Maskinporten-token");
        return hentTokenFraMaskinporten();
    }

    private String hentTokenFraMaskinporten() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> body = Map.of(
            "identity_provider", IDENTITY_PROVIDER,
            "target", maskinportenScopes
        );

        MaskinportenTokenResponse response = restTemplate.postForObject(
                naisTokenEndpoint,
                new HttpEntity<>(body, headers),
                MaskinportenTokenResponse.class
        );

        if (response == null || response.getAccessToken() == null) {
            throw new AltinnException("Tomt svar fra Maskinporten token-endepunkt");
        }

        return response.getAccessToken();
    }
}
