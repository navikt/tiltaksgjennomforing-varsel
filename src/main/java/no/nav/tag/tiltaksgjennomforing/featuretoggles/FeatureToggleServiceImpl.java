package no.nav.tag.tiltaksgjennomforing.featuretoggles;

import lombok.RequiredArgsConstructor;
import no.finn.unleash.Unleash;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"prod", "preprod"})
@RequiredArgsConstructor
public class FeatureToggleServiceImpl implements FeatureToggleService {

    private final Unleash unleash;

    @Override
    public boolean smsVarselErAktiv() {
        return unleash.isEnabled("tag.tiltak.smsvarsel");
    }
}
