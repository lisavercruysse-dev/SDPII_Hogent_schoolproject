package dto;

import java.time.LocalDate;

public record MachineDTO(
        int id,
        String code,
        String siteNaam,
        String locatieInSite,
        String productinfo,
        String status,
        String productieStatus,
        String geformatteerdeUptime,
        LocalDate datumLaatsteOnderhoud,
        long dagenSindsOnderhoud
) {}