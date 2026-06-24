import domein.*;
import dto.*;
import exception.TeamInformationException;
import exception.WerknemerInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.GebruikerDao;
import repository.SiteDao;
import repository.TeamDao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TeamTest {

    @Mock private TeamDao teamDao;
    @Mock private SiteDao siteDao;
    @Mock private GebruikerDao gebruikerDao;

    private TeamManager teamManager;

    private static final WerknemerDTO VERANTWOORDELIJKE_DTO = new WerknemerDTO(
            1,
            "Pieter",
            "De Bakker",
            "Verantwoordelijke",
            "",
            LocalDate.of(1995, 1, 1),
            "België",
            "9000",
            "Gent",
            "Gentstraat",
            12,
            null,
            "Pieter.DeBakker@example.com",
            EntityStatus.ACTIEF
    );
    private static final SiteDTO SITE_DTO = new SiteDTO(
            1,
            "Site Gent",
            "Gent",
            "België",
            250,
            "ACTIEF",
            "GEZOND",
            new BigDecimal("51.0543"),
            new BigDecimal("3.7174")
    );

    private static Werknemer VERANTWOORDELIJKE;
    private static Site SITE;

    static Stream<Arguments> valuesIncorrect() {
        return Stream.of(
                //naam
                Arguments.of("", VERANTWOORDELIJKE_DTO, SITE_DTO),
                Arguments.of("     ", VERANTWOORDELIJKE_DTO, SITE_DTO),
                Arguments.of("A", VERANTWOORDELIJKE_DTO, SITE_DTO),
                Arguments.of(null, VERANTWOORDELIJKE_DTO, SITE_DTO),

                //verantwoordelijke
                Arguments.of("Site A", null, SITE_DTO),

                //site
                Arguments.of("Site A", VERANTWOORDELIJKE_DTO, null)
        );
    }

    @BeforeEach
    public void setup() throws WerknemerInformationException{
        teamManager = new TeamManager(teamDao, gebruikerDao, siteDao);

        VERANTWOORDELIJKE = Werknemer.builder()
                .voornaam(VERANTWOORDELIJKE_DTO.voornaam())
                .achternaam(VERANTWOORDELIJKE_DTO.achternaam())
                .jobTitel(JobTitel.VERANTWOORDELIJKE)
                .telefoon(VERANTWOORDELIJKE_DTO.telefoon())
                .geboortedatum(VERANTWOORDELIJKE_DTO.geboortedatum())
                .land(VERANTWOORDELIJKE_DTO.land())
                .postcode(VERANTWOORDELIJKE_DTO.postcode())
                .stad(VERANTWOORDELIJKE_DTO.stad())
                .straat(VERANTWOORDELIJKE_DTO.straat())
                .huisnummer(VERANTWOORDELIJKE_DTO.huisnummer())
                .bus(VERANTWOORDELIJKE_DTO.bus())
                .build();

        SITE = new Site(
                SITE_DTO.name(),
                SITE_DTO.locatie(),
                SITE_DTO.capaciteit(),
                SITE_DTO.land(),
                OperationeleStatus.valueOf(SITE_DTO.operationeleStatus()),
                SiteProductieStatus.valueOf(SITE_DTO.siteProductieStatus()),
                SITE_DTO.breedtegraad(),
                SITE_DTO.lengtegraad()
        );
        SITE.setId(1);

        lenient().when(teamDao.get(1)).thenReturn(maakTeam());
        lenient().when(gebruikerDao.get(1)).thenReturn(VERANTWOORDELIJKE);
        lenient().when(siteDao.get(SITE_DTO.id())).thenReturn(SITE);
    }

    private TeamInputDTO maakTeamInputDTO(Integer id) {
        return new TeamInputDTO(
                "Team A", SITE_DTO, VERANTWOORDELIJKE_DTO, id);
    }

    private Team maakTeam() {
        return new Team(VERANTWOORDELIJKE, "Team A", SITE);
    }

    private TeamDTO maakTeamDTO() {
        return new TeamDTO(1, "Team A", 1, EntityStatus.ACTIEF);
    }

    @Test
    void addTeam_transactionCommitted() {
        teamManager.saveTeam(maakTeamInputDTO(null));

        verify(teamDao).startTransaction();
        verify(teamDao).commitTransaction();
        verify(siteDao, never()).rollbackTransaction();
    }

    @Test
    void addTeam_insertWordtAangeroepen() {
        teamManager.saveTeam(maakTeamInputDTO(null));

        verify(teamDao, times(1)).insert(any(Team.class));
    }

    @Test
    void addTeam_transactionRolledBackOnError() {
        doThrow(new RuntimeException("DB fout")).when(teamDao).insert(any());

        assertThrows(RuntimeException.class, () -> teamManager.saveTeam(maakTeamInputDTO(null)));

        verify(teamDao).rollbackTransaction();
        verify(teamDao, never()).commitTransaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Team A", "AA"})
    void addTeam_correcteNaam_maaktTeam(String naam) {
        TeamInputDTO dto = new TeamInputDTO(naam, SITE_DTO, VERANTWOORDELIJKE_DTO, null);
        Team result = teamManager.saveTeam(dto);
        assertEquals(naam, result.getNaam());
    }

    @ParameterizedTest
    @MethodSource("valuesIncorrect")
    void addTeam_fouteNaam_gooitException(String naam, WerknemerDTO werknemer, SiteDTO site) {
        TeamInputDTO dto = new TeamInputDTO(naam,  site, werknemer, null);
        assertThrows(TeamInformationException.class, () -> teamManager.saveTeam(dto));
    }

    @Test
    void addTeam_bestaatAl_gooitException() {
        Team t1 = new Team(VERANTWOORDELIJKE, "Team A", SITE);
        Team t2 = new Team(VERANTWOORDELIJKE, "Team B", SITE);
        List<Team> bestaand = List.of(t1, t2);

        TeamInputDTO dto = new TeamInputDTO("Team A", SITE_DTO, VERANTWOORDELIJKE_DTO, null);
        when(teamDao.findAll()).thenReturn(bestaand);

        assertThrows(IllegalArgumentException.class, () -> teamManager.saveTeam(dto));
        verify(teamDao, never()).insert(any(Team.class));
    }

    @Test
    void editTeam_roeptUpdateAan() {
        Team team = maakTeam();
        when(teamDao.get(1)).thenReturn(team);
        teamManager.saveTeam(maakTeamInputDTO(1));
        verify(teamDao).update(team);
    }

    @Test
    void editTeam_transactionCommitted() {
        teamManager.saveTeam(maakTeamInputDTO(1));
        verify(teamDao).startTransaction();
        verify(teamDao).commitTransaction();
        verify(siteDao, never()).rollbackTransaction();
    }

    @Test
    void editTeam_transactionRollbackOnError() {
        doThrow(new RuntimeException("DB fout")).when(teamDao).update(any());
        assertThrows(RuntimeException.class, () -> teamManager.saveTeam(maakTeamInputDTO(1)));
        verify(teamDao).rollbackTransaction();
        verify(siteDao, never()).commitTransaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Team B", "aa"})
    void editTeam_correcteNaam_wijzigtNaam(String naam) {
        Team team = maakTeam();
        TeamInputDTO dto = new TeamInputDTO(naam,  SITE_DTO, VERANTWOORDELIJKE_DTO, 1);
        when(teamDao.get(1)).thenReturn(team);
        teamManager.saveTeam(dto);
        assertEquals(naam, team.getNaam());
    }

    @ParameterizedTest
    @MethodSource("valuesIncorrect")
    void editTeam_fouteWaarden_gooitException(String naam, WerknemerDTO werknemer, SiteDTO site) {
        TeamInputDTO dto = new TeamInputDTO(naam,  site, werknemer, 1);
        assertThrows(TeamInformationException.class, () -> teamManager.saveTeam(dto));
    }

    @Test
    void deleteTeam_zetStatusInactief() {
        Team team = maakTeam();
        when(teamDao.get(1)).thenReturn(team);
        teamManager.deleteTeam(maakTeamDTO());
        assertEquals(EntityStatus.INACTIEF, team.getStatus());
    }

    @Test
    void deleteTeam_teamHeeftNogLeden_gooitException() throws WerknemerInformationException{
        Team team = maakTeam();

        Werknemer w1 = Werknemer.builder()
                .voornaam("Emma")
                .achternaam("Van Aert")
                .jobTitel(JobTitel.WERKNEMER)
                .telefoon("0474252326")
                .geboortedatum(LocalDate.of(1985, 2, 6))
                .land("België")
                .postcode("9000")
                .stad("Gent")
                .straat("Gentstraat")
                .huisnummer(20)
                .bus(2)
                .build();

        team.getWerknemers().add(w1);

        when(teamDao.get(1)).thenReturn(team);
        assertThrows(IllegalArgumentException.class, () -> teamManager.deleteTeam(maakTeamDTO()));
    }

    @Test
    void deleteTeam_roeptUpdateAan() {
        Team team = maakTeam();
        when(teamDao.get(1)).thenReturn(team);
        teamManager.deleteTeam(maakTeamDTO());
        verify(teamDao).update(team);
    }

    @Test
    void deleteTeam_roeptGeenHardDeleteAan() {
        teamManager.deleteTeam(maakTeamDTO());
        verify(teamDao).startTransaction();
        verify(teamDao).commitTransaction();
        verify(siteDao, never()).delete(any());
    }

    @Test
    void deleteTeam_transactionCommitted() {
        teamManager.deleteTeam(maakTeamDTO());
        verify(teamDao).startTransaction();
        verify(teamDao).commitTransaction();
        verify(teamDao, never()).rollbackTransaction();
    }

    @Test
    void deleteTeam_transactionRollbackOnError() {
        doThrow(new RuntimeException("DB fout")).when(teamDao).update(any());
        assertThrows(RuntimeException.class, () -> teamManager.deleteTeam(maakTeamDTO()));
        verify(teamDao).rollbackTransaction();
        verify(siteDao, never()).commitTransaction();
    }
}