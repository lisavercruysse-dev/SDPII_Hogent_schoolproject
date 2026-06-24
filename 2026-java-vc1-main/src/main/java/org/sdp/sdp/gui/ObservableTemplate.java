package org.sdp.sdp.gui;

import dto.TaaktemplateDTO;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Getter;

public class ObservableTemplate {
    @Getter
    private final int id;
    private final StringProperty type;
    private final StringProperty omschrijving;
    private final StringProperty duurTijd;
    private final StringProperty statusProperty;
    @Getter
    private final TaaktemplateDTO template;

    public ObservableTemplate(TaaktemplateDTO template){
        id = template.id();
        type = new SimpleStringProperty(template.type().toString().toLowerCase());
        omschrijving = new SimpleStringProperty(template.omschrijving());
        duurTijd = new SimpleStringProperty(Integer.toString(template.duurTijd()));
        statusProperty = new SimpleStringProperty(template.status().toString().toLowerCase());
        this.template = template;
    }

    public StringProperty typeProperty(){return type;}
    public StringProperty duurTijdProperty(){return duurTijd;}
    public StringProperty omschrijvingProperty(){return omschrijving;}
    public StringProperty statusProperty(){return statusProperty;}

    public void update(TaaktemplateDTO dto) {
        type.set(dto.type().toString().toLowerCase());
        omschrijving.set(dto.omschrijving());
        duurTijd.set(String.valueOf(dto.duurTijd()));
        statusProperty.set(dto.status().toString().toLowerCase());
    }
}
