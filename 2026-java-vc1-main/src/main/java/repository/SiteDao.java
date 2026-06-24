package repository;

import domein.Site;

import java.util.List;

public interface SiteDao extends GenericDao<Site>{
    List<Site> findAllActive();
    Site getSiteFromTeam(int teamId);

    boolean existsByNaamIgnoreCase(String naam);
}
