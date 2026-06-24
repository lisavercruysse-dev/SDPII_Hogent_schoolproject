package util;

import lombok.Getter;

@Getter
public enum MeldingType {

    TEAM_AANGEMAAKT("Team aangemaakt"),
    WERKNEMER_TOEGEVOEGD("Werknemer toegevoegd"),
    SYSTEEM("Systeem"),

    // web types om problemen te voorkomen op java deel
    ALGEMEEN("Algemeen"),
    TAAK_TOEGEWEZEN("Taak toegewezen"),
    TAAK_GEWIJZIGD("Taak gewijzigd"),
    TAAK_VERWIJDERD("Taak verwijderd"),
    ZIEKMELDING("Ziekmelding"),
    VAKANTIEAANVRAAG("Vakantieaanvraag"),
    AFWEZIGHEID_GEANNULEERD("Afwezigheid geannuleerd");

    private final String display;

    MeldingType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}