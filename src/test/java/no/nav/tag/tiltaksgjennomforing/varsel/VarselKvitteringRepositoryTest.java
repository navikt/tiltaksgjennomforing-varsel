package no.nav.tag.tiltaksgjennomforing.varsel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@SpringBootTest(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
@DirtiesContext
public class VarselKvitteringRepositoryTest {
    @Autowired
    private VarselKvitteringRepository repository;

    @Test
    public void existsById__id_fra_testdata() {
        assertThat(repository.existsById(UUID.fromString("123e4567-e89b-12d3-a456-426655440000"))).isTrue();
    }

    @Test
    public void existsById__ukjent_id() {
        assertThat(repository.existsById(UUID.randomUUID())).isFalse();
    }

    @Test
    public void insert__kan_lagres() {
        UUID id = UUID.randomUUID();
        VarselKvittering varselKvittering = new VarselKvittering(id, VarselStatus.SENDT);
        repository.insert(varselKvittering);
        assertThat(repository.existsById(id)).isTrue();
    }

    @Test
    public void insert__kan_hentes_opp() {
        UUID id = UUID.randomUUID();
        VarselKvittering varselKvittering = new VarselKvittering(id, VarselStatus.SENDT);
        repository.insert(varselKvittering);
        assertThat(repository.findById(id)).hasValue(varselKvittering);
    }
}