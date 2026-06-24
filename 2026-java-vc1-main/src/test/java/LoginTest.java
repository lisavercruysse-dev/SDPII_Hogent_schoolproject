import de.mkammerer.argon2.Argon2Factory;
import domein.*;
import exception.WerknemerInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoginTest {

    @Mock
    private GebruikerDao werknemerRepo;

    @InjectMocks
    private WerknemerManager werknemerManager;

    private static final LocalDate GEBOORTEDATUM = LocalDate.of(1990, 1, 1);
    private static final String PLAIN_WACHTWOORD = "12345678";

    private Werknemer manager;
    private Werknemer verantwoordelijke;
    private Werknemer administrator;
    private Werknemer werknemer;
    private Werknemer inactiefManager;
    private Werknemer inactiefVerantwoordelijke;
    private Werknemer inactiefAdministrator;
    private Werknemer inactiefWerknemer;

    @BeforeEach
    public void setUp() throws WerknemerInformationException {
        manager = maakWerknemer("Jan", "Janssens", JobTitel.MANAGER, "ACTIEF");
        verantwoordelijke = maakWerknemer("Pieter", "De Bakker", JobTitel.VERANTWOORDELIJKE, "ACTIEF");
        administrator = maakWerknemer("Sara", "Stevens", JobTitel.ADMINISTRATOR, "ACTIEF");
        werknemer = maakWerknemer("Tom", "Claes", JobTitel.WERKNEMER, "ACTIEF");
        inactiefManager = maakWerknemer("Jan", "Janssens", JobTitel.MANAGER, "INACTIEF");
        inactiefVerantwoordelijke = maakWerknemer("Pieter", "De Bakker", JobTitel.VERANTWOORDELIJKE, "INACTIEF");
        inactiefAdministrator = maakWerknemer("Sara", "Stevens", JobTitel.ADMINISTRATOR, "INACTIEF");
        inactiefWerknemer = maakWerknemer("An", "Peeters", JobTitel.WERKNEMER, "INACTIEF");
    }

    // ─── Succesvol inloggen ───────────────────────────────────────────────────

    @Test
    public void manager_kanInloggen() {
        when(werknemerRepo.findByEmail(manager.getEmail())).thenReturn(Optional.of(manager));
        assertDoesNotThrow(() -> werknemerManager.login(manager.getEmail(), PLAIN_WACHTWOORD));
    }

    @Test
    public void verantwoordelijke_kanInloggen() {
        when(werknemerRepo.findByEmail(verantwoordelijke.getEmail())).thenReturn(Optional.of(verantwoordelijke));
        assertDoesNotThrow(() -> werknemerManager.login(verantwoordelijke.getEmail(), PLAIN_WACHTWOORD));
    }

    @Test
    public void administrator_kanInloggen() {
        when(werknemerRepo.findByEmail(administrator.getEmail())).thenReturn(Optional.of(administrator));
        assertDoesNotThrow(() -> werknemerManager.login(administrator.getEmail(), PLAIN_WACHTWOORD));
    }

    // ─── Geen toegang ─────────────────────────────────────────────────────────

    @Test
    public void werknemer_kanNietInloggen_geenToegang() {
        when(werknemerRepo.findByEmail(werknemer.getEmail())).thenReturn(Optional.of(werknemer));
        assertThrows(IllegalAccessException.class,
                () -> werknemerManager.login(werknemer.getEmail(), PLAIN_WACHTWOORD));
    }

    // ─── Inactief account ─────────────────────────────────────────────────────

    @Test
    public void inactiefWerknemer_kanNietInloggen() {
        when(werknemerRepo.findByEmail(inactiefWerknemer.getEmail())).thenReturn(Optional.of(inactiefWerknemer));
        assertThrows(IllegalStateException.class,
                () -> werknemerManager.login(inactiefWerknemer.getEmail(), PLAIN_WACHTWOORD));
    }

    @Test
    public void inactiefVerantwoordelijke_kanNietInloggen() {
        when(werknemerRepo.findByEmail(inactiefVerantwoordelijke.getEmail())).thenReturn(Optional.of(inactiefVerantwoordelijke));
        assertThrows(IllegalStateException.class,
                () -> werknemerManager.login(inactiefVerantwoordelijke.getEmail(), PLAIN_WACHTWOORD));
    }

    @Test
    public void inactiefManager_kanNietInloggen() {
        when(werknemerRepo.findByEmail(inactiefManager.getEmail())).thenReturn(Optional.of(inactiefManager));
        assertThrows(IllegalStateException.class,
                () -> werknemerManager.login(inactiefManager.getEmail(), PLAIN_WACHTWOORD));
    }

    @Test
    public void inactiefAdministrator_kanNietInloggen() {
        when(werknemerRepo.findByEmail(inactiefAdministrator.getEmail())).thenReturn(Optional.of(inactiefAdministrator));
        assertThrows(IllegalStateException.class,
                () -> werknemerManager.login(inactiefAdministrator.getEmail(), PLAIN_WACHTWOORD));
    }
    // ─── Ongeldig e-mailadres ─────────────────────────────────────────────────

    @Test
    public void onbekendEmail_gooitException() {
        when(werknemerRepo.findByEmail("onbekend@example.com")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> werknemerManager.login("onbekend@example.com", PLAIN_WACHTWOORD));
    }

    @Test
    public void leegEmail_gooitException() {
        when(werknemerRepo.findByEmail("")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> werknemerManager.login("", PLAIN_WACHTWOORD));
    }

    // ─── Ongeldig wachtwoord ──────────────────────────────────────────────────

    @Test
    public void foutWachtwoord_gooitException() {
        when(werknemerRepo.findByEmail(manager.getEmail())).thenReturn(Optional.of(manager));
        assertThrows(IllegalArgumentException.class,
                () -> werknemerManager.login(manager.getEmail(), "FoutWachtwoord!"));
    }

    @Test
    public void leegWachtwoord_gooitException() {
        when(werknemerRepo.findByEmail(manager.getEmail())).thenReturn(Optional.of(manager));
        assertThrows(IllegalArgumentException.class,
                () -> werknemerManager.login(manager.getEmail(), ""));
    }

    // ─── Hulpmethode ──────────────────────────────────────────────────────────

    private Werknemer maakWerknemer(String voornaam, String achternaam,
                                    JobTitel jobTitel, String status)
            throws WerknemerInformationException {

        var builder = Werknemer.builder()
                .voornaam(voornaam)
                .achternaam(achternaam)
                .jobTitel(jobTitel)
                .geboortedatum(GEBOORTEDATUM)
                .land("België")
                .postcode("9000")
                .stad("Gent")
                .straat("Vlaanderenstraat")
                .huisnummer(12);

        if (jobTitel == JobTitel.WERKNEMER) {
            builder.telefoon("0412345678");
        }

        Werknemer w = builder.build();
        // Overschrijf het gegenereerde wachtwoord met een gekende hash
        w.setWachtwoord(hashWachtwoord(PLAIN_WACHTWOORD));
        w.setStatus(EntityStatus.valueOf(status));
        return w;
    }

    private String hashWachtwoord(String plain) {
        var argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
        return argon2.hash(2, 65536, 4, plain.toCharArray());
    }
}