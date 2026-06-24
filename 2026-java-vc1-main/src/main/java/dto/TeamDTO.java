package dto;

import domein.EntityStatus;

public record TeamDTO (int id, String naam, int siteId, EntityStatus status) {}
