package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "machines")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Machine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", unique = true)
    private String code;

    @ManyToOne
    @JoinTable(
            name = "site_machines",
            joinColumns = @JoinColumn(name = "machineId"),
            inverseJoinColumns = @JoinColumn(name = "siteId")
    )
    private Site site;

    @Transient
    private String locatieInSite;

    @Column(name = "productinfo")
    private String productinfo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MachineStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "productieStatus")
    private ProductieStatus productieStatus;

    @Column(name = "upTime")
    private long uptimeMinuten;

    @Column(name = "datumLaatsteOnderhoud")
    private LocalDate datumLaatsteOnderhoud;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    private Machine(Builder builder){
        this.code = builder.code;
        this.site = builder.site;
        this.locatieInSite = builder.locatieInSite;
        this.productinfo = builder.productinfo;
        this.status = builder.status;
        this.productieStatus = builder.productieStatus;
        this.uptimeMinuten = builder.uptimeMinuten;
        this.datumLaatsteOnderhoud = builder.datumLaatsteOnderhoud;
        controleerOnderhoudStatus();
    }

    public void controleerOnderhoudStatus() {
        if (this.datumLaatsteOnderhoud != null) {
            long dagenSindsOnderhoud = ChronoUnit.DAYS.between(this.datumLaatsteOnderhoud, LocalDate.now());
            if (dagenSindsOnderhoud > 30 && this.status != MachineStatus.GESTOPT) {
                this.status = MachineStatus.NOOD_AAN_ONDERHOUD;
            }
        }
    }

    public void softDelete() {
        this.isDeleted = true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String code;
        private Site site;
        private String locatieInSite;
        private String productinfo;
        private MachineStatus status;
        private ProductieStatus productieStatus;
        private long uptimeMinuten;
        private LocalDate datumLaatsteOnderhoud;

        public Builder code(String code){ this.code = code; return this; }
        public Builder site(Site site){ this.site = site; return this; }
        public Builder locatieInSite(String locatie){ this.locatieInSite = locatie; return this; }
        public Builder productinfo(String productinfo){ this.productinfo = productinfo; return this; }
        public Builder status(MachineStatus status){ this.status = status; return this; }
        public Builder productieStatus(ProductieStatus productieStatus){ this.productieStatus = productieStatus; return this; }
        public Builder uptimeMinuten(long uptimeMinuten){ this.uptimeMinuten = uptimeMinuten; return this; }
        public Builder datumLaatsteOnderhoud(LocalDate datum){ this.datumLaatsteOnderhoud = datum; return this; }

        public Machine build(){
            return new Machine(this);
        }
    }
}