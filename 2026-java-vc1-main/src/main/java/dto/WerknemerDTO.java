package dto;

import domein.EntityStatus;

import java.time.LocalDate;

public record WerknemerDTO(
        int id,
        String voornaam,
        String achternaam,
        String jobTitel,
        String telefoon,
        LocalDate geboortedatum,
        String land,
        String postcode,
        String stad,
        String straat,
        Integer huisnummer,
        Integer bus,
        String email,
        EntityStatus status
) {}
