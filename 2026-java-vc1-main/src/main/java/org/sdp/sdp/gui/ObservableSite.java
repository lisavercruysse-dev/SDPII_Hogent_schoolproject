package org.sdp.sdp.gui;

import domein.Site;
import dto.SiteDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class ObservableSite {

    @Getter
    private final int id;
    private final StringProperty name;
    private final StringProperty locatie;
    private final IntegerProperty capaciteit;
    private final StringProperty land;
    private final StringProperty operationeleStatus;
    private final StringProperty siteProductieStatus;
    private final StringProperty breedtegraad;
    private final StringProperty lengtegraad;

    public ObservableSite(SiteDTO site) {
        id = site.id();
        this.name = new SimpleStringProperty(site.name());
        this.locatie = new SimpleStringProperty(site.locatie());
        this.capaciteit = new SimpleIntegerProperty(site.capaciteit());
        this.land = new SimpleStringProperty(site.land());
        this.operationeleStatus = new SimpleStringProperty(site.operationeleStatus());
        this.siteProductieStatus = new SimpleStringProperty(site.siteProductieStatus());
        this.breedtegraad = new SimpleStringProperty(String.valueOf(site.breedtegraad()));
        this.lengtegraad = new SimpleStringProperty(String.valueOf(site.lengtegraad()));
    }

    public StringProperty nameProperty() { return name; }
    public StringProperty locatieProperty() { return locatie; }
    public IntegerProperty  capaciteitProperty() { return capaciteit; }
    public StringProperty landProperty() { return land; }
    public StringProperty operationeleStatusProperty() { return operationeleStatus; }
    public StringProperty productieStatusProperty() { return siteProductieStatus; }
    public StringProperty breedtegraadProperty() { return breedtegraad; }
    public StringProperty lengtegraadProperty() { return lengtegraad; }

    public String getName() {
        return name.get();
    }
    public String getCapaciteit() { return String.valueOf(capaciteit.get()); }

}
