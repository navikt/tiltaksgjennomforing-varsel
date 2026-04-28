package no.nav.tag.tiltaksgjennomforing.exceptions;

public class AltinnException extends RuntimeException {
    public AltinnException(String message) {
        super(message);
    }

    public AltinnException(String message, Throwable cause) {
        super(message, cause);
    }
}
