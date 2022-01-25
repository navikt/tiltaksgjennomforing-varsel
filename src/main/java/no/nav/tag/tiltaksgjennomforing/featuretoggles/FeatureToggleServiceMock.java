package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class FeatureToggleServiceMock implements FeatureToggleService {
    @Override
    public boolean smsVarselErDeaktivert() {
        return false;
    }

    @Override
    public boolean smsVarselErIgnorert() {
        return false;
    }
}
