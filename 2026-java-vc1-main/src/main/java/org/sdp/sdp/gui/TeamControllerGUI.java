package org.sdp.sdp.gui;

import domein.*;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.TeamInputDTO;
import dto.WerknemerDTO;
import exception.TeamInformationException;
import exception.WerknemerInformationException;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import lombok.Setter;
import util.MeldingType;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TeamControllerGUI extends VBox implements FilterableTable{
    private final MainController mainController;
    private ObservableTeamsTable observableTeamsTable;
    private final SiteController siteController;
    private final WerknemerController werknemerController;
    private final TeamController teamController;
    private final MeldingenController meldingenController;
    private ObservableWerknemersTable observableWerknemersTable;
    private ObservableTeam teamToEdit = null;
    private TextField txtMinAantalWerknemers;
    private TextField txtMaxAantalWerknemers;
    @FXML private TableColumn<ObservableTeam, String> naamCol;
    @FXML private TableColumn<ObservableTeam, String> verantwoordelijkeCol, statusCol;
    @FXML private TableColumn<ObservableTeam, Integer> aantalLedenCol;
    @FXML private TableColumn<ObservableTeam, String> siteCol;
    @FXML private TableView<ObservableTeam> teamsTbl;
    @FXML private TextField naamInput;
    @FXML private Label  title, naamError, verantwoordelijkeError, siteError, naamDetail, verantwoordelijkeDetail, aantalLedenDetail;
    @FXML private TableView<ObservableWerknemer> werknemersTable;
    @FXML private TableColumn<ObservableWerknemer, String> achternaamCol, voornaamCol, emailCol;
    @FXML private TableColumn<ObservableWerknemer, Void> btnCol;
    @FXML private TableColumn<ObservableTeam, Void> teamsBtnCol;
    @FXML private ComboBox<SiteDTO> siteInput;
    @FXML private ComboBox<WerknemerDTO> verantwoordelijkeInput;
    @FXML private VBox placeholderContainer, detailContainer, teamsForm;
    @FXML private Button annuleerEditBtn, toevoegenBtn;

    private final Map<String, TextField> columnFilterFields = new LinkedHashMap<>();
    private final Map<String, CheckBox> statusCheckboxes   = new LinkedHashMap<>();

    public TeamControllerGUI(MainController mainController, TeamController teamController, WerknemerController werknemerController, SiteController siteController, MeldingenController meldingenController) {
        this.mainController = mainController;
        this.werknemerController = werknemerController;
        this.siteController = siteController;
        this.teamController = teamController;
        this.meldingenController = meldingenController;

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/teams.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        initialize();
        initializeTeamWerknemersTable();
    }

    private void initialize() {
        Image trashImage = new Image(getClass().getResource("/images/16201366061606130516-128.png").toString());
        Image editImage = new Image(getClass().getResource("/images/edit.png").toString());

        annuleerEditBtn.setVisible(false);
        annuleerEditBtn.setManaged(false);

        columnFilterFields.put("naam", setupTextFilter(naamCol, "naam"));
        columnFilterFields.put("verantwoordelijke", setupTextFilter(verantwoordelijkeCol, "verantwoordelijke"));
        columnFilterFields.put("site", setupTextFilter(siteCol, "site"));
        List<TextField> range = setupRangeFilter(aantalLedenCol, "aantalLeden");
        txtMinAantalWerknemers = range.get(0);
        txtMaxAantalWerknemers = range.get(1);
        setupCheckboxFilter(
                statusCol, "status",
                Arrays.stream(OperationeleStatus.values()).map(Enum::name).toList(),
                statusCheckboxes);

        Label placeholder = new Label("Geen teams gevonden");
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-size: 13;");
        teamsTbl.setPlaceholder(placeholder);

        naamCol.setCellValueFactory(cellData -> cellData.getValue().naamProperty());
        verantwoordelijkeCol.setCellValueFactory(cellData -> cellData.getValue().verantwoordelijkeProperty());
        siteCol.setCellValueFactory(cellData -> cellData.getValue().siteProperty());
        aantalLedenCol.setCellValueFactory(cellData -> cellData.getValue().aantalWerknemersProperty().asObject());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.substring(0, 1).toUpperCase() + item.substring(1).toLowerCase());
                    switch (item.toUpperCase()) {
                        case "ACTIEF"   -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        case "INACTIEF" -> setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        default         -> setStyle("");
                    }
                }
            }
        });

        teamsBtnCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteTeamBtn = new Button();
            private final Button editTeamBtn = new Button();
            private final HBox buttons = new HBox(8, editTeamBtn, deleteTeamBtn);
            {
                ImageView deleteIcon = new ImageView(trashImage);
                deleteIcon.setFitWidth(20);
                deleteIcon.setPreserveRatio(true);
                deleteIcon.setSmooth(false);
                deleteTeamBtn.setGraphic(deleteIcon);
                deleteTeamBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                deleteTeamBtn.setOnAction(e -> {
                    ObservableTeam team = getTableView().getItems().get(getIndex());
                    onDeleteTeamAction(team);
                });

                ImageView editIcon = new ImageView(editImage);
                editIcon.setFitWidth(20);
                editIcon.setPreserveRatio(true);
                editIcon.setSmooth(false);
                editTeamBtn.setGraphic(editIcon);
                editTeamBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                editTeamBtn.setOnAction(e -> {
                    ObservableTeam team = getTableView().getItems().get(getIndex());
                    onEditTeamAction(team);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        if (mainController.getIngelogdeGebruiker().jobTitel().equals(JobTitel.VERANTWOORDELIJKE.name())) {
            List<TeamDTO> verantwoordelijkeTeams = teamController.getTeamsVanVerantwoordelijke(mainController.getIngelogdeGebruiker().id());
            this.observableTeamsTable = new ObservableTeamsTable(teamController, werknemerController, siteController, verantwoordelijkeTeams);

            teamsForm.setVisible(false);
            teamsForm.setManaged(false);
            teamsBtnCol.setVisible(false);
            teamsBtnCol.setResizable(false);
        } else this.observableTeamsTable = new ObservableTeamsTable(teamController, werknemerController, siteController, teamController.getAllTeams());

        SortedList<ObservableTeam> sortedList = new SortedList<>(observableTeamsTable.getFilteredTeams());
        sortedList.comparatorProperty().bind(teamsTbl.comparatorProperty());
        teamsTbl.setItems(sortedList);

        naamCol.setSortType(TableColumn.SortType.ASCENDING);
        teamsTbl.getSortOrder().add(naamCol);

        teamsTbl.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        showDetail(newValue);
                        int index = teamsTbl.getSelectionModel().getSelectedIndex();
                        System.out.printf("%d %s", index, newValue.naamProperty());
                    } else {
                        showPlaceHolder();
                    }
                });

        List<SiteDTO> sites = siteController.getAllSites();

        siteInput.getItems().addAll(sites);
        siteInput.setConverter(new StringConverter<SiteDTO>() {
            @Override
            public String toString(SiteDTO s) {
                return s == null ? "" : s.name();
            }

            @Override
            public SiteDTO fromString(String s) {
                return null;
            }
        });

        siteInput.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            verantwoordelijkeInput.getItems().clear();
            if (newValue != null) {
                List<WerknemerDTO> filteredVerantwoordelijken = new ArrayList<>();
                filteredVerantwoordelijken.addAll(werknemerController.getVerantwoordelijkenVanSite(newValue.id()));
                filteredVerantwoordelijken.addAll(werknemerController.getVerantwoordelijkenZonderSite());
                verantwoordelijkeInput.getItems().addAll(filteredVerantwoordelijken);
            }
            verantwoordelijkeInput.getSelectionModel().selectFirst();
        });

        verantwoordelijkeInput.setConverter(new StringConverter<WerknemerDTO>() {
            @Override
            public String toString(WerknemerDTO w) {
                return w == null ? "" : w.voornaam() + " " + w.achternaam();
            }

            @Override
            public WerknemerDTO fromString(String s) {
                return null;
            }
        });
        siteInput.getSelectionModel().selectFirst();
        showPlaceHolder();
    }

    @FXML
    private void resetFilters() {
        columnFilterFields.values().forEach(TextInputControl::clear);
        txtMaxAantalWerknemers.clear();
        txtMinAantalWerknemers.clear();
        statusCheckboxes.values().forEach(cb -> cb.setSelected(true));
    }

    @Override
    public void updateFilter() {
        String naam             = columnFilterFields.getOrDefault("naam", new TextField()).getText();
        String verantwoordelijke = columnFilterFields.getOrDefault("verantwoordelijke", new TextField()).getText();
        String site             = columnFilterFields.getOrDefault("site", new TextField()).getText();

        Integer min = parseIntOrNull(txtMinAantalWerknemers.getText());
        Integer max = parseIntOrNull(txtMaxAantalWerknemers.getText());

        Set<String> toegestaneStatussen = statusCheckboxes.values().stream().noneMatch(CheckBox::isSelected) ?
                Arrays.stream(EntityStatus.values()).map(Enum::name).collect(Collectors.toSet()) :
                statusCheckboxes.entrySet().stream().filter(e -> e.getValue().isSelected())
                        .map(Map.Entry::getKey).collect(Collectors.toSet());

        observableTeamsTable.changeFilter(naam, verantwoordelijke, site, min, max, toegestaneStatussen);
    }

    @Override
    public TableView<?> getTable() {
        return teamsTbl;
    }

    private Integer parseIntOrNull(String tekst) {
        try { return (tekst == null || tekst.isBlank()) ? null : Integer.parseInt(tekst.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    public void toevoegenAction(ActionEvent actionEvent) {
        naamError.setText("");

        String naam =  naamInput.getText();
        SiteDTO site = siteInput.getSelectionModel().getSelectedItem();
        WerknemerDTO verantwoordelijke = verantwoordelijkeInput.getSelectionModel().getSelectedItem();

        try {
            if (teamToEdit != null) {
                observableTeamsTable.saveTeam(new TeamInputDTO(naam, site, verantwoordelijke, teamToEdit.getId()));
                setToevoegen();
            } else {
                observableTeamsTable.saveTeam(new TeamInputDTO(naam, site, verantwoordelijke, null));
                if (meldingenController != null && verantwoordelijke != null) {
                    meldingenController.voegMeldingToeVoorGroep(
                            MeldingType.TEAM_AANGEMAAKT,
                            "Nieuw Team Toegewezen",
                            "Je bent succesvol toegewezen als verantwoordelijke van het team: " + naam,
                            List.of(verantwoordelijke.id())
                    );
                    mainController.updateMeldingenCounter();
                }
            }
        } catch (IllegalArgumentException e) {
            naamError.setText(e.getMessage());
        } catch (TeamInformationException ex) {
            handleErrors(ex);
        }
    }

    private void initializeTeamWerknemersTable() {
        voornaamCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        achternaamCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        voornaamCol.setSortable(false);
        achternaamCol.setSortable(false);
        achternaamCol.setStyle("-fx-border-color: transparent;");
        emailCol.setSortable(false);
        werknemersTable.setSelectionModel(null);

        Image trashImage = new Image(getClass().getResource("/images/16201366061606130516-128.png").toString());

        btnCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button();
            {
                ImageView icon = new ImageView(trashImage);
                icon.setFitWidth(20);
                icon.setPreserveRatio(true);
                icon.setSmooth(false);
                deleteBtn.setGraphic(icon);
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                deleteBtn.setOnAction(e -> {
                    ObservableWerknemer werknemer = getTableView().getItems().get(getIndex());
                    onVerwijderWerknemerVanTeamAction(werknemer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteBtn);
                }
            }
        });
    }

    private void handleErrors (TeamInformationException ex) {
        ex.getErrors().forEach((el, message) -> {
            switch (el) {
                case NAAM -> naamError.setText(message);
                case VERANTWOORDELIJKE -> verantwoordelijkeError.setText(message);
                case SITE -> siteError.setText(message);
            }
        });
    }

    private void showDetail(ObservableTeam team){
        naamDetail.setText(team.naamProperty().getValue());
        verantwoordelijkeDetail.setText(team.verantwoordelijkeProperty().getValue());
        aantalLedenDetail.textProperty().unbind();
        aantalLedenDetail.textProperty().bind(team.aantalWerknemersProperty().asString());

        observableWerknemersTable = new ObservableWerknemersTable(
                werknemerController,
                werknemerController.getWerknemersFromTeam(team.getId())
        );
        werknemersTable.setItems(observableWerknemersTable.getFilteredList());

        detailContainer.setVisible(true);
        detailContainer.setManaged(true);
        placeholderContainer.setVisible(false);
        placeholderContainer.setManaged(false);
    }

    private void showPlaceHolder(){
        placeholderContainer.setVisible(true);
        placeholderContainer.setManaged(true);
        detailContainer.setVisible(false);
        detailContainer.setManaged(false);
    }

    public void onWerknemerToevoegenAction(ActionEvent actionEvent) {
        ObservableTeam selected = teamsTbl.getSelectionModel().getSelectedItem();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/WerknemerToevoegen.fxml"));
            WerknemerAanTeamToevoegenController controller = new WerknemerAanTeamToevoegenController(mainController,
                    werknemerController,
                    selected, observableWerknemersTable, meldingenController);
            loader.setController(controller);

            Node popup = loader.load();
            mainController.showPopup(popup);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onDeleteTeamAction(ObservableTeam team) {
        mainController.showPopup(new VerwijderController(mainController,
                "Verwijder team",
                "Weet je zeker dat je dit team wilt verwijderen? Deze actie kan niet ongedaan gemaakt worden.",
                () -> observableTeamsTable.removeTeam(team)));
    }

    private void onVerwijderWerknemerVanTeamAction(ObservableWerknemer werknemer) {
        ObservableTeam selectedTeam = teamsTbl.getSelectionModel().getSelectedItem();
        mainController.showPopup(new VerwijderController(
                mainController,
                "Verwijder uit team",
                "Weet je zeker dat je deze werknemer uit het team wilt verwijderen? Je kan deze altijd opnieuw aan het team toevoegen.",
                () -> {
                    observableWerknemersTable.verwijderUitTeam(werknemer.getId(), selectedTeam.getId(), selectedTeam);
                }
        ));
    }

    private void onEditTeamAction(ObservableTeam team) {
        setEdit(team);
    }

    public void onCancelEdit(ActionEvent actionEvent) {
        setToevoegen();
    }

    private void setEdit(ObservableTeam team) {
        naamError.setText("");
        teamToEdit = team;
        title.setText("Team bewerken");
        annuleerEditBtn.setVisible(true);
        annuleerEditBtn.setManaged(true);
        naamInput.setText(team.naamProperty().getValue());
        toevoegenBtn.setText("Bewerken");

        siteInput.getItems().stream()
                .filter(s -> s.name().equals(team.siteProperty().getValue()))
                .findFirst()
                .ifPresent(siteInput.getSelectionModel()::select);
        siteInput.setDisable(true);

        verantwoordelijkeInput.getItems().stream()
                .filter(w -> (w.voornaam() + " " + w.achternaam()).equals(team.verantwoordelijkeProperty().getValue()))
                .findFirst()
                .ifPresent(verantwoordelijkeInput.getSelectionModel()::select);
    }

    private void setToevoegen() {
        naamError.setText("");
        teamToEdit = null;
        annuleerEditBtn.setVisible(false);
        annuleerEditBtn.setManaged(false);

        title.setText("Team toevoegen");
        naamInput.setText("");
        siteInput.setDisable(false);
        toevoegenBtn.setText("Toevoegen");
    }

    private Label positionArrow(TableColumn<?, ?> col) {
        Label sortArrow = new Label("");

        col.sortTypeProperty().addListener((obs, old, nw) -> {
            if (teamsTbl.getSortOrder().contains(col)) {
                sortArrow.setText(nw == TableColumn.SortType.ASCENDING ? " ▲" : " ▼");
            } else {
                sortArrow.setText("");
            }
        });

        teamsTbl.getSortOrder().addListener((javafx.collections.ListChangeListener<TableColumn<ObservableTeam, ?>>) change -> {
            if (teamsTbl.getSortOrder().contains(col)) {
                sortArrow.setText(col.getSortType() == TableColumn.SortType.ASCENDING ? " ▲" : " ▼");
            } else {
                sortArrow.setText("");
            }
        });
        return sortArrow;
    }

    private Label boldLabel(String tekst) {
        Label lbl = new Label(tekst);
        lbl.setStyle("-fx-font-weight: bold;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER_LEFT);
        return lbl;
    }
}