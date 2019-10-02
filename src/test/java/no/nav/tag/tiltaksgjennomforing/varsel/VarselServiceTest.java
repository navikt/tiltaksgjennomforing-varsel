package no.nav.tag.tiltaksgjennomforing.varsel;

import no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleService;
import no.nav.tag.tiltaksgjennomforing.featuretoggles.FeatureToggleServiceMock;
import no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel.AltinnVarselAdapter;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselMelding;
import no.nav.tag.tiltaksgjennomforing.varsel.kafka.SmsVarselResultatProducer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VarselServiceTest {
    @InjectMocks
    private VarselService varselService;
    @Mock
    private VarselKvitteringRepository repository;
    @Mock
    private AltinnVarselAdapter altinnVarselAdapter;
    @Mock
    private SmsVarselResultatProducer resultatProducer;
    @Spy
    private FeatureToggleService featureToggleService = new FeatureToggleServiceMock();

    @Test
    public void prosesserMelding__sender_varsel_bare_en_gang() {
        SmsVarselMelding melding = new SmsVarselMelding(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"), "00000000000", "11111111", "test");

        when(repository.existsById(any())).thenReturn(false);
        varselService.prosesserVarsel(melding);
        when(repository.existsById(any())).thenReturn(true);
        varselService.prosesserVarsel(melding);

        verify(altinnVarselAdapter).sendVarsel(anyString(), anyString(), anyString());
        verify(repository).insert(any(VarselKvittering.class));
        verify(resultatProducer).sendMeldingTilKafka(any(VarselKvittering.class));
    }
}