package org.sdp.sdp.gui;

import domein.SiteController;
import domein.TeamController;
import domein.WerknemerController;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.TeamInputDTO;
import dto.WerknemerDTO;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.*;

public class TeamToevoegenController extends VBox {

    private final MainController mainController;
    private final ObservableWerknemersTable werknemersTable;
    private final ObservableWerknemer werknemer;
    private final TeamController teamController;
    private final SiteController siteController;

    @FXML public Label teamError, siteLbl, siteError;
    @FXML public ComboBox<TeamDTO> teamCombo;
    @FXML public ComboBox<SiteDTO> siteCombo;


    public TeamToevoegenController(MainController mainController, TeamController teamController, SiteController siteController, ObservableWerknemersTable observableWerknemersTable, ObservableWerknemer werknemer) {
        this.mainController = mainController;
        this.werknemersTable = observableWerknemersTable;
        this.werknemer = werknemer;
        this.teamController = teamController;
        this.siteController = siteController;
    }

    @FXML
    private void initialize() {
        teamError.setText("");

        List<TeamDTO> teamsVanWerknemer = teamController.getTeamsVanWerknemer(werknemer.getId());
        List<TeamDTO> teamsAlsVerantwoordelijke = teamController.getTeamsVanVerantwoordelijke(werknemer.getId());

        List<TeamDTO> teams = new ArrayList<>();
        teams.addAll(teamsVanWerknemer);
        teams.addAll(teamsAlsVerantwoordelijke);

        siteCombo.getItems().addAll(siteController.getAllSites());
        siteCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(SiteDTO site) { return site == null ? "" : site.name(); }
            @Override
            public SiteDTO fromString(String string) { return null; }
        });

        siteCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            teamCombo.getItems().clear();
            teamCombo.getSelectionModel().clearSelection();
            if (newValue != null) {
                Set<Integer> alInTeam = teams.stream()
                        .map(TeamDTO::id)
                        .collect(Collectors.toSet());

                teamCombo.getItems().addAll(
                        teamController.getTeamsVanSite(newValue.id()).stream()
                                .filter(t -> !alInTeam.contains(t.id()))
                                .toList()
                );
                teamCombo.getSelectionModel().selectFirst();
            }
        });

        if (!teams.isEmpty()) {
            siteCombo.setDisable(true);
            siteCombo.getItems().stream()
                    .filter(s -> s.id() == teams.getFirst().siteId())
                    .findFirst()
                    .ifPresent(siteCombo.getSelectionModel()::select);
        } else {
            siteCombo.getSelectionModel().selectFirst();
        }

        teamCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TeamDTO team) { return team == null ? "Geen Teams" : team.naam(); }
            @Override
            public TeamDTO fromString(String string) { return null; }
        });

        teamCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(TeamDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText("Geen teams");
                } else {
                    setText(item.naam());
                }
            }
        });

    }

    public void btnCloseAction(ActionEvent actionEvent) {
        mainController.closePopup();
    }

    public void btnConfirmAction(ActionEvent actionEvent) {
        TeamDTO selected = teamCombo.getValue();

        if (selected == null) {
            teamError.setText("Selecteer een team.");
            return;
        }

        try {
            werknemersTable.addTeamToWerknemer(werknemer.getId(), selected.id());
            mainController.closePopup();
        } catch (Exception e) {
            teamError.setText(e.getMessage());
        }
    }
}
