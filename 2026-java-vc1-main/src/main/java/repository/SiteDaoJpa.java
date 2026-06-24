package repository;

import domein.Site;
import domein.Team;

import java.util.List;

public class SiteDaoJpa extends GenericDaoJpa<Site> implements SiteDao{
    public SiteDaoJpa() {super(Site.class);
    }

    @Override
    public List<Site> findAllActive(){
        return em.createQuery("""
            SELECT s FROM Site s 
            WHERE s.isDeleted = false 
            ORDER BY s.name
            """, Site.class)
                .getResultList();
    }

    @Override
    public Site getSiteFromTeam(int teamId) {
        return em.createQuery(
                "SELECT t.site FROM Team t WHERE t.id = :teamId", Site.class
        ).setParameter("teamId", teamId).getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public boolean existsByNaamIgnoreCase(String naam) {
        if (naam == null || naam.isBlank()) {
            return false;
        }

        return em.createQuery("""
            SELECT COUNT(s) FROM Site s 
            WHERE LOWER(s.name) = LOWER(:naam) 
              AND s.isDeleted = false
            """, Long.class)
                .setParameter("naam", naam.trim())
                .getSingleResult() > 0;
    }
}
