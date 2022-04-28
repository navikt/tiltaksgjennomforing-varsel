package no.nav.tag.tiltaksgjennomforing.infrastruktur;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SjekkAktiveProfilerInitializerTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ConfigurableApplicationContext applicationContext;

    private void aktiveProfiler(String... profiler) {
        when(applicationContext.getEnvironment().getActiveProfiles()).thenReturn(profiler);
    }

    @Test()
    public void initialize__ingen_profil() {
        Assertions.assertThrows(Exception.class, () -> {
            aktiveProfiler();
            new SjekkAktiveProfilerInitializer().initialize(applicationContext);
        });
    }

    @Test()
    public void initialize__feilaktig_profil() {
        Assertions.assertThrows(Exception.class, () -> {
            aktiveProfiler("foo");
            new SjekkAktiveProfilerInitializer().initialize(applicationContext);
        });
    }

    @Test()
    public void initialize__for_mange_profiler() {
        Assertions.assertThrows(Exception.class, () -> {
            aktiveProfiler("dev", "prod");
            new SjekkAktiveProfilerInitializer().initialize(applicationContext);
        });
    }

    @Test
    public void initialize__riktig() {
        aktiveProfiler("prod", "foo");
        new SjekkAktiveProfilerInitializer().initialize(applicationContext);
    }
}