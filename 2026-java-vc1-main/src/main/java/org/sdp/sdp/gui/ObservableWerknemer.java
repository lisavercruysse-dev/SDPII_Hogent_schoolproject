package org.sdp.sdp.gui;

import domein.Werknemer;
import dto.WerknemerDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class ObservableWerknemer {

    @Getter
    private final int id;
    private final StringProperty voornaam;
    private final StringProperty achternaam;
    private final StringProperty jobTitel;
    private final StringProperty email;
    private final StringProperty telefoon;
    private final StringProperty land;
    private final StringProperty postcode;
    private final StringProperty stad;
    private final StringProperty straat;
    private final StringProperty huisnr;
    private final StringProperty bus;
    private final StringProperty status;
    @Getter
    private final WerknemerDTO werknemer;

   /* @Getter
    private final Werknemer werknemer;*/ // koppeling naar domein

    public ObservableWerknemer(WerknemerDTO werknemer) {
        id = werknemer.id();
        this.werknemer = werknemer;
        this.voornaam = new SimpleStringProperty(werknemer.voornaam());
        this.achternaam = new SimpleStringProperty(werknemer.achternaam());
        this.jobTitel = new SimpleStringProperty(werknemer.jobTitel().toLowerCase());
        this.email = new SimpleStringProperty(werknemer.email());
        this.telefoon = new SimpleStringProperty(werknemer.telefoon());
        this.land = new SimpleStringProperty(werknemer.land());
        this.postcode = new SimpleStringProperty(werknemer.postcode());
        this.stad = new SimpleStringProperty(werknemer.stad());
        this.straat = new SimpleStringProperty(werknemer.straat());
        this.huisnr = new SimpleStringProperty(werknemer.huisnummer().toString());
        this.bus = new SimpleStringProperty();
        this.status = new SimpleStringProperty(werknemer.status().toString().toLowerCase());

        // wijzigingen in GUI doorgeven aan domein
        //this.voornaam.addListener((obs, oldVal, newVal) -> werknemer.setVoornaam(newVal));
        //this.achternaam.addListener((obs, oldVal, newVal) -> werknemer.setAchternaam(newVal));
    }

    public StringProperty firstNameProperty() { return voornaam; }
    public StringProperty lastNameProperty() { return achternaam; }
    public StringProperty jobTitelProperty() { return jobTitel; }
    public StringProperty emailProperty() { return email; }
    public StringProperty telefoonProperty() { return telefoon; }
    public StringProperty landProperty() { return land; }
    public StringProperty postcodeProperty() { return postcode; }
    public StringProperty stadProperty() { return stad; }
    public StringProperty straatProperty() { return straat; }
    public StringProperty huisnrProperty() { return huisnr; }
    public StringProperty busProperty() { return bus; }
    public StringProperty statusProperty() { return status; }

    public String getFirstName() {
        return voornaam.get();
    }
    public String getLastName() {
        return achternaam.get();
    }
    //public String getJobTitel() { return jobTitel.get(); }
    //public String getEmail() { return email.get(); }

    public void update(WerknemerDTO dto){
        voornaam.set(dto.voornaam());
        achternaam.set(dto.achternaam());
        jobTitel.set(dto.jobTitel().toLowerCase());
        telefoon.set(dto.telefoon());
        land.set(dto.land());
        postcode.set(dto.postcode());
        stad.set(dto.stad());
        straat.set(dto.straat());
        huisnr.set(dto.huisnummer().toString());
        bus.set(dto.bus() != null ? dto.bus().toString() : "");
        status.set(dto.status().toString().toLowerCase());
    }
}

