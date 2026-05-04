package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    String idempotencyId;
    String sendersReference;
    Recipient recipient;

    @Value
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Recipient {
        RecipientSms recipientSms;
    }

    @Value
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class RecipientSms {
        String phoneNumber;
        SmsSettings smsSettings;
    }

    @Value
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SmsSettings {
        String sender;
        String body;
        String sendingTimePolicy;
    }
}
