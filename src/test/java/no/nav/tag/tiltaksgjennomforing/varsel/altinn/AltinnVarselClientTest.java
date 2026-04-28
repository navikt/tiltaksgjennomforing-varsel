package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class AltinnVarselClientTest {
    private static final String VALID_RESPONSE_BODY =
            "{\"notificationOrderId\":\"3fa85f64-5717-4562-b3fc-2c963f66afa6\",\"notification\":{\"shipmentId\":\"4fa85f64-5717-4562-b3fc-2c963f66afa6\",\"sendersReference\":\"ref-1\",\"reminders\":[]}}";

    private WireMockServer wireMock;
    private AltinnVarselClient client;
    private AltinnTokenService altinnTokenService;

    @BeforeEach
    void setUp() {
        wireMock = new WireMockServer(WireMockConfiguration.options().dynamicPort());
        wireMock.start();

        wireMock.stubFor(post(urlEqualTo("/notifications/api/v1/future/orders"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(VALID_RESPONSE_BODY)));

        AltinnVarselProperties properties = new AltinnVarselProperties();
        properties.setBaseUrl("http://localhost:" + wireMock.port());
        properties.setSendingTimePolicy("Daytime");

        altinnTokenService = mock(AltinnTokenService.class);
        when(altinnTokenService.hentAltinnToken()).thenReturn("test-altinn-token");

        client = new AltinnVarselClient(new RestTemplate(), properties, altinnTokenService);
    }

    @AfterEach
    void tearDown() {
        wireMock.stop();
    }

    @Test
    void sendSms_vellykket() {
        assertThatCode(() -> client.sendSms("idempotency-123", "+4712345678", "Testvarsel"))
                .doesNotThrowAnyException();

        wireMock.verify(postRequestedFor(urlEqualTo("/notifications/api/v1/future/orders"))
                .withHeader("Authorization", equalTo("Bearer test-altinn-token"))
                .withRequestBody(matchingJsonPath("$.idempotencyId", equalTo("idempotency-123")))
                .withRequestBody(matchingJsonPath("$.recipient.recipientSms.phoneNumber", equalTo("+4712345678")))
                .withRequestBody(matchingJsonPath("$.recipient.recipientSms.smsSettings.body", equalTo("Testvarsel")))
                .withRequestBody(matchingJsonPath("$.recipient.recipientSms.smsSettings.sendingTimePolicy", equalTo("Daytime"))));
    }

    @Test
    void sendSms_kasterException_ved_http_feil() {
        wireMock.stubFor(post(urlEqualTo("/notifications/api/v1/future/orders"))
                .willReturn(aResponse().withStatus(500)));

        assertThatThrownBy(() -> client.sendSms("idempotency-456", "+4712345678", "Testvarsel"))
                .isInstanceOf(AltinnException.class);
    }

    @Test
    void sendSms_evicterer_token_og_prover_pa_nytt_ved_401() {
        wireMock.stubFor(post(urlEqualTo("/notifications/api/v1/future/orders"))
                .inScenario("retry")
                .whenScenarioStateIs("Started")
                .willReturn(aResponse().withStatus(401))
                .willSetStateTo("retry"));

        wireMock.stubFor(post(urlEqualTo("/notifications/api/v1/future/orders"))
                .inScenario("retry")
                .whenScenarioStateIs("retry")
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody(VALID_RESPONSE_BODY)));

        assertThatCode(() -> client.sendSms("idempotency-789", "+4712345678", "Testvarsel"))
                .doesNotThrowAnyException();

        verify(altinnTokenService).evict();
        verify(altinnTokenService, times(2)).hentAltinnToken();
    }
}
