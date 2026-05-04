package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AltinnVarselClient {
    private static final String ORDER_PATH = "/notifications/api/v1/future/orders";

    private final RestTemplate restTemplate;
    private final AltinnVarselProperties properties;
    private final AltinnTokenService altinnTokenService;

    public OrderResponse sendSms(String idempotencyId, String phoneNumber, String body) {
        try {
            return postSmsOrder(idempotencyId, phoneNumber, body);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED || e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.warn("Autentiseringsfeil mot Altinn-3, invaliderer token-cache og prøver på nytt");
                altinnTokenService.evict();
                try {
                    return postSmsOrder(idempotencyId, phoneNumber, body);
                } catch (HttpClientErrorException retryEx) {
                    log.error("Autentiseringsfeil mot Altinn-3 etter token-refresh, status={}, body={}", retryEx.getStatusCode(), retryEx.getResponseBodyAsString(), retryEx);
                    throw new AltinnException("Autentiseringsfeil mot Altinn-3 etter token-refresh: " + retryEx.getMessage(), retryEx);
                }
            } else {
                throw new AltinnException("Feil ved sending av SMS-varsel via Altinn-3: " + e.getMessage());
            }
        }
    }

    private OrderResponse postSmsOrder(String idempotencyId, String phoneNumber, String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(altinnTokenService.hentAltinnToken());

        OrderRequest request = OrderRequest.builder()
            .idempotencyId(idempotencyId)
            .recipient(OrderRequest.Recipient.builder()
                .recipientSms(OrderRequest.RecipientSms.builder()
                    .phoneNumber(phoneNumber)
                    .smsSettings(OrderRequest.SmsSettings.builder()
                        .body(body)
                        .sendingTimePolicy(properties.getSendingTimePolicy())
                        .build()
                    )
                    .build()
                )
                .build()
            )
            .build();

        String url = properties.getBaseUrl() + ORDER_PATH;
        HttpEntity<OrderRequest> httpEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OrderResponse> response = restTemplate.postForEntity(url, httpEntity, OrderResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new AltinnException("Uventet svar fra Altinn-3 SMS API: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("Feil ved sending av SMS-varsel via Altinn-3", e);
            throw new AltinnException("Feil ved sending av SMS-varsel via Altinn-3: " + e.getMessage());
        }
    }
}
