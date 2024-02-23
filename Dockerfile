FROM ghcr.io/navikt/baseimages/temurin:11
COPY import-vault-token.sh /init-scripts
COPY /target/tiltaksgjennomforing-varsel-1.0.0-SNAPSHOT.jar app.jar