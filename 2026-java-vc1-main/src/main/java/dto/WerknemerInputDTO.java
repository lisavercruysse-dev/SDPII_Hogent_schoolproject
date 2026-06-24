package dto;

import domein.JobTitel;
import domein.Team;

import java.time.LocalDate;

public record WerknemerInputDTO(
        Integer id,
        String voornaam,
        String achternaam,
        JobTitel jobTitel,
        String telefoon,
        LocalDate geboortedatum,
        String land,
        String postcode,
        String stad,
        String straat,
        Integer huisnummer,
        Integer bus
) {}