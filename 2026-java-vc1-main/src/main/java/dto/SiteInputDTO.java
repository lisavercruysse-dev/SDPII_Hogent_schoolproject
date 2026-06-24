package dto;

import java.math.BigDecimal;

public record SiteInputDTO (
        String name,
        String locatie,
        String land,
        int capaciteit,
        String operationeleStatus,
        String siteProductieStatus,
        BigDecimal breedtegraad,
        BigDecimal lengtegraad
){ }
