package no.nav.tag.tiltaksgjennomforing.varsel.kafka;

import lombok.extern.slf4j.Slf4j;
import no.nav.tag.tiltaksgjennomforing.varsel.VarselService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@Slf4j
@ActiveProfiles({"dev", "kafka"})
public class SmsVarselConsumerTest {

    @MockBean
    private VarselService varselService;

    private KafkaTemplate<String, String> template;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @ClassRule
    public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, false, 1, "arbeidsgiver.tiltak-sms");

    @Before
    public void setUp() {
        Map<String, Object> senderProperties = KafkaTestUtils.senderProps(embeddedKafka.getEmbeddedKafka().getBrokersAsString());
        ProducerFactory<String, String> producerFactory = new DefaultKafkaProducerFactory<>(senderProperties);

        template = new KafkaTemplate<>(producerFactory);

        for (var messageListenerContainer : kafkaListenerEndpointRegistry.getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer, embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic());
        }
    }

    @Test
    public void testReceive() {
        template.send("arbeidsgiver.tiltak-sms", "{\"smsVarselId\":\"123e4567-e89b-12d3-a456-426655440000\",\"identifikator\":\"00000000000\",\"telefonnummer\":\"11111111\",\"meldingstekst\":\"test\"}");

        // Bruker timeout siden konsumering skjer asynkront etter sending. Hvis koden skal debugges bør timeout settes høyere enn 1000 ms.
        verify(varselService, timeout(1000)).prosesserVarsel(new SmsVarselMelding(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), "00000000000", "11111111", "test", null));
    }
}