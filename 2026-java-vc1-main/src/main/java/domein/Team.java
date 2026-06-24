package domein;

import exception.TeamInformationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.TeamElement;

import java.util.*;

@Entity
@Table(name="teams")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    @Setter(AccessLevel.NONE)
    private int id;

    @Column(name = "NAAM")
    private String naam;

    @ManyToOne
    @JoinColumn(name = "verantwoordelijkeId", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Werknemer verantwoordelijke;

    //site

    @ManyToMany(mappedBy = "teams")
    private List<Werknemer> werknemers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "SITE_ID")
    private Site site;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EntityStatus status;

    @Transient
    Map<TeamElement, String> errors = new HashMap<>();

    public Team(Werknemer verantwoordelijke, String naam, Site site) {
        setNaam(naam);
        setVerantwoordelijke(verantwoordelijke);
        setSite(site);
        this.status = EntityStatus.ACTIEF;

        if (!errors.isEmpty()) throw new TeamInformationException(errors);
    }

    public void setNaam(String naam) {
        if (naam == null || naam.isBlank()) {
            errors.put(TeamElement.NAAM, "Naam is verplicht");
        } else if (naam.trim().length() < 2) {
            errors.put(TeamElement.NAAM, "Naam moet minstens 2 karakters zijn");
        } else {
            this.naam = naam;
        }
    }

    public void setVerantwoordelijke(Werknemer verantwoordelijke) {
        if (verantwoordelijke == null) {
            errors.put(TeamElement.VERANTWOORDELIJKE, "Verantwoordelijke is verplicht");
        } else {
            this.verantwoordelijke = verantwoordelijke;
        }
    }

    public void setSite(Site site) {
        if (site == null) {
            errors.put(TeamElement.SITE, "Site is verplicht");
        } else {
            this.site = site;
        }
    }

}
