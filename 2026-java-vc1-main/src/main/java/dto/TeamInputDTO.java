package dto;

import domein.Site;
import domein.Werknemer;

public record TeamInputDTO(
        String name,
        SiteDTO site,
        WerknemerDTO verantwoordelijke,
        Integer id
) {}
