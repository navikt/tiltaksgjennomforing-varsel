package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import no.nav.tag.tiltaksgjennomforing.varsel.VarselStatus;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SmsVarselMeldingTest {
    private static final String TOPIC = "sms-varsel";
    private static final String TYPE_ID_HEADER = "__TypeId__";
    private final JsonMapper objectMapper = JsonMapper.builder().findAndAddModules().build();

    private SmsVarselMelding meldingMedTlf(String telefonnummer) {
        return new SmsVarselMelding(UUID.randomUUID(), "identifikator", telefonnummer, "tekst", null);
    }

    @Test
    public void kafka_json_serializer_and_deserializer_preserve_message_contract() throws Exception {
        UUID smsVarselId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        SmsVarselMelding melding = new SmsVarselMelding(
                smsVarselId, "synthetic-id", "+4712345678", "synthetic message", "test-app");
        Headers headers = new RecordHeaders();
        byte[] payload = new JacksonJsonSerializer<SmsVarselMelding>().serialize(TOPIC, headers, melding);

        JsonNode expectedJson = objectMapper.readTree(
                "{\"smsVarselId\":\"123e4567-e89b-12d3-a456-426614174000\","
                        + "\"identifikator\":\"synthetic-id\",\"telefonnummer\":\"+4712345678\","
                        + "\"meldingstekst\":\"synthetic message\",\"avsenderApplikasjon\":\"test-app\","
                        + "\"telefonnummerMedLandskode\":\"+4712345678\"}");
        assertThat(objectMapper.readTree(payload)).isEqualTo(expectedJson);
        assertThat(headers.lastHeader(TYPE_ID_HEADER)).isNotNull();
        assertThat(new String(headers.lastHeader(TYPE_ID_HEADER).value(), StandardCharsets.UTF_8))
                .isEqualTo(SmsVarselMelding.class.getName());

        SmsVarselMelding deserialized = new JacksonJsonDeserializer<>(SmsVarselMelding.class)
                .deserialize(TOPIC, headers, payload);
        assertThat(deserialized).isEqualTo(melding);
    }

    @Test
    public void kafka_json_deserializer_accepts_explicit_null_and_omitted_optional_sender() {
        String explicitNullSender = "{\"smsVarselId\":\"123e4567-e89b-12d3-a456-426614174000\","
                + "\"identifikator\":\"synthetic-id\",\"telefonnummer\":\"+4712345678\","
                + "\"meldingstekst\":\"synthetic message\",\"avsenderApplikasjon\":null}";
        String omittedSenderWithUnknownField = "{\"smsVarselId\":\"123e4567-e89b-12d3-a456-426614174000\","
                + "\"identifikator\":\"synthetic-id\",\"telefonnummer\":\"+4712345678\","
                + "\"meldingstekst\":\"synthetic message\",\"futureField\":\"ignored\"}";

        assertThat(deserializeKafkaJson(explicitNullSender).getAvsenderApplikasjon()).isNull();
        assertThat(deserializeKafkaJson(omittedSenderWithUnknownField).getAvsenderApplikasjon()).isNull();
    }

    @Test
    public void kafka_result_json_preserves_fields_status_and_type_header() throws Exception {
        SmsVarselResultatMelding resultat = new SmsVarselResultatMelding(
                UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), VarselStatus.SENDT);
        Headers headers = new RecordHeaders();
        byte[] payload = new JacksonJsonSerializer<SmsVarselResultatMelding>()
                .serialize(TOPIC, headers, resultat);

        assertThat(objectMapper.readTree(payload)).isEqualTo(objectMapper.readTree(
                "{\"smsVarselId\":\"123e4567-e89b-12d3-a456-426614174000\",\"status\":\"SENDT\"}"));
        assertThat(headers.lastHeader(TYPE_ID_HEADER)).isNotNull();
        assertThat(new String(headers.lastHeader(TYPE_ID_HEADER).value(), StandardCharsets.UTF_8))
                .isEqualTo(SmsVarselResultatMelding.class.getName());
    }

    private SmsVarselMelding deserializeKafkaJson(String json) {
        Headers headers = new RecordHeaders();
        headers.add(TYPE_ID_HEADER, SmsVarselMelding.class.getName().getBytes(StandardCharsets.UTF_8));
        return new JacksonJsonDeserializer<>(SmsVarselMelding.class).deserialize(
                TOPIC, headers, json.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_naar_pluss_prefiks() {
        assertThat(meldingMedTlf("+4712345678").getTelefonnummerMedLandskode()).isEqualTo("+4712345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_for_annet_land_med_pluss() {
        assertThat(meldingMedTlf("+4612345678").getTelefonnummerMedLandskode()).isEqualTo("+4612345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__returnerer_uendret_med_00_prefiks() {
        assertThat(meldingMedTlf("004712345678").getTelefonnummerMedLandskode()).isEqualTo("004712345678");
    }

    @Test
    public void getTelefonnummerMedLandskode__legger_til_norsk_landkode_naar_ingen_prefiks() {
        assertThat(meldingMedTlf("12345678").getTelefonnummerMedLandskode()).isEqualTo("+4712345678");
    }
}
