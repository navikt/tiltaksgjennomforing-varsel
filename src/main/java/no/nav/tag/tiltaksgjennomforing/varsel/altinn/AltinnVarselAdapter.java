package no.nav.tag.tiltaksgjennomforing.varsel.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AltinnVarselAdapter {
    private final AltinnVarselClient altinnVarselClient;

    public String sendVarsel(String smsVarselId, String telefonnummer, String varseltekst) {
        OrderResponse response = altinnVarselClient.sendSms(smsVarselId, telefonnummer, varseltekst);
        return response.getNotificationOrderId().toString();
    }
}
