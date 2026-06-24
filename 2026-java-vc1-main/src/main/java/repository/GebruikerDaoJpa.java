package repository;

import domein.JobTitel;
import domein.Team;
import domein.Werknemer;
import jakarta.persistence.TypedQuery;

import java.util.List;
import java.util.Optional;

public class GebruikerDaoJpa extends GenericDaoJpa<Werknemer> implements GebruikerDao{

    public GebruikerDaoJpa() {super(Werknemer.class);}

    public Optional<Werknemer> findByEmail(String email) {
        TypedQuery<Werknemer> query = em.createQuery(
                "SELECT w FROM Werknemer w WHERE w.email = :email", Werknemer.class);
        query.setParameter("email", email);
        List<Werknemer> result = query.getResultList();
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }

    @Override
    public List<Werknemer> getVerantwoordelijken() {
        return em.createQuery(
                        "SELECT w FROM Werknemer w WHERE UPPER(w.jobTitel) = UPPER(:jobTitel)",
                        Werknemer.class
                )
                .setParameter("jobTitel", "verantwoordelijke")
                .getResultList();
    }

    @Override
    public List<Werknemer> getWerknemersFromTeam(int id) {
        return em.createQuery(
                "SELECT w FROM Werknemer w JOIN w.teams t WHERE t.id = :teamId", Werknemer.class
        ).setParameter("teamId", id).getResultList();
    }

    @Override
    public Werknemer voegWerknemerToeAanTeam(int werknemerId, int teamId) {
        Werknemer w = em.find(Werknemer.class, werknemerId);
        Team t = em.find(Team.class, teamId);

        if (!w.getTeams().contains(t)) {
            w.getTeams().add(t);
            t.getWerknemers().add(w);
        }
        return w;
    }

    @Override
    public List<Werknemer> getGewoneWerknemers() {
        return em.createQuery(
                "SELECT w FROM Werknemer w WHERE UPPER(w.jobTitel) = UPPER(:jobTitel) ",
                Werknemer.class
        ).setParameter("jobTitel", "werknemer").getResultList();
    }

    @Override
    public Werknemer getVerantwoordelijkeVoorTeam(int teamId) {
        Team team = em.find(Team.class, teamId);
        return team != null ? team.getVerantwoordelijke() : null;
    }

    @Override
    public List<Werknemer> getVerantwoordelijkenVanSite(int siteId) {
        return em.createQuery(
                "SELECT w FROM Werknemer w WHERE w.jobTitel = :jobtitel AND EXISTS (SELECT t FROM Team t WHERE t.verantwoordelijke = w AND t.site.id = :siteId)",
                Werknemer.class
        ).setParameter("jobtitel", JobTitel.VERANTWOORDELIJKE)
                .setParameter("siteId", siteId).getResultList();
    }

    @Override
    public List<Werknemer> getVerantwoordelijkenZonderSite() {
        return em.createQuery(
                        "SELECT w FROM Werknemer w " +
                                "WHERE w.jobTitel = :jobtitel " +
                                "AND NOT EXISTS (SELECT t FROM Team t WHERE t.verantwoordelijke = w)",
                        Werknemer.class)
                .setParameter("jobtitel", JobTitel.VERANTWOORDELIJKE)
                .getResultList();
    }

    @Override
    public List<Werknemer> getWerknemersVanSite(int siteId) {
        return em.createQuery(
            "SELECT w FROM Werknemer w JOIN w.teams t WHERE t.site.id = :siteId AND w.jobTitel = :jobTitel" , Werknemer.class
        ).setParameter("siteId", siteId)
                .setParameter("jobTitel", JobTitel.WERKNEMER)
                .getResultList();
    }

    @Override
    public List<Werknemer> getWerknemersZonderTeam() {
        return em.createQuery(
                        "SELECT w FROM Werknemer w WHERE w.teams IS EMPTY AND w.jobTitel = :jobTitel", Werknemer.class
                ).setParameter("jobTitel", JobTitel.WERKNEMER)
                .getResultList();
    }

}
