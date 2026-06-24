package domein;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.SiteElement;

import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name="sites")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Site {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name="locatie")
    private String locatie;

    @Column(name="capaciteit")
    private int capaciteit;

    @Column(name="land")
    private String land;

    @Enumerated(EnumType.STRING)
    @Column(name="operationeleStatus")
    private OperationeleStatus operationeleStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "siteProductieStatus")
    private SiteProductieStatus siteProductieStatus;

    @Column(name = "breedtegraad")
    private BigDecimal breedtegraad;

    @Column(name = "lengtegraad")
    private BigDecimal lengtegraad;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    @OneToMany(mappedBy = "site")
    private List<Team> teams;

    public void softDelete() {
        this.isDeleted = true;
    }

    private Site(Builder builder){
        this.name = builder.name;
        this.locatie = builder.locatie;
        this.capaciteit = builder.capaciteit;
        this.land = builder.land;
        this.operationeleStatus = builder.operationeleStatus;
        this.siteProductieStatus = builder.siteProductieStatus;
        this.breedtegraad = builder.breedtegraad;
        this.lengtegraad = builder.lengtegraad;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{
        private Map<SiteElement, String> errors;

        private String name;
        private String locatie;
        private int capaciteit;
        private String land;
        private OperationeleStatus operationeleStatus;
        private SiteProductieStatus siteProductieStatus;
        private BigDecimal breedtegraad;
        private BigDecimal lengtegraad;

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public Builder locatie(String locatie){
            this.locatie = locatie;
            return this;
        }

        public Builder capaciteit(Integer capaciteit){
            this.capaciteit = capaciteit;
            return this;
        }

        public Builder land(String land){
            this.land = land;
            return this;
        }

        public Builder operationeleStatus(OperationeleStatus operationeleStatus){
            this.operationeleStatus = operationeleStatus;
            return this;
        }

        public Builder siteProductieStatus(SiteProductieStatus siteProductieStatus){
            this.siteProductieStatus = siteProductieStatus;
            return this;
        }

        public Builder breedtegraad(BigDecimal breedtegraad) {
            this.breedtegraad = breedtegraad;
            return this;
        }
        public Builder lengtegraad(BigDecimal lengtegraad) {
            this.lengtegraad = lengtegraad;
            return this;
        }

        public Site build(){
            return new Site(this);
        }
    }


    public Site(String name, String locatie, int capaciteit, String land,
                OperationeleStatus operationeleStatus, SiteProductieStatus siteProductieStatus,
                BigDecimal breedtegraad, BigDecimal lengtegraad) {
        this.name = name;
        this.locatie = locatie;
        this.capaciteit = capaciteit;
        this.land = land;
        this.operationeleStatus = operationeleStatus;
        this.siteProductieStatus = siteProductieStatus;
        this.breedtegraad       = breedtegraad;
        this.lengtegraad        = lengtegraad;
    }
}
