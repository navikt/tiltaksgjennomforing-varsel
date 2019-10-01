package no.nav.tag.tiltaksgjennomforing.varsel;

import lombok.Value;

import java.util.UUID;

@Value
public class VarselKvittering {
    private final UUID id;
    private final VarselStatus status;
}
