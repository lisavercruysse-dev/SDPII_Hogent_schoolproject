package dto;

import domein.EntityStatus;
import domein.TaakType;

public record TaaktemplateDTO(int id, TaakType type, String omschrijving, int duurTijd, EntityStatus status) {
}
