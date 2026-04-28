package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {

    public static final String ALTINN_TOKEN_CACHE = "altinn_token_cache";

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(ALTINN_TOKEN_CACHE);
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1)
                .expireAfterWrite(25, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }
}
