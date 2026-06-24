package domein;

import dto.TeamDTO;
import dto.TeamInputDTO;
import repository.GebruikerDaoJpa;
import repository.GenericDaoJpa;
import repository.SiteDaoJpa;
import repository.TeamDaoJpa;

import java.util.List;

public class TeamController {

    private final TeamManager teamManager;

    public TeamController() {
        this.teamManager = new TeamManager(
                new TeamDaoJpa(),
                new GebruikerDaoJpa(),
                new SiteDaoJpa()

        );
    }

    private TeamDTO toDTO(Team team) {
        return new TeamDTO(team.getId(), team.getNaam(), team.getSite().getId(), team.getStatus());
    }

    public TeamDTO saveTeam(TeamInputDTO dto) {
        Team team = teamManager.saveTeam(dto);
        return toDTO(team);
    }

    public List<TeamDTO> getAllTeams(){
        List<Team> teams = teamManager.getAllTeams();
        return teams.stream().map(this::toDTO).toList();
    }

    public Team getTeamByID(int id){
        return teamManager.getTeamById(id);
    }

    public TeamDTO deleteTeam(TeamDTO team) {
        Team t = teamManager.deleteTeam(team);
        return toDTO(t);
    }

    public List<TeamDTO> getTeamsVanWerknemer(int werknemerId) {
        List<Team> teams =  teamManager.getTeamsVanWerknemer(werknemerId);
        return teams.stream().map(this::toDTO).toList();
    }

    public List<TeamDTO> getTeamsVanVerantwoordelijke(int werknemerId) {
        List<Team> teams = teamManager.getTeamsVanVerantwoordelijke(werknemerId);
        return teams.stream().map(this::toDTO).toList();
    }

    public List<TeamDTO> getTeamsVanSite(int siteId) {
        List<Team> teams = teamManager.getTeamsVanSite(siteId);
        return teams.stream().map(this::toDTO).toList();
    }

    public List<TeamDTO> getTeamsVanVerantwoordelijkeSite(int werknemerId) {
        List<Team> teams = teamManager.getTeamsVanVerantwoordelijke(werknemerId);
        return teams.stream().map(this::toDTO).toList();
    }
}
