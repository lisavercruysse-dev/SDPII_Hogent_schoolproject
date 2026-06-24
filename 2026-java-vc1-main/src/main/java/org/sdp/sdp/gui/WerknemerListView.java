package org.sdp.sdp.gui;

import domein.WerknemerController;
import dto.WerknemerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WerknemerListView {

    private final WerknemerController controller;
    private final ObservableList<WerknemerDTO> werknemers;
    @Getter
    private final FilteredList<WerknemerDTO> filteredWerknemers;

    public WerknemerListView(WerknemerController controller, ObservableTeam team) {
        this.controller = controller;

        List<WerknemerDTO> werknemersAlInTeam = controller.getWerknemersFromTeam(team.getId());
        List<WerknemerDTO> werknemersVanSite = controller.getWerknemersVanSite(team.getTeam().siteId());
        List<WerknemerDTO> werknemersNietInTeam = controller.getWerknemersZonderTeam();

        List<WerknemerDTO> werknemers = new ArrayList<>(werknemersVanSite);
        werknemers.addAll(werknemersNietInTeam);
        werknemers.removeAll(werknemersAlInTeam);

        this.werknemers = FXCollections.observableArrayList(
                werknemers.stream()
                        .distinct()
                        .toList()
        );

        this.filteredWerknemers = new FilteredList<>(this.werknemers, w -> true);
    }

    public void changeFilter(String filter) {
        filteredWerknemers.setPredicate(werknemer -> {
            if (filter == null || filter.isEmpty()) {
                return true;
            }
            String fullName = (werknemer.voornaam() + " " + werknemer.achternaam()).toLowerCase();
            return fullName.contains(filter.toLowerCase());
        });
    }
}
