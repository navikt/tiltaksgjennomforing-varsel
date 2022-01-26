package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel.AltinnVarselAdapter;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselMelding;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselResultatProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles({"dev", "kafka"})
@EmbeddedKafka(topics = {"sms", "sms-resultat"})
public class VarselServiceTest {
    @Autowired
    private VarselService varselService;
    @SpyBean
    private VarselKvitteringRepository repository;
    @MockBean
    private AltinnVarselAdapter altinnVarselAdapter;
    @MockBean
    private SmsVarselResultatProducer resultatProducer;

    @Test
    public void prosesserMelding__sender_varsel_bare_en_gang() {
        UUID smsVarselId = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        SmsVarselMelding melding = new SmsVarselMelding(smsVarselId, "00000000000", "11111111", "test", null);

        varselService.prosesserVarsel(melding);
        varselService.prosesserVarsel(melding);

        verify(altinnVarselAdapter, times(1)).sendVarsel(anyString(), anyString(), anyString());
        verify(repository, times(1)).insert(any(VarselKvittering.class));
        verify(resultatProducer, times(1)).sendMeldingTilKafka(any(VarselKvittering.class));

        assertThat(repository.findById(smsVarselId)).isPresent();
    }
}