package org.sdp.sdp.gui;

import domein.*;
import dto.TeamDTO;
import dto.WerknemerInputDTO;
import exception.WerknemerInformationException;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.Setter;

import java.io.IOException;
import java.util.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class GebruikersController extends ScrollPane implements FilterableTable{
    @FXML private TableView<ObservableWerknemer> tblWerknemers;
    @FXML private TableColumn<ObservableWerknemer, String> firstNameCol;
    @FXML private TableColumn<ObservableWerknemer, String> lastNameCol, statusCol;
    @FXML private TableColumn<ObservableWerknemer, String> jobTitelCol;
    @FXML private TableColumn<ObservableWerknemer, String> emailCol;
    @FXML private TextField voornaamInput;
    @FXML private Label voornaamError, title;
    @FXML private TextField achternaamInput;
    @FXML private Label achternaamError;
    @FXML private TextField telefoonInput;
    @FXML private Label telefoonError;
    @FXML private DatePicker geboorteInput;
    @FXML private Label geboorteError;
    @FXML private ComboBox<String> jobtitelInput;
    @FXML private Label jobtitelError;
    @FXML private TextField landInput;
    @FXML private Label landError;
    @FXML private TextField postcodeInput;
    @FXML private Label postcodeError;
    @FXML private TextField stadInput;
    @FXML private Label stadError;
    @FXML private TextField straatInput;
    @FXML private Label straatError;
    @FXML private TextField huisnrInput;
    @FXML private Label huisnrError;
    @FXML private TextField busInput;
    @FXML private Label busError;
    @FXML private VBox placeholderContainer;
    @FXML private VBox detailContainer;
    @FXML private Label
            voornaamDetail,
            achternaamDetail,
            landDetail,
            postcodeDetail,
            stadDetail,
            straatDetail,
            huisnrDetail,
            busDetail,
            emailDetail,
            telefoonDetail,
            jobtitelDetail;
    @FXML private TableView<ObservableTeam> werknemerTeams;
    @FXML private TableColumn<ObservableTeam, String> naamCol, siteCol;
    @FXML private TableColumn<ObservableWerknemer, Void> werknemerBtnCol;
    @FXML private Button annuleerBtn, toevoegenBtn;

    private final MainController mainController;
    private final WerknemerController werknemerController;
    private final TeamController teamController;
    private final SiteController siteController;
    private ObservableTeamsTable observableTeamsTable;

    private ObservableWerknemer werknemerToEdit = null;
    private final Map<String, TextField> columnFilterFields = new LinkedHashMap<>();
    private final ObservableWerknemersTable observableWerknemersTable;

    private final Map<String, CheckBox> jobtitelCheckboxes = new LinkedHashMap<>();
    private final Map<String, CheckBox> statusCheckboxes = new LinkedHashMap<>();


    public GebruikersController(MainController mainController, WerknemerController controller, TeamController teamController, SiteController siteController) {

        this.mainController = mainController;
        this.teamController = teamController;
        this.siteController = siteController;
        this.werknemerController = controller;
        // wrapper maken
        this.observableWerknemersTable = new ObservableWerknemersTable(controller, controller.getWerknemers());

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/WerknemersFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        initializeTable();
        initializeWerknemerTeamsTable();
    }

    private void initializeTable() {
        columnFilterFields.put("achternaam", setupTextFilter(lastNameCol, "achternaam"));
        columnFilterFields.put("voornaam", setupTextFilter(firstNameCol, "voornaam"));
        columnFilterFields.put("email", setupTextFilter(emailCol, "email"));
        setupCheckboxFilter(jobTitelCol, "jobtitel",
                Arrays.stream(JobTitel.values()).map(Enum::name).toList(),
                jobtitelCheckboxes
                );
        setupCheckboxFilter(statusCol, "status",
                Arrays.stream(EntityStatus.values()).map(Enum::name).toList(),
                statusCheckboxes
                );

        firstNameCol.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameCol.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());
        jobTitelCol.setCellValueFactory(cellData -> cellData.getValue().jobTitelProperty());
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
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

        Image trashImage = new Image(getClass().getResource("/images/16201366061606130516-128.png").toString());
        Image editImage = new Image(getClass().getResource("/images/edit.png").toString());

        werknemerBtnCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteWerknemerBtn = new Button();
            private final Button editWerknemerBtn = new Button();
            private final HBox buttons = new HBox(8, editWerknemerBtn, deleteWerknemerBtn);
            {
                ImageView deleteIcon = new ImageView(trashImage);
                deleteIcon.setFitWidth(20);
                deleteIcon.setPreserveRatio(true);
                deleteIcon.setSmooth(false);
                deleteWerknemerBtn.setGraphic(deleteIcon);
                deleteWerknemerBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                deleteWerknemerBtn.setOnAction(e -> {
                    ObservableWerknemer w = getTableView().getItems().get(getIndex());
                    onDeleteWerknemerAction(w);
                });

                ImageView editIcon = new ImageView(editImage);
                editIcon.setFitWidth(20);
                editIcon.setPreserveRatio(true);
                editIcon.setSmooth(false);
                editWerknemerBtn.setGraphic(editIcon);
                editWerknemerBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                editWerknemerBtn.setOnAction(e -> {
                    ObservableWerknemer w = getTableView().getItems().get(getIndex());
                    onEditWerknemerAction(w);
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

        // SortedList in GUI
        SortedList<ObservableWerknemer> sortedList =
                new SortedList<>(observableWerknemersTable.getFilteredList());

        // Binding voor kolomsortering
        sortedList.comparatorProperty().bind(tblWerknemers.comparatorProperty());

        tblWerknemers.setItems(sortedList);

        // Default sortering instellen
        firstNameCol.setSortType(TableColumn.SortType.ASCENDING);
        tblWerknemers.getSortOrder().add(lastNameCol);

        tblWerknemers.getSelectionModel().selectedItemProperty().
                addListener((observableValue, oldPerson, newPerson) -> {
                    //Controleer of er een persoon is geselecteerd
                    if (newPerson != null) {
                        showDetail(newPerson);
                        int index = tblWerknemers.
                                getSelectionModel().getSelectedIndex();
                        System.out.printf("%d %s %s%n", index,
                                newPerson.getFirstName(),
                                newPerson.getLastName());
                    }
                    else {
                        showPlaceholder();
                    }
                });

        //jobtitelCombo
        for (JobTitel jobTitel : JobTitel.values()) {
            jobtitelInput.getItems().add(jobTitel.name().toLowerCase());
        }
        jobtitelInput.getSelectionModel().selectFirst();
        showPlaceholder();

        setToevoegen();
    }

    @Override
    public void updateFilter() {
        String achternaam = columnFilterFields.getOrDefault("achternaam", new TextField()).getText();
        String voornaam = columnFilterFields.getOrDefault("voornaam", new TextField()).getText();
        String email  = columnFilterFields.getOrDefault("email", new TextField()).getText();

        Set<String> toegestaneJobtitels = jobtitelCheckboxes.values().stream().noneMatch(CheckBox::isSelected) ?
                Arrays.stream(JobTitel.values()).map(Enum::name).collect(Collectors.toSet()) :
                jobtitelCheckboxes.entrySet().stream().filter(e -> e.getValue().isSelected())
                        .map(Map.Entry::getKey).collect(Collectors.toSet());

        Set<String> toegestaneStatussen = statusCheckboxes.values().stream().noneMatch(CheckBox::isSelected) ?
                Arrays.stream(EntityStatus.values()).map(Enum::name).collect(Collectors.toSet()) :
                statusCheckboxes.entrySet().stream().filter(e -> e.getValue().isSelected())
                        .map(Map.Entry::getKey).collect(Collectors.toSet());

        observableWerknemersTable.changeFilter(achternaam, voornaam, email, toegestaneStatussen, toegestaneJobtitels);
    }

    @Override
    public TableView<?> getTable() {
        return tblWerknemers;
    }

    @FXML
    private void resetFilters() {
        columnFilterFields.values().forEach(TextInputControl::clear);
        jobtitelCheckboxes.values().forEach(cb -> cb.setSelected(true));
        statusCheckboxes.values().forEach(cb -> cb.setSelected(true));
    }

    private void initializeWerknemerTeamsTable() {
        naamCol.setCellValueFactory(cellData -> cellData.getValue().naamProperty());
        siteCol.setCellValueFactory(cellData -> cellData.getValue().siteProperty());

        naamCol.setSortable(false);
        naamCol.setStyle("-fx-border-color: transparent;");
        siteCol.setSortable(false);
        werknemerTeams.setSelectionModel(null);
    }

    private void resetErrors() {
        voornaamError.setText("");
        achternaamError.setText("");
        telefoonError.setText("");
        geboorteError.setText("");
        jobtitelError.setText("");
        landError.setText("");
        postcodeError.setText("");
        stadError.setText("");
        straatError.setText("");
        huisnrError.setText("");
        busError.setText("");
    }

    private void setInputFiels(ObservableWerknemer werknemer) {
        if (werknemer == null) {
            voornaamInput.clear();
            achternaamInput.clear();
            telefoonInput.clear();
            geboorteInput.setValue(null);
            jobtitelInput.getSelectionModel().selectFirst();
            landInput.clear();
            postcodeInput.clear();
            stadInput.clear();
            straatInput.clear();
            huisnrInput.clear();
            busInput.clear();
        }
        else {
            voornaamInput.setText(werknemer.firstNameProperty().getValue());
            achternaamInput.setText(werknemer.lastNameProperty().getValue());
            telefoonInput.setText(werknemer.telefoonProperty().getValue());
            geboorteInput.setValue(werknemer.getWerknemer().geboortedatum());
            landInput.setText(werknemer.landProperty().getValue());
            jobtitelInput.setValue(werknemer.jobTitelProperty().getValue());
            postcodeInput.setText(werknemer.postcodeProperty().getValue());
            stadInput.setText(werknemer.stadProperty().getValue());
            straatInput.setText(werknemer.straatProperty().getValue());
            huisnrInput.setText(werknemer.huisnrProperty().getValue());
            busInput.setText(werknemer.busProperty().getValue());
        }
    }

    private void onEditWerknemerAction(ObservableWerknemer werknemer) {
        setEdit(werknemer);
    }

    public void onCancelEdit() {
        setToevoegen();
    }

    private void setEdit(ObservableWerknemer werknemer) {
        werknemerToEdit = werknemer;
        resetErrors();
        annuleerBtn.setVisible(true);
        annuleerBtn.setManaged(true);
        toevoegenBtn.setText("Bewerken");
        setInputFiels(werknemer);
    }

    private void setToevoegen() {
        werknemerToEdit = null;
        resetErrors();
        annuleerBtn.setVisible(false);
        annuleerBtn.setManaged(false);
        toevoegenBtn.setText("Toevoegen");
        setInputFiels(null);
    }

    private void onDeleteWerknemerAction(ObservableWerknemer werknemer) {
        mainController.showPopup(new VerwijderController(mainController,
                "Deactiveer werknemer", "Weet je zeker dat je deze werknemer wil deactiveren? Je kan deze later opnieuw activeren.",
                () -> observableWerknemersTable.deactiveerWerknemer(werknemer)));
    }

    private void showDetail(ObservableWerknemer werknemer) {
        List<TeamDTO> teamsWerknemer = teamController.getTeamsVanWerknemer(werknemer.getId());
        List<TeamDTO> teamsVerantwoordelijke = teamController.getTeamsVanVerantwoordelijke(werknemer.getId());

        Set<TeamDTO> teams = new HashSet<>();
        teams.addAll(teamsWerknemer);
        teams.addAll(teamsVerantwoordelijke);

        observableTeamsTable = new ObservableTeamsTable(
                teamController,
                werknemerController,
                siteController,
                teams.stream().toList()
        );


        voornaamDetail.setText(werknemer.firstNameProperty().getValue());
        achternaamDetail.setText(werknemer.lastNameProperty().getValue());
        landDetail.setText(werknemer.landProperty().getValue());
        postcodeDetail.setText(werknemer.postcodeProperty().getValue());
        stadDetail.setText(werknemer.stadProperty().getValue());
        straatDetail.setText(werknemer.straatProperty().getValue());
        huisnrDetail.setText(werknemer.huisnrProperty().getValue());
        busDetail.setText(werknemer.busProperty().getValue());
        emailDetail.setText(werknemer.emailProperty().getValue());
        telefoonDetail.setText(werknemer.telefoonProperty().getValue());
        jobtitelDetail.setText(werknemer.jobTitelProperty().getValue());

        werknemerTeams.setItems(observableTeamsTable.getFilteredTeams());

        detailContainer.setVisible(true);
        detailContainer.setManaged(true);
        placeholderContainer.setVisible(false);
        placeholderContainer.setManaged(false);
    }

    private void showPlaceholder() {
        placeholderContainer.setVisible(true);
        placeholderContainer.setManaged(true);
        detailContainer.setVisible(false);
        detailContainer.setManaged(false);
    }

    public void btnToevoegenAction(ActionEvent actionEvent) {
        resetErrors();

        Integer id = null;
        if (werknemerToEdit != null) {
            id = werknemerToEdit.getId();
        }
        String voornaam =  voornaamInput.getText();
        String achternaam =  achternaamInput.getText();
        String telefoon =  telefoonInput.getText();
        JobTitel jobTitel = JobTitel.valueOf(jobtitelInput.getSelectionModel().getSelectedItem().toUpperCase());
        LocalDate geboorteDatum =  geboorteInput.getValue();
        String land =  landInput.getText();
        String postcode =  postcodeInput.getText();
        String stad =  stadInput.getText();
        String straat =  straatInput.getText();
        Integer huisnr;
        Integer bus;
        try {
            huisnr =  Integer.parseInt(huisnrInput.getText());
        } catch (NumberFormatException e) {
            huisnr = null;
        }

        try {
            bus = Integer.parseInt(busInput.getText());
        } catch (NumberFormatException e) {
            bus = null;
        }

        try {
            observableWerknemersTable.saveWerknemer(new WerknemerInputDTO(id, voornaam, achternaam, jobTitel, telefoon, geboorteDatum, land, postcode, stad, straat, huisnr, bus));
            setToevoegen();
        } catch (WerknemerInformationException ex) {
            handleErrors(ex);
        }
    }

    private void handleErrors (WerknemerInformationException ex) {
        ex.getInformationRequired().forEach((field, message) -> {
            switch (field) {
                case VOORNAAM    -> voornaamError.setText(message);
                case ACHTERNAAM  -> achternaamError.setText(message);
                case TELEFOON    -> telefoonError.setText(message);
                case GEBOORTEDATUM -> geboorteError.setText(message);
                case JOBTITEL    -> jobtitelError.setText(message);
                case LAND        -> landError.setText(message);
                case POSTCODE    -> postcodeError.setText(message);
                case STAD        -> stadError.setText(message);
                case STRAAT      -> straatError.setText(message);
                case HUISNUMMER      -> huisnrError.setText(message);
                case BUS         -> busError.setText(message);
            }
        });
    }
}