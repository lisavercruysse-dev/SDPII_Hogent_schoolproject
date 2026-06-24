package org.sdp.sdp.gui;

import dto.MachineDTO;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ObservableMachine {

    @Getter
    private final int id;
    private final StringProperty code;
    private final StringProperty siteNaam;
    private final StringProperty datumLaatsteOnderhoud;
    private final StringProperty productieStatus;
    private final StringProperty status;
    private final StringProperty locatieInSite;
    private final StringProperty productinfo;

    @Getter
    private final LocalDate ruweDatumLaatsteOnderhoud;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ObservableMachine(MachineDTO machine) {
        this.id = machine.id();
        this.code = new SimpleStringProperty(machine.code());
        this.siteNaam = new SimpleStringProperty(machine.siteNaam());

        this.ruweDatumLaatsteOnderhoud = machine.datumLaatsteOnderhoud();
        String datum = machine.datumLaatsteOnderhoud() != null ? machine.datumLaatsteOnderhoud().format(FORMATTER) : "N/A";
        this.datumLaatsteOnderhoud = new SimpleStringProperty(datum);

        this.productieStatus = new SimpleStringProperty(machine.productieStatus());
        this.status = new SimpleStringProperty(machine.status());
        this.locatieInSite = new SimpleStringProperty(machine.locatieInSite() != null ? machine.locatieInSite() : "Geen locatie");
        this.productinfo = new SimpleStringProperty(machine.productinfo() != null ? machine.productinfo() : "Geen product");
    }

    public StringProperty codeProperty() { return code; }
    public StringProperty siteNaamProperty() { return siteNaam; }
    public StringProperty datumLaatsteOnderhoudProperty() { return datumLaatsteOnderhoud; }
    public StringProperty productieStatusProperty() { return productieStatus; }
    public StringProperty statusProperty() { return status; }
    public StringProperty locatieProperty() { return locatieInSite; }
    public StringProperty productProperty() { return productinfo; }

    public String getCode() { return code.get(); }
}