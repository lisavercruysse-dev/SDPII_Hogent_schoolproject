package repository;

import domein.Site;
import domein.Team;

import java.util.List;

public interface TeamDao extends GenericDao<Team>{

    public Site getSiteFromTeam(int teamId);
    public List<Team> getTeamsVanWerknemer(int werknemerId);
    public List<Team> getTeamsVanVerantwoordelijke(int werknemerId);
    public List<Team> getTeamsVanSite(int teamId);

}
