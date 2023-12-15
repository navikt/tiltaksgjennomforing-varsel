package no.nav.tag.tiltaksgjennomforing.varsel.altinnvarsel;

import java.util.Objects;

import static java.util.Arrays.stream;

public enum AltinnFunksjonellFeil {
    ELEMENT_MANGLER_DATA(1, "Et av elementene: FromAddress, ShipmentDateTime, eller NotificationType mangler data. Teksten i meldingen angir hvilke felt som har mangler."),
    ADRESSE_UGYLDIG_FOR_TRANSPORTTYPE(30009, "Gis hvis man har oppgitt en adresse i ReceiverAddress når TransportType er Both, SMSPreferred eller EmailPreferred."),
    UGYLDIG_EPOSTADRESSE(30010, "Ugyldig epostadresse angitt på et ReceiverEndPoint."),
    MOBILTELEFONNUMMER_IKKE_FUNNET(30301, "Altinn har ikke klart å finne noe mobiltelefonnummer å sende varsel til. Brukes når transporttypen bør medføre varsel på SMS."),
    MOBILTELEFONNUMMER_HAR_UGYLDIG_LANDKODE(30302, "Ugyldig/ukjent landkode i et mobiltelefonnummer."),
    UGYLDIG_NORSK_MOBILTELEFONNUMMER(30303, "Ugyldig norsk mobiltelefonnummer."),
    VARSLINGSADRESSE_IKKE_REGISTRERT_FOR_ORGANISASJON(30304, "Avgiver av typen organisasjon har ikke registrert noen varslingsadresse som kan benyttes i varsel på angitt kanal."),
    INGEN_VARSLINGSADRESSE_KAN_BENYTTES(30306, "Avgiver har ingen varslingsadresse som kan benyttes til å sende varsel på angitt kanal."),
    MANGLER_VARSLINGSADRESSE_FOR_TRANSPORTTYPE_BOTH(30309, "Avgiver har ikke varslingsadresse for både epost og SMS. Denne feilen kan oppstå for transporttype Both."),
    SPRAAKKODE_UGYLDIG(40001, "Ugyldig angitt språkkode."),
    ANTALL_VARSLER_OVERSTIGER_GRENSE(40014, "Angitt antall varsler overstiger konfigurert grense."),
    TRANSPORTTYPE_MAA_VAERE_SMS_ELLER_EMAIL(40015, "TransportType må være enten SMS eller Email."),
    UGYLDIG_FOEDSELSNUMMER(40016, "Angitt fødselsnummer er ikke gyldig."),
    UGYLDIG_EPOST_I_FROMADRESS(40020, "Parameter FromAddress må være en gyldig e-post adresse."),
    UGYLDIG_AVGIVER(60012, "Ugyldig avgiver. Altinn klarer ikke identifisere noen avgiver basert på angitt ReporteeNumber.");

    public final Integer feilkode;
    public final String beskrivelse;

    AltinnFunksjonellFeil(Integer feilkode, String beskrivelse) {
        this.feilkode = feilkode;
        this.beskrivelse = beskrivelse;
    }

    public static boolean erFunksjonellFeil(Integer feilkode) {
        return stream(values()).anyMatch(feil -> Objects.equals(feil.feilkode, feilkode));
    }
}
