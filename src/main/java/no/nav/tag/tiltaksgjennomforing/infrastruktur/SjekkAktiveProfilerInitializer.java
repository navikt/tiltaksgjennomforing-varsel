package no.nav.tag.tiltaksgjennomforing.infrastruktur;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class SjekkAktiveProfilerInitializer implements ApplicationContextInitializer {

    public static final List<String> MILJOER = Arrays.asList("dev", "preprod", "preprod-aiven", "prod", "prod-aiven");

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        log.info("kjører aktiv profil: {}", (Object) applicationContext.getEnvironment().getActiveProfiles());
        if (ugyldigKjoremiljo(applicationContext.getEnvironment().getActiveProfiles())) {
            throw new IllegalStateException("Applikasjonen må startes med én av profilene aktivert: " + MILJOER.toString());
        }
    }

    private static boolean ugyldigKjoremiljo(String[] profiler) {
        int antall = 0;
        for (String profil : profiler) {
            if (MILJOER.contains(profil)) {
                antall++;
            }
        }
        return antall != 1;
    }
}
