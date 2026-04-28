package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static no.nav.tag.tiltaksgjennomforing.varsel.altinn.CacheConfig.ALTINN_TOKEN_CACHE;

@Slf4j
@Service
@RequiredArgsConstructor
public class AltinnTokenService {
    private static final String EXCHANGE_URL = "/authentication/api/v1/exchange/maskinporten";

    private final MaskinportenTokenService maskinportenTokenService;
    private final RestTemplate restTemplate;
    private final AltinnVarselProperties properties;

    @Cacheable(ALTINN_TOKEN_CACHE)
    public String hentAltinnToken() {
        log.info("Henter nytt Altinn-token via token-exchange");
        String maskinportenToken = maskinportenTokenService.hentToken();
        return exchangeToAltinnToken(maskinportenToken);
    }

    @CacheEvict(ALTINN_TOKEN_CACHE)
    public void evict() {
        log.info("Sletter cachet Altinn-token");
    }

    private String exchangeToAltinnToken(String maskinportenToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(maskinportenToken);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    properties.getBaseUrl() + EXCHANGE_URL,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    String.class
            );

            String altinnToken = response.getBody();
            if (altinnToken == null || altinnToken.isBlank()) {
                throw new AltinnException("Tomt svar fra Altinn token-exchange endepunkt");
            }
            return altinnToken;
        } catch (RestClientException e) {
            log.error("Feil ved token-exchange mot Altinn", e);
            throw new AltinnException("Feil ved token-exchange mot Altinn: " + e.getMessage());
        }
    }
}
