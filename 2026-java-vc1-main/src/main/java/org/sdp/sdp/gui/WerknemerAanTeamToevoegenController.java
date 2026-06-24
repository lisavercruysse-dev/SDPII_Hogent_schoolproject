package org.sdp.sdp.gui;

import domein.WerknemerController;
import domein.MeldingenController;
import dto.WerknemerDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import util.MeldingType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class WerknemerAanTeamToevoegenController {
    private final MainController mainController;
    private final WerknemerController werknemerController;
    private final MeldingenController meldingenController;
    private final ObservableTeam team;
    private final ObservableWerknemersTable werknemersTable;
    private WerknemerListView observableList;
    private List<WerknemerDTO> selectedWerknemers;

    @FXML
    private Label werknemerError;
    @FXML private ListView<WerknemerDTO> werknemersListView;
    @FXML private TextField filterInput;

    public WerknemerAanTeamToevoegenController(MainController mainController, WerknemerController werknemerController, ObservableTeam team, ObservableWerknemersTable werknemersTable, MeldingenController meldingenController) {
        this.mainController = mainController;
        this.werknemerController = werknemerController;
        this.team = team;
        this.werknemersTable = werknemersTable;
        this.meldingenController = meldingenController;
        observableList = new WerknemerListView(werknemerController, team);
    }

    @FXML
    private void initialize() {
        werknemerError.setText("");
        selectedWerknemers = new ArrayList<>();

        SortedList<WerknemerDTO> sortedWerknemers = new SortedList<>(observableList.getFilteredWerknemers());
        sortedWerknemers.setComparator(Comparator.comparing(WerknemerDTO::achternaam));
        werknemersListView.setItems(sortedWerknemers);
        werknemersListView.setSelectionModel(null);
        werknemersListView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            private WerknemerDTO current;

            {
                checkBox.setOnAction(e -> {
                    if (current == null) return;
                    if (checkBox.isSelected()) {
                        if (!selectedWerknemers.contains(current)) selectedWerknemers.add(current);
                    } else {
                        selectedWerknemers.remove(current);
                    }
                });
            }

            @Override
            protected void updateItem(WerknemerDTO w, boolean empty) {
                super.updateItem(w, empty);
                if (empty || w == null) {
                    current = null;
                    setGraphic(null);
                } else {
                    current = w;
                    checkBox.setText(w.voornaam() + " " + w.achternaam());
                    checkBox.setSelected(selectedWerknemers.contains(w));
                    setGraphic(checkBox);
                }
            }
        });

        filterInput.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter();
        });
    }

    private void updateFilter() {
        observableList.changeFilter(filterInput.getText());
    }

    public void btnCloseAction(ActionEvent actionEvent) {
        mainController.closePopup();
    }

    public void btnConfirmAction(ActionEvent actionEvent) {
        if (selectedWerknemers.isEmpty()) {
            werknemerError.setText("Selecteer een werknemer.");
            return;
        }
        try {
            selectedWerknemers.forEach(werknemer -> {
                werknemersTable.addWerknemerToTeam(werknemer.id(), team.getId(), team);
                if (meldingenController != null) {
                    meldingenController.voegMeldingToeVoorGroep(
                            MeldingType.WERKNEMER_TOEGEVOEGD,
                            "Toegevoegd aan Team",
                            "Je bent succesvol toegevoegd aan het team: " + team.naamProperty().get(),
                            List.of(werknemer.id())
                    );
                }
            });
            mainController.updateMeldingenCounter();
            mainController.closePopup();
        } catch (Exception ex) {
            werknemerError.setText(ex.getMessage());
        }
    }
}