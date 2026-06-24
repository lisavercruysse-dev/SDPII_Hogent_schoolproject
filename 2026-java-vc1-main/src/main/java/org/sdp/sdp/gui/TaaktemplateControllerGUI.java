package org.sdp.sdp.gui;

import domein.EntityStatus;
import domein.OperationeleStatus;
import domein.TaakType;
import domein.TaaktemplateController;
import dto.TaaktemplateInputDTO;
import exception.TaaktemplateInformationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class TaaktemplateControllerGUI extends VBox implements FilterableTable{
    @FXML private TableView<ObservableTemplate> templatesTbl;
    @FXML private TableColumn<ObservableTemplate, String> typeCol, duurTijdCol, statusCol, omschrijvingCol;
    @FXML private TableColumn<ObservableTemplate, Void> btnCol;
    @FXML private Label typeError, omschrijvingError, duurTijdError, formTitel;
    @FXML private TextField duurTijdInput;
    @FXML private TextArea omschrijvingInput;
    @FXML private Button annuleerEditBtn, toevoegenBtn;
    @FXML private ComboBox<String> typeCombo;
    private TextField minDuurtijd, maxDuurtijd;
    private ObservableTemplate templateToEdit;
    private final Map<String, CheckBox> statusCheckboxes = new LinkedHashMap<>();
    private final TaaktemplateController templateController;
    private final ObservableTemplateTable observableTemplateTable;
    private final MainController mainController;
    private final Map<String, TextField> columnFilterFields = new LinkedHashMap<>();

    public TaaktemplateControllerGUI(TaaktemplateController taaktemplateController, MainController mainController) {
        templateController = taaktemplateController;
        this.mainController = mainController;
        this.observableTemplateTable = new ObservableTemplateTable(taaktemplateController);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/TaakTemplatesFrame.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @FXML
    private void initialize() {
        typeCol.setCellValueFactory(cellData -> cellData.getValue().typeProperty());
        duurTijdCol.setCellValueFactory(cellData -> cellData.getValue().duurTijdProperty());
        omschrijvingCol.setCellValueFactory(cellData -> cellData.getValue().omschrijvingProperty());
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

        btnCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button();
            private final Button editBtn = new Button();
            private final HBox buttons = new HBox(8, editBtn, deleteBtn);
            {
                ImageView deleteIcon = new ImageView(trashImage);
                deleteIcon.setFitWidth(20);
                deleteIcon.setPreserveRatio(true);
                deleteIcon.setSmooth(false);
                deleteBtn.setGraphic(deleteIcon);
                deleteBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                deleteBtn.setOnAction(e -> {
                    ObservableTemplate template = getTableView().getItems().get(getIndex());
                    onDeleteTemplateAction(template);
                });

                ImageView editIcon = new ImageView(editImage);
                editIcon.setFitWidth(20);
                editIcon.setPreserveRatio(true);
                editIcon.setSmooth(false);
                editBtn.setGraphic(editIcon);
                editBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-padding: 0; -fx-background-insets: 0;");
                editBtn.setOnAction(e -> {
                    ObservableTemplate template = getTableView().getItems().get(getIndex());
                    onEditTemplate(template);
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

        SortedList<ObservableTemplate> sortedList = new SortedList<>(observableTemplateTable.getFilteredTemplates());
        sortedList.comparatorProperty().bind(templatesTbl.comparatorProperty());
        templatesTbl.setItems(sortedList);

        columnFilterFields.put("type", setupTextFilter(typeCol, "type"));
        columnFilterFields.put("omschrijving", setupTextFilter(omschrijvingCol, "omschrijving"));
        setupCheckboxFilter(
                statusCol, "status",
                Arrays.stream(OperationeleStatus.values()).map(Enum::name).toList(),
                statusCheckboxes);
        List<TextField> range = setupRangeFilter(duurTijdCol, "duurTijd");
        minDuurtijd = range.get(0);
        maxDuurtijd = range.get(1);

        typeCol.setSortType(TableColumn.SortType.ASCENDING);
        templatesTbl.getSortOrder().add(typeCol);

        Label placeholder = new Label("Geen templates gevonden");
        placeholder.setStyle("-fx-text-fill: #999999; -fx-font-size: 13;");
        templatesTbl.setPlaceholder(placeholder);

        annuleerEditBtn.setManaged(false);
        annuleerEditBtn.setVisible(false);

        ObservableList<String> allTypes = FXCollections.observableArrayList(
                Arrays.stream(TaakType.values()).map(t -> t.name().toLowerCase()).toList()
        );
        typeCombo.setItems(allTypes);
    }

    @Override
    public void updateFilter() {
        String type = columnFilterFields.getOrDefault("type", new TextField()).getText();
        String omschrijving = columnFilterFields.getOrDefault("omschrijving", new TextField()).getText();

        Integer min = parseIntOrNull(minDuurtijd.getText());
        Integer max = parseIntOrNull(maxDuurtijd.getText());

        Set<String> toegestaneStatussen = statusCheckboxes.values().stream().noneMatch(CheckBox::isSelected) ?
                Arrays.stream(EntityStatus.values()).map(Enum::name).collect(Collectors.toSet()) :
                statusCheckboxes.entrySet().stream().filter(e -> e.getValue().isSelected())
                        .map(Map.Entry::getKey).collect(Collectors.toSet());

        observableTemplateTable.changeFilter(type, omschrijving, min, max, toegestaneStatussen);
    }

    private Integer parseIntOrNull(String tekst) {
        try { return (tekst == null || tekst.isBlank()) ? null : Integer.parseInt(tekst.trim()); }
        catch (NumberFormatException e) { return null; }
    }


    @Override
    public TableView<?> getTable() {return templatesTbl;}

    @FXML
    private void resetFilters() {
        columnFilterFields.values().forEach(TextInputControl::clear);
        maxDuurtijd.clear();
        minDuurtijd.clear();
        statusCheckboxes.values().forEach(cb -> cb.setSelected(true));
    }

    private void setEdit(ObservableTemplate template) {
        templateToEdit = template;

        emptyErrors();
        formTitel.setText("Template Bewerken");


        duurTijdInput.setText(template.duurTijdProperty().getValue());
        typeCombo.setValue(template.typeProperty().getValue());
        omschrijvingInput.setText(template.omschrijvingProperty().getValue());

        annuleerEditBtn.setVisible(true);
        annuleerEditBtn.setManaged(true);
        toevoegenBtn.setText("Bewerken");
    }

    private void setToevoegen() {
        templateToEdit = null;
        emptyErrors();
        formTitel.setText("Template Toevoegen");

        annuleerEditBtn.setVisible(false);
        annuleerEditBtn.setManaged(false);
        toevoegenBtn.setText("Toevoegen");

        emptyInput();
    }

    public void onToevoegen() {
        emptyErrors();

        TaakType type = TaakType.valueOf(typeCombo.getSelectionModel().getSelectedItem().toUpperCase());
        Integer duurTijd;
        String omschrijving = omschrijvingInput.getText();
        try {
            duurTijd = Integer.parseInt(duurTijdInput.getText());
        } catch (NumberFormatException e) {
            duurTijd = null;
        }

        try {
            if (templateToEdit != null) {
                observableTemplateTable.saveTemplate(new TaaktemplateInputDTO(templateToEdit.getId(), type, omschrijving, duurTijd));
            } else {
                observableTemplateTable.saveTemplate(new TaaktemplateInputDTO(null, type, omschrijving, duurTijd));
            }
            setToevoegen();
            emptyInput();
        } catch (TaaktemplateInformationException ex) {
            handleErrors(ex);
        }
    }

    private void onEditTemplate(ObservableTemplate template) {
        setEdit(template);
    }

    public void onAnnuleerEdit() {
        setToevoegen();
    }

    private void handleErrors(TaaktemplateInformationException ex) {
        ex.getErrors().forEach((el, message) -> {
            switch (el) {
                case TYPE -> typeError.setText(message);
                case DUURTIJD ->  duurTijdError.setText(message);
                case OMSCHRIJVING -> omschrijvingError.setText(message);
            }
        });
    }

    private void emptyInput() {
        typeCombo.getSelectionModel().clearSelection();
        duurTijdInput.setText("");
        omschrijvingInput.setText("");
    }

    private void emptyErrors(){
        typeError.setText("");
        omschrijvingError.setText("");
        duurTijdError.setText("");
    }

    private void onDeleteTemplateAction(ObservableTemplate template) {
        mainController.showPopup(new VerwijderController(mainController,
                "Verwijder template",
                "Weet je zeker dat je dit template wilt verwijderen? Deze actie kan niet ongedaan gemaakt worden.",
                () -> observableTemplateTable.removeTemplate(template)));
    }
}
