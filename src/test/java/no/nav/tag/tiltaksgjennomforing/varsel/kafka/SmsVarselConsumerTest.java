package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselService;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@DirtiesContext
@Slf4j
@ActiveProfiles({"dev", "kafka"})
@EnableKafka
@EmbeddedKafka(partitions = 1, topics = { "arbeidsgiver.tiltak-sms" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SmsVarselConsumerTest {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Autowired
    SmsVarselConsumer smsVarselConsumer;

    @MockBean
    private VarselService varselService;

    private KafkaTemplate<String, SmsVarselMelding> kafkaTemplate;

    @BeforeAll
    public void setUp() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(props));
    }

    @Test
    public void testReceive() throws InterruptedException {
        SmsVarselMelding varselMelding = new SmsVarselMelding(
                UUID.fromString( "123e4567-e89b-12d3-a456-426655440000"),
                "00000000000",
                "11111111",
                "avtale om arbeidstrening iverksatt",
                "tiltaksgjennomforing-varsel"
        );
        kafkaTemplate.send("arbeidsgiver.tiltak-sms", "test", varselMelding);
        Thread.sleep(500L);
        // Bruker timeout siden konsumering skjer asynkront etter sending. Hvis koden skal debugges bør timeout settes høyere enn 1000 ms.
        verify(varselService, timeout(500)).prosesserVarsel(new SmsVarselMelding(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), "00000000000", "11111111", "avtale om arbeidstrening iverksatt", "tiltaksgjennomforing-varsel"));
    }
}