package org.sdp.sdp.gui;

import domein.SiteController;
import domein.TeamController;
import domein.WerknemerController;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.TeamInputDTO;
import dto.WerknemerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ObservableTeamsTable {
    private final TeamController teamController;
    private final WerknemerController werknemerController;
    private final SiteController siteController;
    private final ObservableList<ObservableTeam> observableTeamList;
    @Getter
    private final FilteredList<ObservableTeam> filteredTeams;

    public ObservableTeamsTable(TeamController teamController, WerknemerController werknemerController, SiteController siteController, List<TeamDTO> teams) {
        this.teamController = teamController;
        this.werknemerController = werknemerController;
        this.siteController = siteController;
        this.observableTeamList = FXCollections.observableArrayList();

        if (teams != null) {
            teams.forEach(t -> observableTeamList.add(new ObservableTeam(t, werknemerController, siteController)));
        }
        this.filteredTeams = new FilteredList<>(observableTeamList, t -> true);
    }

    public void changeFilter(String naam, String verantwoordelijke, String site, Integer min, Integer max, Set<String> toegestaneStatussen) {
        filteredTeams.setPredicate(t ->{
                if (naam != null && !naam.isBlank()) {
                    if (!t.naamProperty().get().toLowerCase()
                            .contains(naam.toLowerCase())) return false;
                }

                if (verantwoordelijke != null && !verantwoordelijke.isBlank()) {
                    if (!t.verantwoordelijkeProperty().get().toLowerCase()
                            .contains(verantwoordelijke.toLowerCase())) return false;
                }

                if (site != null && !site.isBlank()) {
                    if (!t.siteProperty().get().toLowerCase()
                            .contains(site.toLowerCase())) return false;
                }

                int werknemerCount = t.aantalWerknemersProperty().get();
                if (min != null && werknemerCount < min) return false;
                if (max != null && werknemerCount > max) return false;

                if (toegestaneStatussen != null &&
                    !toegestaneStatussen.contains(t.statusProperty().get().toUpperCase())
                ) {
                    return false;
                }
                return true;
            }
        );
    }

    private int parseIntOrZero(String s) {
        try { return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private boolean matchesFilter(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return Objects.toString(value, "").toLowerCase().contains(filter.toLowerCase());
    }

    public ObservableTeam saveTeam(TeamInputDTO dto) {
        TeamDTO team = teamController.saveTeam(dto);

        if (dto.id() == null) {
            ObservableTeam ot = new ObservableTeam(team, werknemerController, siteController);
            observableTeamList.add(ot);
            return ot;
        }
        else {
            ObservableTeam existingTeam = observableTeamList.stream().filter(ot -> ot.getId() == dto.id()).findFirst().orElse(null);
            if (existingTeam != null) {
                existingTeam.update(team);
            }
            return existingTeam;
        }
    }

    public void removeTeam(ObservableTeam observableTeam) {
        TeamDTO updated = teamController.deleteTeam(observableTeam.getTeam());
        observableTeam.statusProperty().set(updated.status().toString().toLowerCase());
    }
}