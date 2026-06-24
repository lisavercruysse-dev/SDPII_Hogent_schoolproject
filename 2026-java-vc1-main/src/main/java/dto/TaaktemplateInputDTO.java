package dto;

import domein.TaakType;

public record TaaktemplateInputDTO(Integer id, TaakType type, String omschrijving, Integer duurTijd) {
}
