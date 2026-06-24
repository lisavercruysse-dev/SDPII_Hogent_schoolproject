package dto;

import java.time.LocalDate;

public record MeldingDTO(
        long id,
        String type,
        String titel,
        String detail,
        LocalDate datum,
        boolean gelezen) {}
