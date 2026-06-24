package repository;

import domein.Team;
import domein.Werknemer;

import java.util.List;
import java.util.Optional;

public interface GebruikerDao extends GenericDao<Werknemer>{
    Optional<Werknemer> findByEmail(String email);
    public List<Werknemer> getVerantwoordelijken();
    public List<Werknemer> getWerknemersFromTeam(int id);
    public Werknemer voegWerknemerToeAanTeam(int werknemerId, int teamId);
    public List<Werknemer> getGewoneWerknemers();
    public Werknemer getVerantwoordelijkeVoorTeam(int teamId);
    public List<Werknemer> getVerantwoordelijkenVanSite(int siteId);
    public List<Werknemer> getVerantwoordelijkenZonderSite();
    public List<Werknemer> getWerknemersVanSite(int siteId);
    public List<Werknemer> getWerknemersZonderTeam();
}
