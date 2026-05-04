package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class OrderResponse {

    private UUID notificationOrderId;
    private Notification notification;

    @Data
    @NoArgsConstructor
    public static class Notification {
        private UUID shipmentId;
        private String sendersReference;
        private List<Shipment> reminders;
    }

    @Data
    @NoArgsConstructor
    public static class Shipment {
        private UUID shipmentId;
        private String sendersReference;
    }
}
