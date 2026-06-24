package org.sdp.sdp.gui;

import domein.OperationeleStatus;
import domein.SiteProductieStatus;
import domein.SiteController;
import dto.SiteInputDTO;
import exception.SiteInformationException;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.SiteElement;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class SiteControllerGUI extends ScrollPane implements FilterableTable {
    private final MainController mainController;
    private final SiteController siteController;
    private final ObservableSitesTable observableSitesTable;
    private ObservableSite geselecteerdeSite;
    // ----- Tabel ---------------------------------------------------------
    @FXML private TableView<ObservableSite> tblSites;
    @FXML private TableColumn<ObservableSite, String> nameCol;
    @FXML private TableColumn<ObservableSite, String> locatieCol;
    @FXML private TableColumn<ObservableSite, String> landCol;
    @FXML private TableColumn<ObservableSite, Integer> capaciteitCol;
    @FXML private TableColumn<ObservableSite, String> operationeleStatusCol;
    @FXML private TableColumn<ObservableSite, String> productieStatusCol;
    @FXML private TableColumn<ObservableSite, Void> actieCol;

    // ----- TekstFilter (naam, locatie en land) ---------------------------------
    private final Map<String, TextField> columnFilterFields = new LinkedHashMap<>();
    // ----- Capaciteit range-filter -----------------------------------------------
    private TextField txtMinCapaciteit;
    private TextField txtMaxCapaciteit;
    // ----- Checkbox-filters voor statussen ----------------------------------------
    private final Map<String, CheckBox> opStatusCheckboxes   = new LinkedHashMap<>();
    private final Map<String, CheckBox> prodStatusCheckboxes = new LinkedHashMap<>();

    // ----- Details ----------------------------------------------------------------
    @FXML private VBox      boxGeenDetails;
    @FXML private GridPane boxWelDetails;
    @FXML private Label     lblDetailName;
    @FXML private Label     lblDetailLocatie;
    @FXML private Label     lblDetailLand;
    @FXML private Label     lblDetailCapaciteit;
    @FXML private Label     lblDetailOperationeleStatus;
    @FXML private Label     lblDetailProductieStatus;
    @FXML private Label     lblDetailBreedtegraad;
    @FXML private Label     lblDetailLengtegraad;
    // ----- Toevoeg/Wijzig panel --------------
    @FXML private Label lblFormTitel;
    @FXML private TextField nameInput;
    @FXML private TextField locatieInput;
    @FXML private TextField landInput;
    @FXML private TextField capaciteitInput;
    @FXML private ComboBox<String> cmbOperationeleStatus;
    @FXML private ComboBox<String> cmbProductieStatus;
    @FXML private TextField breedtegraadInput;
    @FXML private TextField lengtegraadInput;
    @FXML private Button    btnToevoegen;
    @FXML private HBox      hboxWijzigenKnoppen;
    // ----- Error labels --------------
    @FXML private Label naamError;
    @FXML private Label locatieError;
    @FXML private Label landError;
    @FXML private Label capaciteitError;
    @FXML private Label operationeleStatusError;
    @FXML private Label productieStatusError;
    @FXML private Label breedtegraadError;
    @FXML private Label lengtegraadError;

    public SiteControllerGUI(MainController mainController, SiteController siteController){
        this.mainController = mainController;
        this.siteController = siteController;
        // wrapper maken
        this.observableSitesTable = new ObservableSitesTable(siteController);

        FXMLLoader loader = new FXMLLoader(getClass()
                .getResource("/org.sdp.sdp/gui/SitesFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException ex) { throw new RuntimeException(ex); }

        initializeTable();
        initializeFormSelectors();
        initializeDetailsPanel();
        showToevoegenForm();
    }

    private void initializeTable() {
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        locatieCol.setCellValueFactory(cellData -> cellData.getValue().locatieProperty());
        landCol.setCellValueFactory(cellData -> cellData.getValue().landProperty());
        capaciteitCol.setCellValueFactory(cellData -> cellData.getValue().capaciteitProperty().asObject());
        operationeleStatusCol.setCellValueFactory(cellData -> cellData.getValue().operationeleStatusProperty());
        operationeleStatusCol.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(capitalize(item));
                zetKleurOperationeel(this, item);
            }
        });

        productieStatusCol.setCellValueFactory(cellData -> cellData.getValue().productieStatusProperty());
        productieStatusCol.setCellFactory(col -> new TableCell<ObservableSite, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(capitalize(item));
                zetKleurProductie(this, item);
            }
        });

        // Tekst-filters (naam & locatie)
        columnFilterFields.put("Naam", setupTextFilter(nameCol,"Naam"));
        columnFilterFields.put("Land", setupTextFilter(landCol,"Land"));
        columnFilterFields.put("Locatie",  setupTextFilter(locatieCol,"Locatie"));

        // Range-filter (capaciteit)
        List<TextField> range = setupRangeFilter(capaciteitCol, "Capaciteit");
        txtMinCapaciteit = range.get(0);
        txtMaxCapaciteit = range.get(1);

        // Checkbox-filters (statussen)
        setupCheckboxFilter(operationeleStatusCol, "Operationele status",
                Arrays.stream(OperationeleStatus.values()).map(Enum::name).toList(),
                opStatusCheckboxes);

        setupCheckboxFilter(
                productieStatusCol, "Productie Status",
                Arrays.stream(SiteProductieStatus.values()).map(Enum::name).toList(),
                prodStatusCheckboxes);

        setupActionButtons();

        // SortedList in GUI
        SortedList<ObservableSite> sortedList =
                new SortedList<>(observableSitesTable.getFilteredList());
        // Binding voor kolomsortering
        sortedList.comparatorProperty().bind(tblSites.comparatorProperty());
        tblSites.setItems(sortedList);
        // Default sortering instellen
        nameCol.setSortType(TableColumn.SortType.ASCENDING);
        tblSites.getSortOrder().add(nameCol);
        tblSites.getSelectionModel().selectedItemProperty().
                addListener((observableValue, oldValue, newValue) -> {
                    //Controleer of er een site is geselecteerd
                    if (newValue != null) {
                        int index = tblSites.
                                getSelectionModel().getSelectedIndex();
                        System.out.printf("%d %s%n", index,
                                newValue.getName());
                    }
                });
    }

    // ── Filter samenvoegen en doorzetten ──────────────────────────────────────
    @Override
    public void updateFilter() {
        String naam    = columnFilterFields.getOrDefault("Naam",    new TextField()).getText();
        String locatie = columnFilterFields.getOrDefault("Locatie", new TextField()).getText();
        String land = columnFilterFields.getOrDefault("Land", new TextField()).getText();

        Integer min = parseIntOrNull(txtMinCapaciteit.getText());
        Integer max = parseIntOrNull(txtMaxCapaciteit.getText());

        // Als geen enkele checkbox aangevinkt is → behandel als "alles toegestaan"
        Set<String> toegestaneOpStatus = opStatusCheckboxes.values().stream()
                .noneMatch(CheckBox::isSelected)
                ? Arrays.stream(OperationeleStatus.values()).map(Enum::name).collect(Collectors.toSet())
                : opStatusCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<String> toegestaneProdStatus = prodStatusCheckboxes.values().stream()
                .noneMatch(CheckBox::isSelected)
                ? Arrays.stream(SiteProductieStatus.values()).map(Enum::name).collect(Collectors.toSet())
                : prodStatusCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        observableSitesTable.changeFilter(
                naam, locatie, land, min, max,
                toegestaneOpStatus, toegestaneProdStatus);
    }

    @Override
    public TableView<?> getTable() {
        return tblSites;
    }

    // ──  Alle filters resetten ──────────────────────────────────────
    @FXML
    private void resetFilters() {
        columnFilterFields.values().forEach(tf -> tf.clear());
        txtMinCapaciteit.clear();
        txtMaxCapaciteit.clear();
        opStatusCheckboxes.values().forEach(cb -> cb.setSelected(true));
        prodStatusCheckboxes.values().forEach(cb -> cb.setSelected(true));
    }

    private Integer parseIntOrNull(String tekst) {
        try { return (tekst == null || tekst.isBlank()) ? null : Integer.parseInt(tekst.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    // ── Actie-knoppen (edit / delete) ─────────────────────────────────────────
    private void setupActionButtons() {
        Image editImage;
        Image trashImage;
        try {
            editImage  = new Image(getClass().getResource("/image/pencil.png").toString());
            trashImage = new Image(getClass().getResource("/image/trash.png").toString());
        } catch (Exception e) {
            editImage  = new Image(getClass().getResource("/images/pencil.png").toString());
            trashImage = new Image(getClass().getResource("/images/trash.png").toString());
        }
        Image finalEdit  = editImage;
        Image finalTrash = trashImage;

        actieCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDel  = new Button();
            private final HBox   pane    = new HBox(8, btnEdit, btnDel);

            {
                pane.setAlignment(Pos.CENTER);

                ImageView editIcon = new ImageView(finalEdit);
                editIcon.setFitWidth(16); editIcon.setPreserveRatio(true); editIcon.setSmooth(false);
                btnEdit.setGraphic(editIcon);
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");

                ImageView trashIcon = new ImageView(finalTrash);
                trashIcon.setFitWidth(16); trashIcon.setPreserveRatio(true); trashIcon.setSmooth(false);
                btnDel.setGraphic(trashIcon);
                btnDel.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");

                btnDel.setOnAction(e -> {
                    ObservableSite site = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                            "Weet je zeker dat je \"" + site.getName() + "\" wilt verwijderen?",
                            ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Site verwijderen");
                    alert.showAndWait()
                            .filter(r -> r == ButtonType.YES)
                            .ifPresent(r -> {
                                siteController.verwijderSite(site.getId());
                                observableSitesTable.refresh();
                                verbergDetails();
                            });
                });

                btnEdit.setOnAction(e -> {
                    geselecteerdeSite = getTableView().getItems().get(getIndex());
                    showWijzigenForm(geselecteerdeSite);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    // ── Panel wisselen ────────────────────────────────────────────────────────
    private void showToevoegenForm() {
        lblFormTitel.setText("Site Toevoegen");
        btnToevoegen.setVisible(true);    btnToevoegen.setManaged(true);
        hboxWijzigenKnoppen.setVisible(false); hboxWijzigenKnoppen.setManaged(false);

        nameInput.clear();
        locatieInput.clear();
        landInput.clear();
        capaciteitInput.clear();
        cmbOperationeleStatus.setValue(null);
        cmbProductieStatus.setValue(null);
        breedtegraadInput.clear();
        lengtegraadInput.clear();
    }

    private void showWijzigenForm(ObservableSite site) {
        lblFormTitel.setText("Site Wijzigen");
        btnToevoegen.setVisible(false);   btnToevoegen.setManaged(false);
        hboxWijzigenKnoppen.setVisible(true); hboxWijzigenKnoppen.setManaged(true);

        clearErrorLabels();

        nameInput.setText(site.getName());
        locatieInput.setText(site.locatieProperty().get());
        landInput.setText(site.landProperty().get());
        capaciteitInput.setText(site.getCapaciteit());
        cmbOperationeleStatus.getSelectionModel().select(site.operationeleStatusProperty().get());
        cmbProductieStatus.getSelectionModel().select(site.productieStatusProperty().get());
        breedtegraadInput.setText(site.breedtegraadProperty().get());
        lengtegraadInput.setText(site.lengtegraadProperty().get());
    }

    // ── ComboBox-vullers ──────────────────────────────────────────────────────
    private void initializeFormSelectors() {
        List<String> opStatussen   = Arrays.stream(OperationeleStatus.values()).map(Enum::name).toList();
        List<String> prodStatussen = Arrays.stream(SiteProductieStatus.values()).map(Enum::name).toList();

        cmbOperationeleStatus.setItems(FXCollections.observableArrayList(opStatussen));
        cmbProductieStatus.setItems(FXCollections.observableArrayList(prodStatussen));
    }

    // ── Details panel ─────────────────────────────────────────────────────────
    private void initializeDetailsPanel() {
        tblSites.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) toonDetails(newVal);
                    else verbergDetails();
                });
    }

    private void toonDetails(ObservableSite site) {
        lblDetailName.setText(site.getName());
        lblDetailLocatie.setText(site.locatieProperty().get());
        lblDetailLand.setText(site.landProperty().get());
        lblDetailCapaciteit.setText(site.getCapaciteit());

        String opStatus = site.operationeleStatusProperty().get();
        lblDetailOperationeleStatus.setText(capitalize(opStatus));
        zetKleurOperationeel(lblDetailOperationeleStatus, opStatus);

        String prodStatus = site.productieStatusProperty().get();
        lblDetailProductieStatus.setText(capitalize(prodStatus));
        zetKleurProductie(lblDetailProductieStatus, prodStatus);

        boxGeenDetails.setVisible(false); boxGeenDetails.setManaged(false);
        boxWelDetails.setVisible(true);   boxWelDetails.setManaged(true);

        lblDetailBreedtegraad.setText(site.breedtegraadProperty().get());
        lblDetailLengtegraad.setText(site.lengtegraadProperty().get());
    }

    private void verbergDetails() {
        boxGeenDetails.setVisible(true);  boxGeenDetails.setManaged(true);
        boxWelDetails.setVisible(false);  boxWelDetails.setManaged(false);
    }

    // ── Kleur-helpers ─────────────────────────────────────────────────────────
    private void zetKleurOperationeel(Labeled c, String waarde) {
        if (waarde == null) { c.setStyle(""); return; }
        switch (waarde.toUpperCase()) {
            case "ACTIEF"   -> c.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            case "INACTIEF" -> c.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            default         -> c.setStyle("-fx-font-weight: bold;");
        }
    }

    private void zetKleurProductie(Labeled c, String waarde) {
        if (waarde == null) { c.setStyle(""); return; }
        switch (waarde.toUpperCase()) {
            case "GEZOND"    -> c.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
            case "PROBLEMEN" -> c.setStyle("-fx-text-fill: #ed6c02; -fx-font-weight: bold;");
            case "OFFLINE"   -> c.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
            default          -> c.setStyle("-fx-font-weight: bold;");
        }
    }

    // ── Hulpmethoden ───────────────────────────────────────────────────
    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    private Label boldLabel(String tekst) {
        Label lbl = new Label(tekst);
        lbl.setStyle("-fx-font-weight: bold;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER);
        return lbl;
    }

    // ── FXML handlers ─────────────────────────────────────────────────────────
    @FXML
    private void btnToevoegenAction(ActionEvent event) {
        clearErrorLabels();

        String naam       = nameInput.getText().trim();
        String locatie    = locatieInput.getText().trim();
        String land       = landInput.getText().trim();
        String capStr     = capaciteitInput.getText().trim();
        String opStatus   = cmbOperationeleStatus.getValue();
        String prodStatus = cmbProductieStatus.getValue();
        String bgStr = breedtegraadInput.getText().trim();
        String lgStr = lengtegraadInput.getText().trim();

        try {
            valideerSiteInput(naam, locatie, land, capStr, opStatus, prodStatus, bgStr, lgStr);
            int cap = Integer.parseInt(capStr);;

            BigDecimal breedtegraad = parseCoordinaat(
                    bgStr, SiteElement.BREEDTEGRAAD, "Breedtegraad", -90, 90);
            BigDecimal lengtegraad = parseCoordinaat(
                    lgStr, SiteElement.LENGTEGRAAD, "Lengtegraad", -180, 180);

            siteController.voegSiteToe(new SiteInputDTO(
                    naam, locatie, land, cap, opStatus, prodStatus,
                    breedtegraad, lengtegraad));
            observableSitesTable.refresh();
            showToevoegenForm();
        } catch (SiteInformationException ex) {
            toonFouten(ex);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Onverwachte fout: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void btnOpslaanWijzigAction(ActionEvent event) {
        if (geselecteerdeSite == null) return;
        clearErrorLabels();

        String naam       = nameInput.getText().trim();
        String locatie    = locatieInput.getText().trim();
        String land       = landInput.getText().trim();
        String capStr     = capaciteitInput.getText().trim();
        String opStatus   = cmbOperationeleStatus.getValue();
        String prodStatus = cmbProductieStatus.getValue();
        String bgStr = breedtegraadInput.getText().trim();
        String lgStr = lengtegraadInput.getText().trim();

        try {
            valideerSiteInput(naam, locatie, land, capStr, opStatus, prodStatus, bgStr, lgStr);
            int cap = Integer.parseInt(capStr);

            BigDecimal breedtegraad = parseCoordinaat(
                    bgStr, SiteElement.BREEDTEGRAAD, "Breedtegraad", -90, 90);
            BigDecimal lengtegraad = parseCoordinaat(
                    lgStr, SiteElement.LENGTEGRAAD, "Lengtegraad", -180, 180);

            siteController.wijzigSite(geselecteerdeSite.getId(), new SiteInputDTO(
                    naam, locatie, land, cap, opStatus, prodStatus,
                    breedtegraad, lengtegraad));

            observableSitesTable.refresh();
            showToevoegenForm();
            verbergDetails();
        } catch (SiteInformationException ex) {
            toonFouten(ex);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Onverwachte fout: " + e.getMessage()).showAndWait();
        }
    }

    private BigDecimal parseCoordinaat(String tekst, SiteElement element, String label,
                                       double min, double max) throws SiteInformationException {
        if (tekst == null || tekst.isBlank()) {
            throw new SiteInformationException(
                    Map.of(element, label + " is verplicht.")
            );
        }
        try {
            BigDecimal waarde = new BigDecimal(tekst.trim().replace(',', '.'));
            if (waarde.compareTo(BigDecimal.valueOf(min)) < 0
                    || waarde.compareTo(BigDecimal.valueOf(max)) > 0) {
                throw new SiteInformationException(
                        Map.of(element, label + " moet tussen " + min + " en " + max + " liggen.")
                );
            }
            return waarde;
        } catch (NumberFormatException e) {
            throw new SiteInformationException(
                    Map.of(element,label + " moet een decimaal getal zijn.")
            );
        }
    }

    @FXML
    private void btnAnnulerenWijzig(ActionEvent event) {
        showToevoegenForm();
    }

    // ── Error handling helpers ─────────────────────────────────────────────
    private void clearErrorLabels() {
        naamError.setText("");
        locatieError.setText("");
        landError.setText("");
        capaciteitError.setText("");
        operationeleStatusError.setText("");
        productieStatusError.setText("");
        breedtegraadError.setText("");
        lengtegraadError.setText("");
    }

    private void toonFouten(SiteInformationException ex) {
        clearErrorLabels();

        Map<SiteElement, String> errors = ex.getInformationRequired();

        for (Map.Entry<SiteElement, String> entry : errors.entrySet()) {
            switch (entry.getKey()) {
                case NAME -> naamError.setText(entry.getValue());
                case LOCATIE -> locatieError.setText(entry.getValue());
                case LAND -> landError.setText(entry.getValue());
                case CAPACITEIT -> capaciteitError.setText(entry.getValue());
                case OPERATIONELESTATUS -> operationeleStatusError.setText(entry.getValue());
                case PRODUCTIESTATUS -> productieStatusError.setText(entry.getValue());
                case BREEDTEGRAAD -> breedtegraadError.setText(entry.getValue());
                case LENGTEGRAAD -> lengtegraadError.setText(entry.getValue());
            }
        }
    }

    private void valideerSiteInput(String naam, String locatie, String land, String capStr,
                                   String opStatus, String prodStatus,
                                   String breedtegraadStr, String lengtegraadStr)
            throws SiteInformationException {

        Map<SiteElement, String> errors = new LinkedHashMap<>();

        if (naam.isBlank())
            errors.put(SiteElement.NAME, "Naam is verplicht.");
        if (locatie.isBlank())
            errors.put(SiteElement.LOCATIE, "Locatie is verplicht.");
        if (land.isBlank())
            errors.put(SiteElement.LAND, "Land is verplicht.");
        if (opStatus == null || opStatus.isBlank())
            errors.put(SiteElement.OPERATIONELESTATUS, "Operationele status is verplicht.");
        if (prodStatus == null || prodStatus.isBlank())
            errors.put(SiteElement.PRODUCTIESTATUS, "Productie status is verplicht.");

        if (capStr.isBlank()) {
            errors.put(SiteElement.CAPACITEIT, "Capaciteit is verplicht.");
        } else {
            try {
                int cap = Integer.parseInt(capStr);
                if (cap < 0) {
                    errors.put(SiteElement.CAPACITEIT, "Capaciteit mag niet negatief zijn.");
                }
            } catch (NumberFormatException e) {
                errors.put(SiteElement.CAPACITEIT, "Capaciteit moet een geheel getal zijn.");
            }
        }

        try {
            parseCoordinaat(breedtegraadStr, SiteElement.BREEDTEGRAAD, "Breedtegraad", -90, 90);
        } catch (SiteInformationException e) {
            errors.putAll(e.getInformationRequired());
        }

        try {
            parseCoordinaat(lengtegraadStr, SiteElement.LENGTEGRAAD, "Lengtegraad", -180, 180);
        } catch (SiteInformationException e) {
            errors.putAll(e.getInformationRequired());
        }

        if (!errors.isEmpty()) {
            throw new SiteInformationException(errors);
        }
    }
}