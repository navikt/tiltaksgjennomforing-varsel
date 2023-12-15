package no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.altinn.schemas.serviceengine.formsengine._2009._10.TransportType;
import no.altinn.schemas.services.serviceengine.notification._2009._10.*;
import no.altinn.schemas.services.serviceengine.standalonenotificationbe._2009._10.StandaloneNotificationBEList;
import no.altinn.services.common.fault._2009._10.AltinnFault;
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasic;
import no.altinn.services.serviceengine.notification._2010._10.INotificationAgencyExternalBasicSendStandaloneNotificationBasicV3AltinnFaultFaultFaultMessage;
import no.nav.tag.tiltaksgjennomforing.exceptions.AltinnException;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import static no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel.AltinnFunksjonellFeil.erFunksjonellFeil;

@Service
@Slf4j
@RequiredArgsConstructor
public class AltinnVarselAdapter {
    private static final String NAMESPACE = "http://schemas.altinn.no/services/ServiceEngine/Notification/2009/10";

    private final INotificationAgencyExternalBasic iNotificationAgencyExternalBasic;
    private final AltinnVarselProperties varselProperties;

    private static JAXBElement<String> ns(String localpart, String value) {
        return new JAXBElement<>(new QName(NAMESPACE, localpart), String.class, value);
    }

    private static <T> JAXBElement<T> ns(String localpart, Class<T> clazz, T value) {
        return new JAXBElement<>(new QName(NAMESPACE, localpart), clazz, value);
    }

    private static JAXBElement<Boolean> ns(String localpart, Boolean value) {
        return new JAXBElement<>(new QName(NAMESPACE, localpart), Boolean.class, value);
    }

    public void sendVarsel(String avgiver, String telefonnummer, String varseltekst) {
        StandaloneNotificationBEList standaloneNotification = new StandaloneNotificationBEList().withStandaloneNotification(new StandaloneNotification()
                .withIsReservable(ns("IsReservable", false))
                .withLanguageID(1044)
                .withNotificationType(ns("NotificationType", "VarselDPVUtenRevarsel"))
                .withReceiverEndPoints(ns("ReceiverEndPoints", ReceiverEndPointBEList.class, new ReceiverEndPointBEList()
                        .withReceiverEndPoint(new ReceiverEndPoint()
                                .withTransportType(ns("TransportType", TransportType.class, TransportType.SMS))
                                .withReceiverAddress(ns("ReceiverAddress", telefonnummer)))))
                .withReporteeNumber(ns("ReporteeNumber", avgiver))
                .withTextTokens(ns("TextTokens", TextTokenSubstitutionBEList.class, new TextTokenSubstitutionBEList()
                        .withTextToken(new TextToken()
                                .withTokenValue(ns("TokenValue", varseltekst)))))
                .withUseServiceOwnerShortNameAsSenderOfSms(ns("UseServiceOwnerShortNameAsSenderOfSms", true)));
        try {
            iNotificationAgencyExternalBasic.sendStandaloneNotificationBasicV3(
                    varselProperties.getSystemBruker(),
                    varselProperties.getSystemPassord(),
                    standaloneNotification);
        } catch (INotificationAgencyExternalBasicSendStandaloneNotificationBasicV3AltinnFaultFaultFaultMessage e) {
            log.error("Feil ved varsling gjennom Altinn", e);
            prosesserVarselFeil(e);
            throw new AltinnException("Feil ved varsling gjennom Altinn");
        } catch (RuntimeException e) {
            log.error("Feil ved varsling gjennom Altinn", e);
            throw new AltinnException("Feil ved varsling gjennom Altinn");
        }
    }

    private void prosesserVarselFeil(INotificationAgencyExternalBasicSendStandaloneNotificationBasicV3AltinnFaultFaultFaultMessage e) {
        final String altinnErrorMessage = constructAltinnErrorMessage(e);
        Integer feilkode = getFeilkode(e);
        log.error("Feil ved varsling gjennom Altinn. Feilkode: {}", feilkode);
        if (erFunksjonellFeil(feilkode)) {
            log.error("Funksjonell feil i kall mot Altinn. ErorrMessage: {}", altinnErrorMessage);
        }
    }

    // Liste over errorID: https://altinn.github.io/docs/api/tjenesteeiere/soap/grensesnitt/varseltjeneste/#feilsituasjoner
    private String constructAltinnErrorMessage(INotificationAgencyExternalBasicSendStandaloneNotificationBasicV3AltinnFaultFaultFaultMessage e) {
        AltinnFault faultInfo = e.getFaultInfo();
        if (faultInfo == null) {
            return e.getMessage();
        }
        return "errorGuid=" + unwrap(faultInfo.getErrorGuid()) + ", " +
                "userGuid=" + unwrap(faultInfo.getUserGuid()) + ", " +
                "errorId=" + faultInfo.getErrorID() + ", " +
                "errorMessage=" + unwrap(faultInfo.getAltinnErrorMessage());
    }
    private Integer getFeilkode(INotificationAgencyExternalBasicSendStandaloneNotificationBasicV3AltinnFaultFaultFaultMessage e) {
        AltinnFault faultInfo = e.getFaultInfo();
        if (faultInfo == null) {
            return null;
        }
        return faultInfo.getErrorID();
    }
    private String unwrap(JAXBElement<String> jaxbElement) {
        if (jaxbElement == null) {
            return "null";
        }
        return jaxbElement.getValue();
    }


}
