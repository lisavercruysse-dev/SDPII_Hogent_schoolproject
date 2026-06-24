package domein;

import de.mkammerer.argon2.Argon2Factory;
import dto.WerknemerDTO;
import dto.WerknemerInputDTO;
import exception.WerknemerInformationException;
import repository.GebruikerDao;

import java.util.Collections;
import java.util.List;

public class WerknemerManager {

    private GebruikerDao werknemerRepo;
    private List<Werknemer> werknemerList;

    public WerknemerManager(GebruikerDao werknemerDao) {
        werknemerRepo = werknemerDao;
    }

    public Werknemer login(String email, String wachtwoord) throws IllegalAccessException {
        Werknemer werknemer = werknemerRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Geen account gevonden voor dit e-mailadres."));

        if (!EntityStatus.ACTIEF.equals(werknemer.getStatus())) {
            throw new IllegalStateException("Account is niet actief.");
        }

        if (werknemer.getJobTitel() == JobTitel.WERKNEMER) {
            throw new IllegalAccessException("Geen toegang tot de applicatie.");
        }

        var argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        if (!argon2.verify(werknemer.getWachtwoord(), wachtwoord.toCharArray())) {
            throw new IllegalArgumentException("Ongeldig wachtwoord.");
        }

        return werknemer;
    }

    public Werknemer saveWerknemer(WerknemerInputDTO werknemerInputDTO) throws WerknemerInformationException {
        Werknemer existing = werknemerInputDTO.id() != null ? werknemerRepo.get(werknemerInputDTO.id()) : null;

        Werknemer w = Werknemer.builder()
                    .existing(existing)
                    .voornaam(werknemerInputDTO.voornaam())
                    .achternaam(werknemerInputDTO.achternaam())
                    .jobTitel(werknemerInputDTO.jobTitel())
                    .telefoon(werknemerInputDTO.telefoon())
                    .geboortedatum(werknemerInputDTO.geboortedatum())
                    .land(werknemerInputDTO.land())
                    .postcode(werknemerInputDTO.postcode())
                    .stad(werknemerInputDTO.stad())
                    .straat(werknemerInputDTO.straat())
                    .huisnummer(werknemerInputDTO.huisnummer())
                    .bus(werknemerInputDTO.bus()).build();

        werknemerRepo.startTransaction();
        try {
            if (werknemerInputDTO.id() == null) {
                werknemerRepo.insert(w);
            } else {
                werknemerRepo.update(w);
            }
            werknemerRepo.commitTransaction();
        } catch (Exception ex) {
            werknemerRepo.rollbackTransaction();
            throw ex;
        }
        return w;

    }

      public List<Werknemer> getWerknemerList() {
        List<Werknemer> result = werknemerRepo.findAll();
        return result != null ? result : Collections.emptyList();
    }

    public Werknemer getVerantwoordelijkeVanTeam(int teamId) {
        return werknemerRepo.getVerantwoordelijkeVoorTeam(teamId);
    }

    public List<Werknemer> getVerantwoordelijkenVanSite (int siteId) {
        return werknemerRepo.getVerantwoordelijkenVanSite(siteId);
    }

    public void wijzigWerknemer(Werknemer werknemer, String nieuweNaam, String nieuweJobtitel) {
        werknemerRepo.startTransaction();
        try {
            werknemer.setVoornaam(nieuweNaam);
            werknemer.setJobTitel(JobTitel.valueOf(nieuweJobtitel.toUpperCase()));
            werknemerRepo.update(werknemer);
            werknemerRepo.commitTransaction();
        } catch (Exception ex) {
            werknemerRepo.rollbackTransaction();
            throw ex;
        }
    }

    public Werknemer deactiveerWerknemer(WerknemerDTO dto) {
        Werknemer werknemer = werknemerRepo.get(dto.id());

        if (werknemer == null) {
            throw new IllegalArgumentException("Er is geen werknemer met dit id");
        }

        werknemer.setStatus(EntityStatus.INACTIEF);

        werknemerRepo.startTransaction();
        try {
            werknemerRepo.update(werknemer);
            werknemerRepo.commitTransaction();
        } catch (Exception ex) {
            werknemerRepo.rollbackTransaction();
            throw ex;
        }

        return werknemer;
    }

    public List<Werknemer> getVerantwoordelijken() {
        return werknemerRepo.getVerantwoordelijken();
    }

    public List<Werknemer> getWerknemersFromTeam(int id) {
        return werknemerRepo.getWerknemersFromTeam(id);
    }

    public void closePersistency() {
        werknemerRepo.closePersistency();
    }

    public Werknemer voegWerknemerToeAanTeam(int werknemerId, int teamId) {
        boolean zitAlInTeam = werknemerRepo.get(werknemerId)
                .getTeams()
                .stream()
                .anyMatch(t -> t.getId() == teamId);

        if (zitAlInTeam) {
            throw new IllegalArgumentException("Deze werknemer zit al in dit team");
        }
        werknemerRepo.startTransaction();
        Werknemer w;
        try {
            w = werknemerRepo.voegWerknemerToeAanTeam(werknemerId, teamId);
            werknemerRepo.commitTransaction();
        } catch (Exception ex) {
            werknemerRepo.rollbackTransaction();
            throw ex;
        }
        return w;
    }

    public List<Werknemer> getGewoneWerknemers() {
        return werknemerRepo.getGewoneWerknemers();
    }

    public Team verwijderWerknemerUitTeam(int werknemerId, int teamId) {
        Team t;
        werknemerRepo.startTransaction();
        try {
            Werknemer w = werknemerRepo.get(werknemerId);
            t = w.getTeams().stream()
                    .filter(team -> team.getId() == teamId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Werknemer zit niet in dit team"));

            w.getTeams().remove(t);
            t.getWerknemers().remove(w);

            werknemerRepo.commitTransaction();
        } catch (Exception ex) {
            werknemerRepo.rollbackTransaction();
            throw ex;
        }
        return t;
    }

    public List<Werknemer> getManagersEnVerantwoordelijken() {
        return getWerknemerList().stream()
                .filter(w -> w.getJobTitel() == JobTitel.MANAGER || w.getJobTitel() == JobTitel.VERANTWOORDELIJKE)
                .toList();
    }

    public List<Werknemer> getVerantwoordelijkenZonderSite() {
        return werknemerRepo.getVerantwoordelijkenZonderSite();
    }

    public List<Werknemer> getWerknemersVanSite(int siteId) {
        return werknemerRepo.getWerknemersVanSite(siteId);
    }

    public List<Werknemer> getWerknemersZonderTeam() {
        return werknemerRepo.getWerknemersZonderTeam();
    }
}
