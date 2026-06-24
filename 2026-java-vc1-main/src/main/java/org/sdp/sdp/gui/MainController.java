package org.sdp.sdp.gui;

import domein.*;
import dto.WerknemerDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MainController {

    private final Map<ToggleButton, Supplier<Node>> navButtons = new HashMap<>();

    private WerknemerController werknemerController;
    private TeamController teamController;
    private SiteController siteController;
    private MeldingenController meldingenController;
    private MachineController machineController;
    private TaaktemplateController taaktemplateController;

    @FXML private HBox topBar;
    @FXML private Label lblNaam;
    @FXML private Label lblJobTitel;
    @FXML private Label lblInitialen;
    @FXML private Button btnUitloggen;

    @FXML public ToggleButton btnGebruikers;
    @FXML public ToggleButton btnMeldingen;
    @FXML public ToggleButton btnTeams;
    @FXML public ToggleButton btnSites;
    @FXML public ToggleButton btnMachines;
    @FXML public ToggleButton btnTaken;

    @FXML public StackPane popupContent;
    @FXML public VBox sidebarVBox;
    @FXML private ToggleGroup navGroup;
    @FXML private StackPane contentPane;

    @Getter
    private WerknemerDTO ingelogdeGebruiker;

    @FXML
    private void initialize() {
        werknemerController = new WerknemerController();
        teamController = new TeamController();
        siteController = new SiteController();
        machineController = new MachineController();
        taaktemplateController = new TaaktemplateController();

        toonLoginScherm();
    }

    // ─── Login ───────────────────────────────────────────────────────────────

    private void toonLoginScherm() {
        // Sidebar verbergen zolang niet ingelogd
        sidebarVBox.setVisible(false);
        sidebarVBox.setManaged(false);

        LoginSchermController loginScherm = new LoginSchermController(werknemerController);
        loginScherm.setOnLoginSuccess(this::handleLoginSuccess);
        contentPane.getChildren().setAll(loginScherm);
    }

    private void handleLoginSuccess(WerknemerDTO werknemer) {
        this.ingelogdeGebruiker = werknemer;
        this.meldingenController = new MeldingenController(werknemer.id());

        // Topbar vullen en tonen
        lblNaam.setText(werknemer.voornaam() + " " + werknemer.achternaam());
        lblJobTitel.setText(werknemer.jobTitel().substring(0,1).toUpperCase() + werknemer.jobTitel().substring(1).toLowerCase());
        lblInitialen.setText(
                String.valueOf(werknemer.voornaam().charAt(0)) +
                        String.valueOf(werknemer.achternaam().charAt(0))
        );
        topBar.setVisible(true);
        topBar.setManaged(true);

        // Sidebar tonen en configureren op basis van rol
        sidebarVBox.setVisible(true);
        sidebarVBox.setManaged(true);
        configureerSidebarOpRol(JobTitel.valueOf(werknemer.jobTitel()));
        registreerNavButtons();

        // Eerste zichtbare knop als standaard selecteren (nu met btnMachines erbij)
        List<ToggleButton> volgorde = List.of(
                btnGebruikers, btnTeams, btnSites, btnMachines, btnTaken, btnMeldingen
        );

        ToggleButton eersteKnop = volgorde.stream()
                .filter(btn -> btn.isVisible() && btn.isManaged())
                .findFirst()
                .orElse(null);
        setDefaultBtn(eersteKnop);
    }

    // ─── Rolgebaseerde sidebar ────────────────────────────────────────────────
    /**
     * Rechten per rol: (zie product backlog - Java)
     *
     * ADMIN           → Gebruikers, Logs, Meldingen
     * VERANTWOORDELIJKE → Teams, Meldingen, Machine, Taken
     * MANAGER         → Teams, Sites, Logs, Meldingen, Machine
     */
    private void configureerSidebarOpRol(JobTitel rol) {
        setKnopZichtbaar(btnGebruikers, RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.GEBRUIKERS));
        setKnopZichtbaar(btnTeams,      RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.TEAMS));
        setKnopZichtbaar(btnSites,      RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.SITES));
        setKnopZichtbaar(btnMeldingen,  RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.MELDINGEN));
        setKnopZichtbaar(btnMachines,   RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.MACHINES));
        setKnopZichtbaar(btnTaken,      RolAutorisatie.heeftToegang(rol, RolAutorisatie.NavItem.TAKEN));
    }

    private void setKnopZichtbaar(ToggleButton knop, boolean zichtbaar) {
        if (knop != null) {
            knop.setVisible(zichtbaar);
            knop.setManaged(zichtbaar);
        }
    }

    // ─── Uitloggen ────────────────────────────────────────────────────────────
    @FXML
    private void handleUitloggen() {
        // Reset state
        ingelogdeGebruiker = null;
        navButtons.clear();

        // Topbar en sidebar verbergen
        topBar.setVisible(false);
        topBar.setManaged(false);
        sidebarVBox.setVisible(false);
        sidebarVBox.setManaged(false);

        // Selectie wegnemen zodat listener niet vuurt
        navGroup.selectToggle(null);

        // Terug naar loginscherm
        toonLoginScherm();
    }

    // ─── Navigatie ────────────────────────────────────────────────────────────

    private void registreerNavButtons() {
        navButtons.clear();

        navButtons.put(btnGebruikers, () -> {
            GebruikersController c = new GebruikersController(this, werknemerController, teamController, siteController);
            return c;
        });
        navButtons.put(btnMeldingen, () -> {
            return loadMeldingen(meldingenController);
        });
        navButtons.put(btnTeams, () -> {
            TeamControllerGUI teamControllerGUI = new TeamControllerGUI(this, teamController, werknemerController, siteController, meldingenController);
            return teamControllerGUI;
        });
        navButtons.put(btnSites, () ->
                new SiteControllerGUI(this, siteController));

        navButtons.put(btnMachines, () ->
                new MachineControllerGUI(this, machineController, siteController, werknemerController, meldingenController));


        navButtons.put(btnTaken, () -> {
            TaaktemplateControllerGUI c = new TaaktemplateControllerGUI(taaktemplateController, this);
            return c;
        });

        navGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                Supplier<Node> factory = navButtons.get((ToggleButton) newToggle);
                if (factory != null) {
                    setContent(factory);
                }
            }
        });
    }

    private void setContent(Supplier<Node> factory) {
        contentPane.getChildren().clear();
        Node newContent = factory.get();
        contentPane.getChildren().add(newContent);
    }

    private Node loadMeldingen(MeldingenController domeinMeldingenController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org.sdp.sdp/gui/Meldingen.fxml"));
            Node node = loader.load();
            MeldingenControllerGUI controller = loader.getController();
            controller.setMainController(this);
            controller.setDomeinController(domeinMeldingenController);
            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Helper for plain FXML screens (no fx:root pattern)
    private Node loadFxml(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            Node node = loader.load();

            Object controller = loader.getController();
            if (controller instanceof CanPopup p) {
                p.setMainController(this);
            }

            return node;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDefaultBtn(ToggleButton defaultBtn) {
        if (defaultBtn != null) {
            defaultBtn.setSelected(true);
        }
    }

    public void showPopup(Node popup) {
        popupContent.setMouseTransparent(false);

        Pane background = new Pane();
        background.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
        background.prefWidthProperty().bind(popupContent.widthProperty());
        background.prefHeightProperty().bind(popupContent.heightProperty());

        StackPane wrapper = new StackPane(background, popup);
        StackPane.setAlignment(popup, Pos.CENTER);

        background.setOnMouseClicked(e -> closePopup());

        popupContent.getChildren().add(wrapper);
    }

    public void closePopup() {
        popupContent.getChildren().clear();
        popupContent.setMouseTransparent(true);
    }

    public void updateMeldingenCounter() {
        if (meldingenController != null) {
            long ongelezen = meldingenController.getAantalOngelezen();
        }
    }

}