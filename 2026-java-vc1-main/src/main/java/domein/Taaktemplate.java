package domein;

import exception.TaaktemplateInformationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.TaaktemplateElement;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "taakTemplates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Taaktemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    TaakType type;

    @Column(name = "omschrijving")
    String omschrijving;

    @Column(name = "duurTijd")
    Integer duurTijd;

    @Setter()
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    EntityStatus status;

    @Transient
    Map<TaaktemplateElement, String> errors = new HashMap<>();

    public Taaktemplate(TaakType type, String omschrijving, Integer duurTijd) {
        setType(type);
        setOmschrijving(omschrijving);
        setDuurTijd(duurTijd);
        setStatus(EntityStatus.ACTIEF);

        if (!errors.isEmpty()) {
            throw new TaaktemplateInformationException(errors);
        }
    }

    public void setOmschrijving(String omschrijving) {
        if (omschrijving == null || omschrijving.isEmpty()) {
            errors.put(TaaktemplateElement.OMSCHRIJVING, "Omschrijving is verplicht");
        } else if (omschrijving.length() < 5) {
            errors.put(TaaktemplateElement.OMSCHRIJVING, "Omschrijving moet uit minstens 5 karakters bestaan");
        } else if (omschrijving.length() > 50) {
            errors.put(TaaktemplateElement.OMSCHRIJVING, "Omschrijving mag niet langer dan 255 karakters zijn");
        } else {
            this.omschrijving = omschrijving;
        }
    }

    public void setType(TaakType type) {
        if (type == null) {
            errors.put(TaaktemplateElement.TYPE, "Type is verplicht");
        } else {
            this.type = type;
        }
    }

    public void setDuurTijd(Integer duurTijd) {
        if (duurTijd == null) {
            errors.put(TaaktemplateElement.DUURTIJD, "Duurtijd is verplicht");

        }
        else if (duurTijd < 15) {
            errors.put(TaaktemplateElement.DUURTIJD, "Duurtijd moet minstens 15 minuten zijn");
        }
        else if (duurTijd > 240) {
            errors.put(TaaktemplateElement.DUURTIJD, "Duurtijd mag niet langer dan 240 minuten zijn");
        }
        else if (duurTijd % 15 != 0) {
            errors.put(TaaktemplateElement.DUURTIJD, "Duurtijd moet uit blokken van 15 minuten bestaan");
        } else {
            this.duurTijd = duurTijd;
        }
    }
}
