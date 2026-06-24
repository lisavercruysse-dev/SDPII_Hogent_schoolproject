package org.sdp.sdp.gui;

import domein.EntityStatus;
import domein.WerknemerController;
import dto.TeamDTO;
import dto.WerknemerInputDTO;
import dto.WerknemerDTO;
import exception.WerknemerInformationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ObservableWerknemersTable {

    private final WerknemerController controller;
    private final ObservableList<ObservableWerknemer> werknemerObservableList;
    @Getter
    private final FilteredList<ObservableWerknemer> filteredList;

    public ObservableWerknemersTable(WerknemerController controller, List<WerknemerDTO> werknemers) {
        this.controller = controller;
        this.werknemerObservableList = FXCollections.observableArrayList(
                werknemers.stream().map(ObservableWerknemer::new).toList()
        );
        this.filteredList = new FilteredList<>(werknemerObservableList, p -> true);
    }

    public void changeFilter(String achternaam, String voornaam, String email, Set<String> toegestaneStatussen, Set<String> toegestaneJobtitels) {
        filteredList.setPredicate(p -> {
            if (achternaam != null && !achternaam.isBlank()) {
                if (!p.lastNameProperty().get().toLowerCase().contains(achternaam.toLowerCase())) return false;
            }

            if (voornaam != null && !voornaam.isBlank()) {
                if (!p.firstNameProperty().get().toLowerCase().contains(voornaam.toLowerCase())) return false;
            }

            if (email != null && !email.isBlank()) {
                if (!p.emailProperty().get().toLowerCase().contains(email.toLowerCase())) return false;
            }

            if (toegestaneJobtitels != null && !toegestaneJobtitels.contains(p.jobTitelProperty().get().toUpperCase())) return false;
            if (toegestaneStatussen != null && !toegestaneStatussen.contains(p.statusProperty().get().toUpperCase())) return false;

            return true;
        });
    }

    private boolean matchesFilter(String value, String filter) {
        if (filter == null || filter.isBlank()) return true;
        return Objects.toString(value, "").toLowerCase().contains(filter.toLowerCase());
    }

    public ObservableWerknemer saveWerknemer(WerknemerInputDTO dto) throws WerknemerInformationException {
        WerknemerDTO w = controller.saveWerknemer(dto);

        if (dto.id() == null) {
            ObservableWerknemer ow = new ObservableWerknemer(w);
            werknemerObservableList.add(ow);
            return ow;
        } else {
            ObservableWerknemer existing = werknemerObservableList.stream().filter(ow -> ow.getId() == dto.id()).findFirst().orElse(null);
            if (existing != null) {
                existing.update(w);
            }
            return existing;
        }
    }

    public void deactiveerWerknemer(ObservableWerknemer observableWerknemer) {
        WerknemerDTO updated = controller.deactiveerWerknemer(observableWerknemer.getWerknemer());
        observableWerknemer.statusProperty().set(updated.status().toString().toLowerCase());
    }

    public void addWerknemerToTeam(int werknemerId, int teamId, ObservableTeam observableTeam) {
        WerknemerDTO dto = controller.voegWerknemerToeAanTeam(werknemerId, teamId);
        ObservableWerknemer ow = new ObservableWerknemer(dto);
        werknemerObservableList.add(ow);
        observableTeam.update();
    }

    public void addTeamToWerknemer(int werknemerId, int teamId) {
        controller.voegWerknemerToeAanTeam(werknemerId, teamId);
        werknemerObservableList.stream()
                .filter(ow -> ow.getId() == werknemerId)
                .findFirst()
                .ifPresent(existing -> {
                    int index = werknemerObservableList.indexOf(existing);
                    werknemerObservableList.set(index, existing);
                });
    }

    public void verwijderUitTeam(int werknemerId, int teamId, ObservableTeam observableTeam) {
        controller.verwijderWerknemerUitTeam(werknemerId, teamId);
        werknemerObservableList.removeIf(ow -> ow.getId() == werknemerId);
        observableTeam.update();
    }
}
