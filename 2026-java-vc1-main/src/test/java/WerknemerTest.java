import domein.*;
import dto.WerknemerDTO;
import dto.WerknemerInputDTO;
import exception.WerknemerInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WerknemerTest {

    @Mock private GebruikerDao werknemerRepo;
    private WerknemerManager werknemerManager;

    private static final String VOORNAAM = "Pieter";
    private static final String ACHTERNAAM = "De Bakker";
    private static final JobTitel JOBTITEL = JobTitel.WERKNEMER;
    private static final String TELEFOON = "0474521686";
    private static final LocalDate GEBOORTEDATUM = LocalDate.of(1989, 6, 8);
    private static final String LAND = "België";
    private static final String POSTCODE = "9000";
    private static final String STAD = "GENT";
    private static final String STRAAT = "Gentstraat";
    private static final int HUISNR = 12;
    private static final int BUS = 1;

    private static Werknemer VERANTWOORDELIJKE;
    private static Werknemer WERKNEMER;
    private static final Site SITE = new Site(
            "Site noord",
            "Gent",
            100,
            "België",
            OperationeleStatus.ACTIEF,
            SiteProductieStatus.GEZOND,
            new BigDecimal("51.0543"),
            new BigDecimal("4.0543"));
    private static Team TEAM;

    static Stream<Arguments> werknemerCorrecteWaarden() {
        return Stream.of(
                //alles
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //voornaam
                Arguments.of("An", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //achternaam
                Arguments.of(VOORNAAM, "Li", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Van den Berg", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "De Bakker", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Müller", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Björk", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Lefèvre", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //telefoon
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "0474523", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "012345678901234", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JobTitel.MANAGER, null, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JobTitel.VERANTWOORDELIJKE, null, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //geboortedatum
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, LocalDate.now().minusYears(16).minusDays(1), LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //land
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "België", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "United Kingdom", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "Côte d'Ivoire", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //postcode
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "1000", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "1234 AB", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "SW1A 1AA", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "A1A 1A1", STAD, STRAAT, HUISNR, BUS),
                //stad
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "Å", STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "Sint-Niklaas", STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "'s-Hertogenbosch", STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "Frankfurt am Main", STRAAT, HUISNR, BUS),
                //straat
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "Rue de l'Église", HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "Sint-Pietersnieuwstraat", HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "Av. João XXI", HUISNR, BUS),
                //huisnr
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, 1, BUS),
                //bus
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, 1),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, null)
        );
    }

    static Stream<Arguments> werknemerIncorrecteWaarden() {
        return Stream.of(
                //voornaam
                Arguments.of("", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of("      ", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(null, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of("A", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of("123456", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of("De Bakker", ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //achternaam
                Arguments.of(VOORNAAM, "", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "      ", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, null, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "a", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "123456", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "De  Bakker", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "De-Bakker", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "O'Brien", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Van  den Berg", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "De_Bakker", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, "Smith2", JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //telefoon
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "       ", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, null, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "123456", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, "1234567891234567", GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //geboortedatum
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, LocalDate.now().plusDays(1), LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, null, LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, LocalDate.now().minusYears(16).plusDays(1), LAND, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //land
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "          ", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, null, POSTCODE, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, "123456", POSTCODE, STAD, STRAAT, HUISNR, BUS),
                //postcode
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, null, STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "      ", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "AB", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "!@#$", STAD, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, "12345678901", STAD, STRAAT, HUISNR, BUS),
                //stad
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, null, STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "", STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "        ", STRAAT, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, "@#$%", STRAAT, HUISNR, BUS),
                //straat
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, null, HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "        ", HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "", HUISNR, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, "@#$%", HUISNR, BUS),
                //huisnr
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, 0, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, null, BUS),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, -5, BUS),
                //bus
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, -1),
                Arguments.of(VOORNAAM, ACHTERNAAM, JOBTITEL, TELEFOON, GEBOORTEDATUM, LAND, POSTCODE, STAD, STRAAT, HUISNR, 0)
        );
    }

    private WerknemerInputDTO werknemerInputDTO(Integer id) {
        return new  WerknemerInputDTO(
                id,
                VOORNAAM,
                ACHTERNAAM,
                JOBTITEL,
                TELEFOON,
                GEBOORTEDATUM,
                LAND,
                POSTCODE,
                STAD,
                STRAAT,
                HUISNR,
                BUS
        );
    }

    private WerknemerDTO werknemerDTO(Werknemer w) {
        return new   WerknemerDTO(
                w.getId(),
                w.getVoornaam(),
                w.getAchternaam(),
                w.getJobTitel().toString(),
                w.getTelefoon(),
                w.getGeboortedatum(),
                w.getLand(),
                w.getPostcode(),
                w.getStad(),
                w.getStraat(),
                w.getHuisnummer(),
                w.getBus(),
                w.getEmail(),
                w.getStatus()
        );
    }

    @BeforeEach
    void setUp() throws WerknemerInformationException {
        werknemerManager = new WerknemerManager(werknemerRepo);

        VERANTWOORDELIJKE = Werknemer.builder()
                .voornaam(VOORNAAM)
                .achternaam(ACHTERNAAM)
                .jobTitel(JobTitel.VERANTWOORDELIJKE)
                .geboortedatum(GEBOORTEDATUM)
                .land(LAND)
                .postcode(POSTCODE)
                .stad(STAD)
                .straat(STRAAT)
                .huisnummer(HUISNR)
                .bus(BUS)
                .build();

        WERKNEMER = Werknemer.builder()
                .voornaam(VOORNAAM)
                .achternaam(ACHTERNAAM)
                .jobTitel(JOBTITEL)
                .geboortedatum(GEBOORTEDATUM)
                .telefoon(TELEFOON)
                .land(LAND)
                .postcode(POSTCODE)
                .stad(STAD)
                .straat(STRAAT)
                .huisnummer(HUISNR)
                .bus(BUS)
                .build();

        TEAM = new Team(VERANTWOORDELIJKE, "Team A", SITE);
        lenient().when(werknemerRepo.get(anyInt())).thenReturn(VERANTWOORDELIJKE);
    }

    @Test
    void addWerknemer_transactionCommitted() throws WerknemerInformationException {
        werknemerManager.saveWerknemer(werknemerInputDTO(null));
        verify(werknemerRepo).startTransaction();
        verify(werknemerRepo).commitTransaction();
        verify(werknemerRepo, never()).rollbackTransaction();
    }

    @Test
    void addWerknemer_insertWordtAangeroepen() throws WerknemerInformationException {
        werknemerManager.saveWerknemer(werknemerInputDTO(null));
        verify(werknemerRepo, times(1)).insert(any(Werknemer.class));
    }

    @Test
    void addWerknemer_transactionRolledBackOnError()  throws WerknemerInformationException {
        doThrow(new RuntimeException()).when(werknemerRepo).insert(any());
        assertThrows(RuntimeException.class, () -> werknemerManager.saveWerknemer(werknemerInputDTO(null)));
        verify(werknemerRepo).rollbackTransaction();
        verify(werknemerRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @EnumSource(JobTitel.class)
    void addWerknemer_correcteJobtitel_maaktWerknemer(JobTitel jobTitel) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                null,
                VOORNAAM,
                ACHTERNAAM,
                jobTitel,
                TELEFOON,
                GEBOORTEDATUM,
                LAND,
                POSTCODE,
                STAD,
                STRAAT,
                HUISNR,
                BUS
        );

        Werknemer result = werknemerManager.saveWerknemer(dto);
        assertEquals(jobTitel, result.getJobTitel());
    }

    @ParameterizedTest
    @MethodSource("werknemerCorrecteWaarden")
    void addWerknemer_correcteWaarden_maaktWerknemer(String voornaam, String achternaam, JobTitel jobTitel, String telefoon,
                                                     LocalDate geboortedatum, String land, String postcode, String stad, String straat,
                                                     Integer huisnr, Integer bus) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                null,
                voornaam,
                achternaam,
                jobTitel,
                telefoon,
                geboortedatum,
                land,
                postcode,
                stad,
                straat,
                huisnr,
                bus
        );

        Werknemer result = werknemerManager.saveWerknemer(dto);
        assertEquals(voornaam, result.getVoornaam());
        assertEquals(achternaam, result.getAchternaam());
        assertEquals(jobTitel, result.getJobTitel());
        assertEquals(telefoon, result.getTelefoon());
        assertEquals(geboortedatum, result.getGeboortedatum());
        assertEquals(land, result.getLand());
        assertEquals(postcode, result.getPostcode());
        assertEquals(stad, result.getStad());
        assertEquals(straat, result.getStraat());
        assertEquals(huisnr, result.getHuisnummer());
        assertEquals(bus, result.getBus());
    }

    @ParameterizedTest
    @MethodSource("werknemerIncorrecteWaarden")
    void addWerknemer_incorrecteWaardenGooitException(String voornaam, String achternaam, JobTitel jobTitel, String telefoon,
                                                      LocalDate geboortedatum, String land, String postcode, String stad, String straat,
                                                      Integer huisnr, Integer bus) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                null,
                voornaam,
                achternaam,
                jobTitel,
                telefoon,
                geboortedatum,
                land,
                postcode,
                stad,
                straat,
                huisnr,
                bus
        );
        assertThrows(WerknemerInformationException.class, () -> werknemerManager.saveWerknemer(dto));
    }

    @Test
    void editWerknemer_roeptUpdateAan() throws WerknemerInformationException {
        werknemerManager.saveWerknemer(werknemerInputDTO(1));
        verify(werknemerRepo).update(VERANTWOORDELIJKE);
    }

    @Test
    void editWerknemer_transactionCommitted() throws WerknemerInformationException {
        werknemerManager.saveWerknemer(werknemerInputDTO(1));
        verify(werknemerRepo).startTransaction();
        verify(werknemerRepo).commitTransaction();
        verify(werknemerRepo, never()).rollbackTransaction();
    }

    @Test
    void editWerknemer_transactionRollbackOnError()  throws WerknemerInformationException {
        doThrow(new RuntimeException("DB fout")).when(werknemerRepo).update(any());
        assertThrows(RuntimeException.class, () -> werknemerManager.saveWerknemer(werknemerInputDTO(1)));
        verify(werknemerRepo).rollbackTransaction();
        verify(werknemerRepo, never()).commitTransaction();
    }

    @ParameterizedTest
    @EnumSource(JobTitel.class)
    void editWerknemer_correcteJobtitel_wijzigtJobtitel(JobTitel jobTitel) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                1,
                VOORNAAM,
                ACHTERNAAM,
                jobTitel,
                TELEFOON,
                GEBOORTEDATUM,
                LAND,
                POSTCODE,
                STAD,
                STRAAT,
                HUISNR,
                BUS
        );
        werknemerManager.saveWerknemer(dto);
        assertEquals(jobTitel, VERANTWOORDELIJKE.getJobTitel());
    }

    @ParameterizedTest
    @MethodSource("werknemerCorrecteWaarden")
    void editWerknemer_correcteWaarden_wijzigtWaarden(String voornaam, String achternaam, JobTitel jobTitel, String telefoon,
                                                      LocalDate geboortedatum, String land, String postcode, String stad, String straat,
                                                      Integer huisnr, Integer bus) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                1,
                voornaam,
                achternaam,
                jobTitel,
                telefoon,
                geboortedatum,
                land,
                postcode,
                stad,
                straat,
                huisnr,
                bus
        );

        werknemerManager.saveWerknemer(dto);
        assertEquals(voornaam, VERANTWOORDELIJKE.getVoornaam());
        assertEquals(achternaam, VERANTWOORDELIJKE.getAchternaam());
        assertEquals(jobTitel, VERANTWOORDELIJKE.getJobTitel());
        assertEquals(telefoon, VERANTWOORDELIJKE.getTelefoon());
        assertEquals(geboortedatum, VERANTWOORDELIJKE.getGeboortedatum());
        assertEquals(land, VERANTWOORDELIJKE.getLand());
        assertEquals(postcode, VERANTWOORDELIJKE.getPostcode());
        assertEquals(stad, VERANTWOORDELIJKE.getStad());
        assertEquals(straat, VERANTWOORDELIJKE.getStraat());
        assertEquals(huisnr, VERANTWOORDELIJKE.getHuisnummer());
        assertEquals(bus, VERANTWOORDELIJKE.getBus());
    }

    @ParameterizedTest
    @MethodSource("werknemerIncorrecteWaarden")
    void editWerknemer_incorrecteWaarden_gooitException(String voornaam, String achternaam, JobTitel jobTitel, String telefoon,
                                                        LocalDate geboortedatum, String land, String postcode, String stad, String straat,
                                                        Integer huisnr, Integer bus) throws WerknemerInformationException {
        WerknemerInputDTO dto = new WerknemerInputDTO(
                1,
                voornaam,
                achternaam,
                jobTitel,
                telefoon,
                geboortedatum,
                land,
                postcode,
                stad,
                straat,
                huisnr,
                bus
        );

        assertThrows(WerknemerInformationException.class, () -> werknemerManager.saveWerknemer(dto));
    }

    @Test
    void deleteWerknemer_zetStatusInactief() {
        werknemerManager.deactiveerWerknemer(werknemerDTO(VERANTWOORDELIJKE));
        assertEquals(EntityStatus.INACTIEF, VERANTWOORDELIJKE.getStatus());
    }

    @Test
    void deleteWerknemer_roeptUpdateAan() {
        werknemerManager.deactiveerWerknemer(werknemerDTO(VERANTWOORDELIJKE));
        verify(werknemerRepo).update(VERANTWOORDELIJKE);
    }

    @Test
    void deleteWerknemer_roeptGeenHardDeleteAan() {
        werknemerManager.deactiveerWerknemer(werknemerDTO(VERANTWOORDELIJKE));
        verify(werknemerRepo).startTransaction();
        verify(werknemerRepo).commitTransaction();
        verify(werknemerRepo, never()).delete(any());
    }

    @Test
    void deleteWerknemer_transactionCommitted() {
        werknemerManager.deactiveerWerknemer(werknemerDTO(VERANTWOORDELIJKE));
        verify(werknemerRepo).startTransaction();
        verify(werknemerRepo).commitTransaction();
        verify(werknemerRepo, never()).rollbackTransaction();
    }

    @Test
    void deleteWerknemer_transactionRolledBackOnError() {
        doThrow(new RuntimeException("DB fout")).when(werknemerRepo).update(any());
        assertThrows(RuntimeException.class, () -> werknemerManager.deactiveerWerknemer(werknemerDTO(VERANTWOORDELIJKE)));
        verify(werknemerRepo).rollbackTransaction();
        verify(werknemerRepo, never()).commitTransaction();
    }

    @Test
    public void voegWerknemerToeAanTeamTest() {
        Mockito.when(werknemerRepo.get(WERKNEMER.getId())).thenReturn(WERKNEMER);
        Mockito.when(werknemerRepo.voegWerknemerToeAanTeam(WERKNEMER.getId(), TEAM.getId())).thenReturn(WERKNEMER);

        Werknemer result = werknemerManager.voegWerknemerToeAanTeam(WERKNEMER.getId(), TEAM.getId());

        assertEquals(WERKNEMER, result);
        Mockito.verify(werknemerRepo).startTransaction();
        Mockito.verify(werknemerRepo).voegWerknemerToeAanTeam(WERKNEMER.getId(), TEAM.getId());
        Mockito.verify(werknemerRepo).commitTransaction();
    }

    @Test
    public void voegWerknemerToeAanTeamTest_werknemerAlInTeam() {
        WERKNEMER.getTeams().add(TEAM);

        Mockito.when(werknemerRepo.get(WERKNEMER.getId())).thenReturn(WERKNEMER);
        assertThrows(IllegalArgumentException.class, () -> werknemerManager.voegWerknemerToeAanTeam(WERKNEMER.getId(), TEAM.getId()));
    }

    @Test
    public void verwijderWerknemerUitTeamTest() throws WerknemerInformationException {
        TEAM.getWerknemers().add(WERKNEMER);
        WERKNEMER.getTeams().add(TEAM);

        Mockito.when(werknemerRepo.get(WERKNEMER.getId())).thenReturn(WERKNEMER);

        assertDoesNotThrow(() -> werknemerManager.verwijderWerknemerUitTeam(WERKNEMER.getId(), TEAM.getId()));
        assertFalse(WERKNEMER.getTeams().contains(TEAM));
        assertFalse(TEAM.getWerknemers().contains(WERKNEMER));
    }
}