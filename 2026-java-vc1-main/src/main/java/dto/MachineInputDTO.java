package dto;

public record MachineInputDTO(
        String code,
        int siteId,
        String locatieInSite,
        String productinfo,
        String status,
        String productieStatus
) {}