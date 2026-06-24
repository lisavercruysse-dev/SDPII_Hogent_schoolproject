package repository;

import domein.Site;
import domein.Team;
import domein.Werknemer;

import java.util.List;

public class TeamDaoJpa extends GenericDaoJpa<Team> implements TeamDao{
    public TeamDaoJpa() {
        super(Team.class);
    }

    @Override
    public Site getSiteFromTeam(int teamId) {
        Team team = em.find(Team.class, teamId);
        return team != null ? team.getSite() : null;
    }


    @Override
    public List<Team> getTeamsVanWerknemer(int werknemerId) {
        return em.createQuery(
                        "SELECT t FROM Werknemer w JOIN w.teams t WHERE w.id = :werknemerId", Team.class)
                .setParameter("werknemerId", werknemerId)
                .getResultList();
    }

    @Override
    public List<Team> getTeamsVanVerantwoordelijke(int werknemerId) {
        return em.createQuery(
                        "SELECT t FROM Team t WHERE t.verantwoordelijke.id = :werknemerId", Team.class)
                .setParameter("werknemerId", werknemerId)
                .getResultList();
    }

    @Override
    public List<Team> getTeamsVanSite(int siteId) {
        return em.createQuery(
                        "SELECT t FROM Team t WHERE t.site.id = :siteId", Team.class)
                .setParameter("siteId", siteId)
                .getResultList();
    }
}
