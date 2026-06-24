package org.sdp.sdp.gui;

import domein.SiteController;
import domein.WerknemerController;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.WerknemerDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.util.List;

public class ObservableTeam {
    @Getter
    private final int id;
    private final StringProperty naam;
    private final StringProperty verantwoordelijke;
    private final StringProperty site;
    private final StringProperty status;
    private final IntegerProperty aantalWerknemers;
    private final WerknemerController werknemerController;
    private final SiteController siteController;
    @Getter
    private final TeamDTO team;
    private final WerknemerDTO verantwoordelijkeDTO;
    private final List<WerknemerDTO> werknemers;
    private final SiteDTO siteDTO;

    public ObservableTeam(TeamDTO team, WerknemerController werknemerController, SiteController siteController) {
        this.team = team;
        this.id = team.id();
        this.verantwoordelijkeDTO = werknemerController.getVerantwoordelijkeVanTeam(team.id());
        this.werknemerController = werknemerController;
        this.siteController = siteController;
        this.werknemers = werknemerController.getWerknemersFromTeam(team.id());
        this.siteDTO = siteController.getSiteFromTeam(team.id());
        this.naam = new SimpleStringProperty(team.naam());
        this.verantwoordelijke = new SimpleStringProperty(verantwoordelijkeDTO.voornaam() + " " + verantwoordelijkeDTO.achternaam());
        this.site = new SimpleStringProperty(siteDTO.name());
        this.aantalWerknemers = new SimpleIntegerProperty(werknemers.size());
        this.status = new SimpleStringProperty(team.status().toString().toLowerCase());
    }

    public StringProperty naamProperty() {return naam;}
    public StringProperty verantwoordelijkeProperty() {return verantwoordelijke;}
    public StringProperty siteProperty() {return site;}
    public IntegerProperty aantalWerknemersProperty() {return aantalWerknemers;}
    public StringProperty statusProperty() {return status;}

    public void update(TeamDTO dto) {
        WerknemerDTO updatedVerantwoordelijke = werknemerController.getVerantwoordelijkeVanTeam(id);
        SiteDTO updatedSite = siteController.getSiteFromTeam(id);
        naam.set(dto.naam());
        verantwoordelijke.set(updatedVerantwoordelijke.voornaam() + " " + updatedVerantwoordelijke.achternaam());
        site.set(updatedSite.name());
        aantalWerknemers.set(werknemerController.getWerknemersFromTeam(id).size());
        status.set(dto.status().toString().toLowerCase());
    }

    public void update() {
        aantalWerknemers.set(werknemerController.getWerknemersFromTeam(id).size());
    }
}
