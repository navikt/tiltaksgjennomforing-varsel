package no.nav.tag.tiltaksgjennomforing.infrastruktur;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("dev")
@Slf4j
@Component
public class IntegrasjonerMockServer implements InitializingBean, DisposableBean {
    private final WireMockServer server;

    @Getter
    private int port;

    public IntegrasjonerMockServer() {
        log.info("Starter mockserver for eksterne integrasjoner.");
        server = new WireMockServer(WireMockConfiguration.options().usingFilesUnderClasspath(".").dynamicPort());
    }

    @Override
    public void destroy() {
        log.info("Stopper mockserver.");
        server.shutdown();
    }

    @Override
    public void afterPropertiesSet() {
        server.start();
        port = server.port();
        log.info("Mockserver startet på port {}", port);
        System.setProperty("wiremock.server.port", String.valueOf(port));
    }
}
