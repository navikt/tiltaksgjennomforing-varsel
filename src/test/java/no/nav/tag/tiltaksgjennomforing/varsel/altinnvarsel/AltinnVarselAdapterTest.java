package no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext
public class AltinnVarselAdapterTest {
    @Autowired
    private AltinnVarselAdapter altinnVarselAdapter;

    @Test
    public void sendVarsel() {
        altinnVarselAdapter.sendVarsel("TestData.enIdentifikator()", "12345678", "varseltekst");
    }
}