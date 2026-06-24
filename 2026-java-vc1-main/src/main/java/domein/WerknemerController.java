package domein;

import dto.TeamDTO;
import dto.WerknemerInputDTO;
import dto.WerknemerDTO;
import exception.WerknemerInformationException;
import repository.GebruikerDaoJpa;
import repository.TeamDaoJpa;

import java.util.List;

public class WerknemerController {

    private final WerknemerManager werknemerManager;

    public WerknemerController() {
        this.werknemerManager = new WerknemerManager(new GebruikerDaoJpa());
    }

    public WerknemerDTO toDTO(Werknemer w) {
        return new WerknemerDTO(w.getId(), w.getVoornaam(), w.getAchternaam(), w.getJobTitel().name(),
                w.getTelefoon(), w.getGeboortedatum(), w.getLand(), w.getPostcode(), w.getStad(),
                w.getStraat(), w.getHuisnummer(), w.getBus(), w.getEmail(), w.getStatus());
    }

    public WerknemerDTO login(String email, String wachtwoord) throws IllegalAccessException{
        Werknemer w = werknemerManager.login(email, wachtwoord);
        return toDTO(w);
    }

    public WerknemerDTO saveWerknemer(WerknemerInputDTO werknemerInputDTO) throws WerknemerInformationException {
        Werknemer w = werknemerManager.saveWerknemer(werknemerInputDTO);
        return toDTO(w);
     }
  
    public List<WerknemerDTO> getWerknemers() {
        List<Werknemer> werknemers = werknemerManager.getWerknemerList();
        return werknemers.stream().map(this::toDTO).toList();
    }

    public WerknemerDTO getVerantwoordelijkeVanTeam(int teamId) {
        Werknemer w = werknemerManager.getVerantwoordelijkeVanTeam(teamId);
        return toDTO(w);
    }

    public List<WerknemerDTO> getVerantwoordelijkenVanSite(int siteId) {
        List<Werknemer> werknemers = werknemerManager.getVerantwoordelijkenVanSite(siteId);
        return werknemers.stream().map(this::toDTO).toList();
    }

    public List<WerknemerDTO> getVerantwoordelijkenZonderSite() {
        List<Werknemer> werknemers = werknemerManager.getVerantwoordelijkenZonderSite();
        return werknemers.stream().map(this::toDTO).toList();
    }

    public List<WerknemerDTO> getWerknemersVanSite(int siteId) {
        List<Werknemer> werknemers = werknemerManager.getWerknemersVanSite(siteId);
        return werknemers.stream().map(this::toDTO).toList();
    }

    public List<WerknemerDTO> getWerknemersZonderTeam() {
        List<Werknemer> werknemers = werknemerManager.getWerknemersZonderTeam();
        return werknemers.stream().map(this::toDTO).toList();
    }

    public List<WerknemerDTO> getGewoneWerknemers() {
        List<Werknemer> werknemers = werknemerManager.getGewoneWerknemers();
        return werknemers.stream().map(this::toDTO).toList();
    }

    public List<WerknemerDTO> getWerknemersFromTeam(int id) {
        List<Werknemer> werknemers = werknemerManager.getWerknemersFromTeam(id);
        return werknemers.stream().map(this::toDTO).toList();
    }

    public void wijzigWerknemer(Werknemer werknemer, String nieuweNaam, String nieuweJobTitel) {
        werknemerManager.wijzigWerknemer(werknemer, nieuweNaam, nieuweJobTitel);
    }

    public WerknemerDTO deactiveerWerknemer(WerknemerDTO dto) {
        Werknemer werknemer = werknemerManager.deactiveerWerknemer(dto);
        return toDTO(werknemer);
    }

    public List<WerknemerDTO> getVerantwoordelijken() {
        return werknemerManager.getVerantwoordelijken().stream().map(this::toDTO).toList();
    }

    public WerknemerDTO voegWerknemerToeAanTeam(int werknemerId, int teamId) {
        Werknemer w = werknemerManager.voegWerknemerToeAanTeam(werknemerId, teamId);
        return toDTO(w);
    }

    public List<Integer> getManagerEnVerantwoordelijkeIds() {
        return werknemerManager.getManagersEnVerantwoordelijken().stream()
                .map(Werknemer::getId)
                .toList();
    }

    public TeamDTO verwijderWerknemerUitTeam(int werknemerId, int teamId) {
        Team team = werknemerManager.verwijderWerknemerUitTeam(werknemerId, teamId);
        return new TeamDTO(team.getId(), team.getNaam(), team.getSite().getId(), team.getStatus());
    }

}
