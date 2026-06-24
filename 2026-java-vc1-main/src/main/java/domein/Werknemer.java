package domein;

import de.mkammerer.argon2.Argon2Factory;
import exception.WerknemerInformationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import util.WerknemerElement;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;

@Entity
@Table(name="werknemers")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter @Setter
public class Werknemer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name="firstName")
    private String voornaam;

    @Column(name="lastName")
    private String achternaam;

    @Enumerated(EnumType.STRING)
    @Column(name = "jobTitel")
    private JobTitel jobTitel;

    @Column(name="email")
    private String email;

    @Column(name="password_hash")
    private String wachtwoord;

    @Column(name="telefoon")
    private String telefoon;

    @Column(name="geboortedatum")
    private LocalDate geboortedatum;

    @Column(name="land")
    private String land;

    @Column(name="postcode")
    private String postcode;

    @Column(name="stad")
    private String stad;

    @Column(name="straat")
    private String straat;

    @Column(name="huisnummer")
    private Integer huisnummer;

    @Column(name="bus")
    private Integer bus;

    @Enumerated(EnumType.STRING)
    @Column(name="status")
    private EntityStatus status;

    @ManyToMany()
    @JoinTable(
            name = "team_werknemers",
            joinColumns = @JoinColumn(name = "werknemerId", referencedColumnName = "id", columnDefinition = "INT UNSIGNED"),
            inverseJoinColumns = @JoinColumn(name = "teamId", referencedColumnName = "ID", columnDefinition = "INT UNSIGNED")
    )
    private List<Team> teams = new ArrayList<>();

    private Werknemer(Builder builder) {
        this.voornaam = builder.voornaam;
        this.achternaam = builder.achternaam;
        this.jobTitel = builder.jobTitel;
        this.email = builder.email;
        this.wachtwoord = builder.wachtwoord;
        this.telefoon = builder.telefoon;
        this.geboortedatum = builder.geboortedatum;
        this.land = builder.land;
        this.postcode = builder.postcode;
        this.stad = builder.stad;
        this.straat = builder.straat;
        this.huisnummer = builder.huisnummer;
        this.bus = builder.bus;
        this.status = builder.status;
        if (builder.team != null) {
            voegTeamToe(builder.team);
        }
    }

    public void voegTeamToe(Team team) {
        this.teams.add(team);
    }

    public void verwijderUitTeam(Team team) {
        this.teams.remove(team);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Map<WerknemerElement, String> errors;

        Werknemer existing;
        private String voornaam;
        private String achternaam;
        private JobTitel jobTitel;
        private String email;
        private String wachtwoord;
        private String telefoon;
        private LocalDate geboortedatum;
        private String land;
        private String postcode;
        private String stad;
        private String straat;
        private Integer huisnummer;
        private Integer bus;
        private EntityStatus status;
        private Team team;

        public Builder voornaam(String voornaam) {
            this.voornaam = voornaam;
            return this;
        }

        public Builder achternaam(String achternaam) {
            this.achternaam = achternaam;
            return this;
        }

        public Builder jobTitel(JobTitel jobTitel) {
            this.jobTitel = jobTitel;
            return this;
        }

        public Builder telefoon(String telefoon) {
            this.telefoon = telefoon == null ? null : telefoon.replaceAll("\\s+", "");
            return this;
        }

       public Builder geboortedatum(LocalDate geboortedatum) {
            this.geboortedatum = geboortedatum;
            return this;
       }

       public Builder land(String land) {
            this.land = land;
            return this;
       }

       public Builder postcode(String postcode) {
            this.postcode = postcode;
            return this;
       }

       public Builder stad(String stad) {
            this.stad = stad;
            return this;
       }

       public Builder straat(String straat) {
            this.straat = straat;
            return this;
       }

       public Builder huisnummer(Integer huisnummer) {
            this.huisnummer = huisnummer;
            return this;
       }

       public Builder bus(Integer bus) {
            this.bus = bus;
            return this;
       }

       public Builder team(Team team) {
            this.team = team;
            return this;
       }

       public Builder existing(Werknemer werknemer) {
            this.existing = werknemer;
            return this;
       }

       private String generatePassword () {
            String pool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
            SecureRandom random = new SecureRandom();
            StringBuilder password = new StringBuilder();

            for (int i = 0; i<8; i++) {
                password.append(pool.charAt(random.nextInt(pool.length())));
            }

            return password.toString();
       }

       public Werknemer build() throws WerknemerInformationException {
            errors = new HashMap<>();

            if (voornaam == null || voornaam.isBlank()) {
                errors.put(WerknemerElement.VOORNAAM, "Voornaam is verplicht");
            } else if (voornaam.length() < 2 || !voornaam.matches("^[A-Za-z]+$")) {
                errors.put(WerknemerElement.VOORNAAM, "minimaal 2 letters, geen speciale tekens");
            }
            if (achternaam == null || achternaam.isBlank()) {
                errors.put(WerknemerElement.ACHTERNAAM, "Achternaam is verplicht");
            } else if (achternaam.length() < 2 || !achternaam.matches("^[A-Za-zÀ-ÿ]([A-Za-zÀ-ÿ]| [A-Za-zÀ-ÿ])*$")) {
                errors.put(WerknemerElement.ACHTERNAAM, "minimaal 2 letters, geen speciale tekens");
            }
           if (jobTitel == null) {
               errors.put(WerknemerElement.JOBTITEL, "JobTitel is verplicht");
           } else if (jobTitel == JobTitel.WERKNEMER) {
               if (telefoon == null || telefoon.isBlank()) {
                   errors.put(WerknemerElement.TELEFOON, "Telefoonnummer is verplicht voor werknemers");
               }
           }
           if (telefoon != null && !telefoon.isBlank() && !telefoon.matches("^\\+?[0-9]{7,15}$")) {
               errors.put(WerknemerElement.TELEFOON, "Ongeldig telefoonnummer");
           }
           if (geboortedatum == null) {
               errors.put(WerknemerElement.GEBOORTEDATUM, "Geboortedatum is verplicht");
           } else if (geboortedatum.isAfter(LocalDate.now())) {
               errors.put(WerknemerElement.GEBOORTEDATUM, "Geboortedatum mag niet in de toekomst liggen");
           } else if (geboortedatum.isAfter(LocalDate.now().minusYears(16))) {
               errors.put(WerknemerElement.GEBOORTEDATUM, "Werknemer moet minstens 16 jaar zijn");
           }
           if (land == null || land.isBlank()) {
               errors.put(WerknemerElement.LAND, "Land is verplicht");
           } else if (!land.matches("^(?=.*[A-Za-zÀ-ÿ])[A-Za-zÀ-ÿ\\-'. ]{2,100}$")) {
               errors.put(WerknemerElement.LAND, "Land moet minstens uit 2 tekens bestaan en moet minstens 2 letter bevatten");
           }
           if (postcode == null || postcode.isBlank()) {
               errors.put(WerknemerElement.POSTCODE, "Postcode is verplicht");
           } else if (!postcode.matches("^[A-Za-z0-9 -]{3,10}$")) {
               errors.put(WerknemerElement.POSTCODE, "Ongeldige postcode");
           }
           if (stad == null || stad.isBlank()) {
               errors.put(WerknemerElement.STAD, "Stad is verplicht");
           } else if (!stad.matches("^(?=.*[A-Za-zÀ-ÿ])[A-Za-zÀ-ÿ0-9\\-'. ]{1,100}$")) {
               errors.put(WerknemerElement.STAD, "Stad moet minstens 1 letter bevatten");
           }
           if (straat == null || straat.isBlank()) {
               errors.put(WerknemerElement.STRAAT, "Straat is verplicht");
           } else if (!straat.matches("^(?=.*[A-Za-zÀ-ÿ])[A-Za-zÀ-ÿ0-9\\-'. ]{1,100}$")) {
               errors.put(WerknemerElement.STRAAT, "Straat moet minstens 1 letter bevatten");
           }
           if (huisnummer == null) {
               errors.put(WerknemerElement.HUISNUMMER, "huisnummer is verplicht");
           } else if (huisnummer <= 0) {
               errors.put(WerknemerElement.HUISNUMMER, "huisnummer moet groter dan 0 zijn");
           }
           if (bus != null) {
               if  (bus <= 0) {
                   errors.put(WerknemerElement.BUS, "Bus moet groter dan 0 zijn");
               }
           }

           if (!errors.isEmpty()) {
               throw new WerknemerInformationException(errors);
           }

           this.email = String.format("%s.%s@example.com", voornaam, achternaam.replaceAll("\\s+", ""));

           //TODO: toon wachtwoord aan werknemer (via email?)
           if (existing == null) {
               String plainWachtwoord = generatePassword();
               System.out.println("Tijdelijk wachtwoord: " + plainWachtwoord);
               var argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
               this.wachtwoord = argon2.hash(2, 65536, 4, plainWachtwoord.toCharArray());

               this.status = EntityStatus.ACTIEF;

               return new Werknemer(this);
           } else {
               existing.setVoornaam(voornaam);
               existing.setAchternaam(achternaam);
               existing.setJobTitel(jobTitel);
               existing.setTelefoon(telefoon);
               existing.setGeboortedatum(geboortedatum);
               existing.setLand(land);
               existing.setPostcode(postcode);
               existing.setStad(stad);
               existing.setStraat(straat);
               existing.setHuisnummer(huisnummer);
               existing.setBus(bus);
               return existing;
           }


       }
    }
}
