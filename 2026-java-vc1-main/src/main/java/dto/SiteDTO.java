package dto;

import java.math.BigDecimal;

public record SiteDTO(
          int id,
          String name,
          String locatie,
          String land,
          Integer capaciteit,
          String operationeleStatus,
          String siteProductieStatus,
          BigDecimal breedtegraad,
          BigDecimal lengtegraad
) {}
