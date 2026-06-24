package org.sdp.sdp.gui;

import domein.*;
import dto.MachineInputDTO;
import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import util.MeldingType;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class MachineControllerGUI extends HBox implements FilterableTable{
    private final WerknemerController werknemerController;
    private final MachineController machineController;
    private final SiteController siteController;
    private final MeldingenController meldingenController;
    private final ObservableMachinesTable observableMachinesTable;
    private final MainController mainController;
    private ObservableMachine geselecteerdeMachine;

    @FXML private TableView<ObservableMachine> tblMachines;
    @FXML private TableColumn<ObservableMachine, String> codeCol, siteCol, datumOnderhoudCol, productieStatusCol, statusCol;
    @FXML private TableColumn<ObservableMachine, Void> actieCol;
    private final Map<String, TextField> columnFilterFields = new LinkedHashMap<>();
    private DatePicker dpMinDatum;
    private DatePicker dpMaxDatum;
    private final Map<String, CheckBox> prodStatusCheckboxes = new LinkedHashMap<>();
    private final Map<String, CheckBox> statusCheckboxes = new LinkedHashMap<>();

    @FXML private VBox boxGeenDetails, vboxToevoegen, vboxWijzigen;
    @FXML private GridPane boxWelDetails;
    @FXML private Label lblDetailCode, lblDetailStatus, lblDetailOnderhoud, lblDetailLocatie, lblDetailProduct;

    @FXML private TextField txtAddCode, txtAddLocatie, txtAddProduct;
    @FXML private ComboBox<String> cmbAddSite, cmbAddStatus;
    @FXML private ComboBox<ProductieStatus> cmbAddProductieStatus;

    @FXML private Label lblAddCodeError, lblAddSiteError, lblAddLocatieError, lblAddProductError, lblAddStatusError, lblAddProdStatusError;

    @FXML private TextField txtEditCode, txtEditLocatie, txtEditProduct;
    @FXML private ComboBox<String> cmbEditSite, cmbEditStatus;
    @FXML private ComboBox<ProductieStatus> cmbEditProductieStatus;
    @FXML private Label lblEditCodeError, lblEditSiteError, lblEditLocatieError, lblEditProductError, lblEditStatusError, lblEditProdStatusError;

    public MachineControllerGUI(MainController mainController, MachineController controller, SiteController siteController, WerknemerController werknemerController, MeldingenController meldingenController) {
        this.mainController = mainController;
        this.werknemerController = werknemerController;
        this.machineController = controller;
        this.siteController = siteController;
        this.meldingenController = meldingenController;

        this.observableMachinesTable = new ObservableMachinesTable(controller);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/MachinesFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try { loader.load(); } catch (IOException ex) { throw new RuntimeException(ex); }

        initializeTable();
        initializeFormSelectors();
        initializeDetailsPanel();
        showToevoegenForm();
    }

    private void initializeTable() {
        codeCol.setCellValueFactory(d -> d.getValue().codeProperty());
        siteCol.setCellValueFactory(d -> d.getValue().siteNaamProperty());
        datumOnderhoudCol.setCellValueFactory(d -> d.getValue().datumLaatsteOnderhoudProperty());

        productieStatusCol.setCellValueFactory(d -> d.getValue().productieStatusProperty());
        productieStatusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else { setText(capitalize(item)); zetKleur(this, item); }
            }
        });

        statusCol.setCellValueFactory(d -> d.getValue().statusProperty());
        statusCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); }
                else { setText(capitalize(item)); zetKleur(this, item); }
            }
        });

        columnFilterFields.put("Code", setupTextFilter(codeCol, "Code"));
        columnFilterFields.put("Site", setupTextFilter(siteCol, "Site"));
        setupDateRangeFilter(datumOnderhoudCol, "Datum laatste onderhoud");

        setupCheckboxFilter(productieStatusCol, "Productiestatus", Arrays.stream(ProductieStatus.values()).map(Enum::name).toList(), prodStatusCheckboxes);
        setupCheckboxFilter(statusCol, "Status", List.of("Draait", "Nood aan onderhoud", "Gestopt"), statusCheckboxes);

        setupActionButtons();

        SortedList<ObservableMachine> sortedList = new SortedList<>(observableMachinesTable.getFilteredList());
        sortedList.comparatorProperty().bind(tblMachines.comparatorProperty());
        tblMachines.setItems(sortedList);
    }

    private void setupDateRangeFilter(TableColumn<?, ?> col, String label) {
        dpMinDatum = new DatePicker(); dpMinDatum.setPromptText("Van datum");
        dpMinDatum.setEditable(false); dpMinDatum.setPrefWidth(125);
        dpMinDatum.getStyleClass().add("filter-date-picker");

        dpMaxDatum = new DatePicker(); dpMaxDatum.setPromptText("Tot datum");
        dpMaxDatum.setEditable(false); dpMaxDatum.setPrefWidth(125);
        dpMaxDatum.getStyleClass().add("filter-date-picker");

        VBox dateBox = new VBox(4, dpMinDatum, dpMaxDatum); dateBox.setAlignment(Pos.CENTER);
        VBox vbox = new VBox(5, boldLabel(label), dateBox); vbox.setAlignment(Pos.CENTER);
        col.setGraphic(vbox); col.setText("");

        dpMinDatum.valueProperty().addListener((obs, old, nw) -> updateFilter());
        dpMaxDatum.valueProperty().addListener((obs, old, nw) -> updateFilter());
    }

    @Override
    public void updateFilter() {
        String code  = columnFilterFields.getOrDefault("Code", new TextField()).getText();
        String site  = columnFilterFields.getOrDefault("Site", new TextField()).getText();
        LocalDate minDatum = dpMinDatum.getValue();
        LocalDate maxDatum = dpMaxDatum.getValue();

        Set<String> toegestaneProdStatus = prodStatusCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected()).map(Map.Entry::getKey).collect(Collectors.toSet());
        Set<String> toegestaneStatus = statusCheckboxes.entrySet().stream()
                .filter(e -> e.getValue().isSelected()).map(Map.Entry::getKey).collect(Collectors.toSet());

        observableMachinesTable.changeFilter(code, site, minDatum, maxDatum, toegestaneProdStatus, toegestaneStatus);
    }

    @Override
    public TableView<?> getTable() {return tblMachines;}

    private void btnResetFiltersAction(ActionEvent event) {
        columnFilterFields.values().forEach(tf -> tf.setText(""));
        if (dpMinDatum != null) dpMinDatum.setValue(null);
        if (dpMaxDatum != null) dpMaxDatum.setValue(null);
        prodStatusCheckboxes.values().forEach(cb -> cb.setSelected(true));
        statusCheckboxes.values().forEach(cb -> cb.setSelected(true));

        tblMachines.getSortOrder().clear();
    }

    private void setupActionButtons() {
        Button btnReset = new Button("↻ Reset");
        btnReset.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #555; -fx-font-weight: bold; -fx-font-size: 11px;");
        btnReset.setOnAction(this::btnResetFiltersAction);
        VBox headerBox = new VBox(btnReset);
        headerBox.setAlignment(Pos.CENTER);
        actieCol.setGraphic(headerBox);
        actieCol.setText("");

        Image editImage = new Image(Objects.requireNonNull(getClass().getResource("/images/edit.png")).toString());
        Image trashImage = new Image(Objects.requireNonNull(getClass().getResource("/images/16201366061606130516-128.png")).toString());

        actieCol.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button();
            private final Button btnDel = new Button();
            private final HBox pane = new HBox(8, btnEdit, btnDel);

            {
                pane.setAlignment(Pos.CENTER);

                ImageView editIcon = new ImageView(editImage);
                editIcon.setFitWidth(16); editIcon.setPreserveRatio(true); editIcon.setSmooth(false);
                btnEdit.setGraphic(editIcon);
                btnEdit.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");

                ImageView deleteIcon = new ImageView(trashImage);
                deleteIcon.setFitWidth(16); deleteIcon.setPreserveRatio(true); deleteIcon.setSmooth(false);
                btnDel.setGraphic(deleteIcon);
                btnDel.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0;");

                btnDel.setOnAction(e -> {
                    ObservableMachine m = getTableView().getItems().get(getIndex());
                    mainController.showPopup(new VerwijderController(
                            mainController, "Verwijder machine",
                            "Weet je zeker dat je machine " + m.getCode() + " wilt verwijderen?",
                            () -> { machineController.verwijderMachine(m.getId()); observableMachinesTable.refresh(); }
                    ));
                });

                btnEdit.setOnAction(e -> {
                    geselecteerdeMachine = getTableView().getItems().get(getIndex());
                    showWijzigenForm(geselecteerdeMachine);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }

    private boolean validateForm(TextField txtCode, ComboBox<String> cmbSite, TextField txtLocatie, TextField txtProduct, ComboBox<String> cmbStatus, ComboBox<ProductieStatus> cmbProdStatus,
                                 Label errCode, Label errSite, Label errLocatie, Label errProduct, Label errStatus, Label errProdStatus) {
        boolean isValid = true;

        errCode.setText(""); errSite.setText(""); errLocatie.setText("");
        errProduct.setText(""); errStatus.setText(""); errProdStatus.setText("");

        String code = txtCode.getText().trim();
        if (code.isBlank()) {
            errCode.setText("Machinecode is verplicht."); isValid = false;
        } else if (!code.matches("^MC-[A-Za-z0-9]{2,4}-\\d{3}$")) {
            errCode.setText("Formaat: MC-XXX-000 (bijv. MC-PLT-001)"); isValid = false;
        }

        if (cmbSite.getValue() == null) { errSite.setText("Selecteer een site."); isValid = false; }

        if (txtLocatie.getText().trim().isBlank()) { errLocatie.setText("Locatie is verplicht."); isValid = false; }

        if (txtProduct.getText().trim().isBlank()) { errProduct.setText("Product is verplicht."); isValid = false; }

        if (cmbStatus.getValue() == null) { errStatus.setText("Selecteer een status."); isValid = false; }

        if (cmbProdStatus.getValue() == null) { errProdStatus.setText("Selecteer een productiestatus."); isValid = false; }

        return isValid;
    }

    @FXML
    private void btnToevoegenAction(ActionEvent event) {
        if (!validateForm(txtAddCode, cmbAddSite, txtAddLocatie, txtAddProduct, cmbAddStatus, cmbAddProductieStatus,
                lblAddCodeError, lblAddSiteError, lblAddLocatieError, lblAddProductError, lblAddStatusError, lblAddProdStatusError)) {
            return;
        }

        String nieuweCode = txtAddCode.getText().trim();

        boolean bestaatAl = machineController.getAllMachines().stream()
                .anyMatch(m -> m.code().equalsIgnoreCase(nieuweCode));

        if (bestaatAl) {
            lblAddCodeError.setText("Deze machinecode bestaat al in het systeem.");
            return;
        }

        try {
            int siteId = siteController.getAllSites().stream()
                    .filter(s -> s.name().equals(cmbAddSite.getValue())).findFirst().map(SiteDTO::id).orElse(0);

            machineController.voegMachineToe(new MachineInputDTO(
                    nieuweCode, siteId, txtAddLocatie.getText().trim(),
                    txtAddProduct.getText().trim(), cmbAddStatus.getValue(), cmbAddProductieStatus.getValue().toString()
            ));
            observableMachinesTable.refresh();
            if (meldingenController != null) {
                List<Integer> ontvangers = werknemerController.getManagerEnVerantwoordelijkeIds();
                meldingenController.voegMeldingToeVoorGroep(MeldingType.SYSTEEM, "Nieuwe Machine", "Machine " + nieuweCode + " is succesvol toegevoegd.", ontvangers);
                mainController.updateMeldingenCounter();
            }
            txtAddCode.clear(); txtAddLocatie.clear(); txtAddProduct.clear();
            cmbAddSite.getSelectionModel().clearSelection();
            cmbAddStatus.getSelectionModel().clearSelection();
            cmbAddProductieStatus.getSelectionModel().clearSelection();
        } catch (Exception e) {
            lblAddCodeError.setText(e.getMessage());
        }
    }

    @FXML
    private void btnOpslaanWijzigAction(ActionEvent event) {
        if (!validateForm(txtEditCode, cmbEditSite, txtEditLocatie, txtEditProduct, cmbEditStatus, cmbEditProductieStatus,
                lblEditCodeError, lblEditSiteError, lblEditLocatieError, lblEditProductError, lblEditStatusError, lblEditProdStatusError)) {
            return;
        }

        String gewijzigdeCode = txtEditCode.getText().trim();

        boolean bestaatAl = machineController.getAllMachines().stream()
                .anyMatch(m -> m.code().equalsIgnoreCase(gewijzigdeCode) && m.id() != geselecteerdeMachine.getId());

        if (bestaatAl) {
            lblEditCodeError.setText("Deze machinecode is al in gebruik door een andere machine.");
            return;
        }

        try {
            int siteId = siteController.getAllSites().stream()
                    .filter(s -> s.name().equals(cmbEditSite.getValue())).findFirst().map(SiteDTO::id).orElse(0);

            machineController.wijzigMachine(geselecteerdeMachine.getId(), new MachineInputDTO(
                    gewijzigdeCode, siteId, txtEditLocatie.getText().trim(),
                    txtEditProduct.getText().trim(), cmbEditStatus.getValue(), cmbEditProductieStatus.getValue().toString()
            ));
            observableMachinesTable.refresh();
            showToevoegenForm();
        } catch (Exception e) {
            lblEditCodeError.setText(e.getMessage());
        }
    }

    private void showToevoegenForm() {
        vboxToevoegen.setVisible(true); vboxToevoegen.setManaged(true);
        vboxWijzigen.setVisible(false); vboxWijzigen.setManaged(false);
        lblAddCodeError.setText(""); lblAddSiteError.setText(""); lblAddLocatieError.setText("");
        lblAddProductError.setText(""); lblAddStatusError.setText(""); lblAddProdStatusError.setText("");
    }

    private void showWijzigenForm(ObservableMachine m) {
        vboxToevoegen.setVisible(false); vboxToevoegen.setManaged(false);
        vboxWijzigen.setVisible(true); vboxWijzigen.setManaged(true);

        txtEditCode.setText(m.getCode()); txtEditLocatie.setText(m.locatieProperty().get());
        txtEditProduct.setText(m.productProperty().get()); cmbEditSite.getSelectionModel().select(m.siteNaamProperty().get());
        cmbEditStatus.getSelectionModel().select(m.statusProperty().get()); cmbEditProductieStatus.getSelectionModel().select(ProductieStatus.valueOf(m.productieStatusProperty().get().toUpperCase()));

        lblEditCodeError.setText(""); lblEditSiteError.setText(""); lblEditLocatieError.setText("");
        lblEditProductError.setText(""); lblEditStatusError.setText(""); lblEditProdStatusError.setText("");
    }

    @FXML private void btnAnnulerenWijzig(ActionEvent event) { showToevoegenForm(); }

    private void initializeFormSelectors() {
        List<String> siteNamen = siteController.getAllSites().stream().map(SiteDTO::name).toList();
        cmbAddSite.setItems(FXCollections.observableArrayList(siteNamen)); cmbEditSite.setItems(FXCollections.observableArrayList(siteNamen));

        List<String> statussen = List.of("Draait", "Nood aan onderhoud", "Gestopt");
        cmbAddStatus.setItems(FXCollections.observableArrayList(statussen)); cmbEditStatus.setItems(FXCollections.observableArrayList(statussen));

        cmbAddProductieStatus.setItems(FXCollections.observableArrayList(ProductieStatus.values())); cmbEditProductieStatus.setItems(FXCollections.observableArrayList(ProductieStatus.values()));
    }

    private void initializeDetailsPanel() {
        tblMachines.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            boxGeenDetails.setVisible(!hasSelection); boxGeenDetails.setManaged(!hasSelection);
            boxWelDetails.setVisible(hasSelection); boxWelDetails.setManaged(hasSelection);

            if (hasSelection) {
                lblDetailCode.setText(newSelection.getCode()); lblDetailOnderhoud.setText(newSelection.datumLaatsteOnderhoudProperty().get());
                lblDetailLocatie.setText(newSelection.locatieProperty().get()); lblDetailProduct.setText(newSelection.productProperty().get());

                String status = newSelection.statusProperty().get();
                lblDetailStatus.setText(capitalize(status)); zetKleur(lblDetailStatus, status);
            }
        });
    }

    private void zetKleur(Labeled component, String waarde) {
        if (waarde == null || waarde.trim().isEmpty()) { component.setStyle(""); return; }
        String upper = waarde.toUpperCase();
        if (upper.contains("GEZOND") || upper.contains("DRAAIT") || upper.contains("OPERATIONEEL")) {
            component.setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        } else if (upper.contains("FALEND") || upper.contains("NOOD") || upper.contains("ONDERHOUD")) {
            component.setStyle("-fx-text-fill: #ed6c02; -fx-font-weight: bold;");
        } else if (upper.contains("OFFLINE") || upper.contains("GESTOPT") || upper.contains("PROBLEMEN")) {
            component.setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
        } else { component.setStyle("-fx-font-weight: bold; -fx-text-fill: #333333;"); }
    }

    private Label boldLabel(String tekst) { Label lbl = new Label(tekst); lbl.setStyle("-fx-font-weight: bold;"); lbl.setMaxWidth(Double.MAX_VALUE); lbl.setAlignment(Pos.CENTER); return lbl; }
    private String capitalize(String s) { return s == null || s.isEmpty() ? s : s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase().replace("_", " "); }
}