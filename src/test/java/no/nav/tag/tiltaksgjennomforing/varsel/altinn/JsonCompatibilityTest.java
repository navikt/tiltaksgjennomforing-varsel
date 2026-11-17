package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@SpringBootTest
@DirtiesContext
public class JsonCompatibilityTest {
    private final JsonMapper httpObjectMapper;

    @Autowired
    public JsonCompatibilityTest(RestTemplate restTemplate) {
        httpObjectMapper = restTemplate.getMessageConverters().stream()
                .filter(JacksonJsonHttpMessageConverter.class::isInstance)
                .map(JacksonJsonHttpMessageConverter.class::cast)
                .map(JacksonJsonHttpMessageConverter::getMapper)
                .findFirst()
                .orElseThrow();
    }

    @Test
    public void order_request_serializes_expected_structure_and_omits_nulls() throws Exception {
        OrderRequest request = OrderRequest.builder()
                .idempotencyId("synthetic-order-id")
                .sendersReference(null)
                .recipient(OrderRequest.Recipient.builder()
                        .recipientSms(OrderRequest.RecipientSms.builder()
                                .phoneNumber("+4712345678")
                                .smsSettings(OrderRequest.SmsSettings.builder()
                                        .sender(null)
                                        .body("synthetic message")
                                        .sendingTimePolicy(null)
                                        .build())
                                .build())
                        .build())
                .build();

        JsonNode expectedJson = httpObjectMapper.readTree(
                "{\"idempotencyId\":\"synthetic-order-id\",\"recipient\":{\"recipientSms\":{"
                        + "\"phoneNumber\":\"+4712345678\",\"smsSettings\":{\"body\":\"synthetic message\"}}}}");
        JsonNode actualJson = httpObjectMapper.valueToTree(request);
        assertThat(actualJson).isEqualTo(expectedJson);
    }

    @Test
    public void maskinporten_response_reads_valid_expires_in_and_string_fields() throws Exception {
        MaskinportenTokenResponse response = httpObjectMapper.readValue(
                "{\"access_token\":\"synthetic-access-token\",\"expires_in\":3600,\"token_type\":\"Bearer\"}",
                MaskinportenTokenResponse.class);

        assertThat(response.getAccessToken()).isEqualTo("synthetic-access-token");
        assertThat(response.getExpiresIn()).isEqualTo(3600);
        assertThat(response.getTokenType()).isEqualTo("Bearer");
    }

    @Test
    public void maskinporten_response_string_fields_default_to_null_when_null_or_omitted() throws Exception {
        MaskinportenTokenResponse explicitNulls = httpObjectMapper.readValue(
                "{\"access_token\":null,\"expires_in\":3600,\"token_type\":null}",
                MaskinportenTokenResponse.class);
        MaskinportenTokenResponse omittedFields = httpObjectMapper.readValue(
                "{\"expires_in\":30}",
                MaskinportenTokenResponse.class);

        assertThat(explicitNulls.getAccessToken()).isNull();
        assertThat(explicitNulls.getTokenType()).isNull();
        assertThat(omittedFields.getAccessToken()).isNull();
        assertThat(omittedFields.getTokenType()).isNull();
    }
}
