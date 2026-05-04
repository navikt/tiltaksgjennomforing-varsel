package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MaskinportenTokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private int expiresIn;
    @JsonProperty("token_type")
    private String tokenType;
}
