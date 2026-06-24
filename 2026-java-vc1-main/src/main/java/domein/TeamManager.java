package domein;

import dto.TeamDTO;
import dto.TeamInputDTO;
import exception.TaaktemplateInformationException;
import exception.TeamInformationException;
import repository.GebruikerDao;
import repository.GenericDao;
import repository.SiteDao;
import repository.TeamDao;

import java.util.List;
import java.util.Set;

public class TeamManager {

    private final TeamDao teamRepository;
    private final GebruikerDao werknemerRepository;
    private final SiteDao siteRepository;

    public TeamManager(TeamDao teamRepository, GebruikerDao werknemerRepository, SiteDao siteRepository) {
        this.teamRepository = teamRepository;
        this.werknemerRepository = werknemerRepository;
        this.siteRepository = siteRepository;
    }

    public Team saveTeam(TeamInputDTO dto) {
        boolean bestaatAl = getAllTeams().stream()
                .anyMatch(t -> t.getNaam().trim().equalsIgnoreCase(dto.name().trim())
                        && t.getSite().getId() == dto.site().id()
                        && (dto.id() == null || t.getId() != dto.id()));
        if (bestaatAl) {
            throw new IllegalArgumentException("Een team met deze naam en site bestaat al.");
        }

        Werknemer verantwoordelijke = dto.verantwoordelijke() != null ?
                werknemerRepository.get(dto.verantwoordelijke().id()) :
                null;
        Site site = dto.site() != null ?
                siteRepository.get(dto.site().id()) :
                null;

        Team team;
        if (dto.id() != null) {
            team = teamRepository.get(dto.id());
            team.errors.clear();
            team.setNaam(dto.name());
            team.setVerantwoordelijke(verantwoordelijke);
            team.setSite(site);

            if (!team.errors.isEmpty()) {
                throw new TeamInformationException(team.errors);
            }

        } else {
            team = new Team(verantwoordelijke, dto.name(), site);
        }

        teamRepository.startTransaction();
        try {
            if (dto.id() == null) teamRepository.insert(team);
            else teamRepository.update(team);
            teamRepository.commitTransaction();
        } catch (Exception ex) {
            teamRepository.rollbackTransaction();
            throw ex;
        }
        return team;
    }

    public List<Team> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams;
    }

    public Team getTeamById(int id) {
        return this.teamRepository.get(id);
    }

    public Team deleteTeam(TeamDTO dto) {
        Team team = teamRepository.get(dto.id());
        if (team == null) {
            throw new IllegalArgumentException("Er is geen team geselecteerd.");
        }

        if (team.getWerknemers() != null && !team.getWerknemers().isEmpty()) {
            throw new IllegalArgumentException("Dit team kan niet worden verwijderd omdat het nog leden bevat.");
        }

        team.setStatus(EntityStatus.INACTIEF);

        teamRepository.startTransaction();
        try {
            teamRepository.update(team);
            teamRepository.commitTransaction();
        } catch (Exception ex) {
            teamRepository.rollbackTransaction();
            throw ex;
        }

        return team;
    }

    public List<Team> getTeamsVanWerknemer(int werknemerId) {
        return teamRepository.getTeamsVanWerknemer(werknemerId);
    }

    public List<Team> getTeamsVanVerantwoordelijke(int werknemerId) {
        return teamRepository.getTeamsVanVerantwoordelijke(werknemerId);
    }

    public List<Team> getTeamsVanSite(int siteId) {
        return teamRepository.getTeamsVanSite(siteId);
    }
}