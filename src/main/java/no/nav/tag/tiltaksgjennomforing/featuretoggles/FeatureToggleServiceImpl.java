package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import lombok.RequiredArgsConstructor;
import no.finn.unleash.Unleash;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"prod", "prod-aiven", "preprod", "preprod-aiven"})
@RequiredArgsConstructor
public class FeatureToggleServiceImpl implements FeatureToggleService {

    private final Unleash unleash;

    @Override
    public boolean smsVarselErDeaktivert() {
        return unleash.isEnabled("arbeidsgiver.tiltak.smsvarsel.deaktivert");
    }

    @Override
    public boolean smsVarselErIgnorert() {
        return unleash.isEnabled("arbeidsgiver.tiltak.smsvarsel.ignorert");
    }
}
