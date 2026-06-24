import domein.*;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.TeamInputDTO;
import dto.WerknemerDTO;
import exception.WerknemerInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdp.sdp.gui.ObservableTeam;
import org.sdp.sdp.gui.ObservableTeamsTable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
public class ObservableTeamsTableTest {
    private final TeamController controller = mock(TeamController.class);
    private final WerknemerController werknemerController = mock(WerknemerController.class);
    private final SiteController siteController = mock(SiteController.class);
    private ObservableTeamsTable table;

    private static Werknemer VERANTWOORDELIJKE1;
    private static Werknemer VERANTWOORDELIJKE2;
    private static final Site SITE1 = new Site(
            "Site Gent", "Gent", 250, "België", OperationeleStatus.ACTIEF, SiteProductieStatus.GEZOND,
            new BigDecimal("51.0543"), new BigDecimal("3.7174")
    );
    private static final Site SITE2 = new Site(
            "Site Brugge", "Brugge", 250, "België",  OperationeleStatus.ACTIEF, SiteProductieStatus.GEZOND,
            new BigDecimal("51.0543"), new BigDecimal("3.7174")
    );
    private Set<String> alleStatussen() {
        return Arrays.stream(EntityStatus.values()).map(Enum::name).collect(Collectors.toSet());
    }

    private SiteDTO toSiteDTO(Site s) {
        return new SiteDTO(
                s.getId(),
                s.getName(),
                s.getLocatie(),
                s.getLand(),
                s.getCapaciteit(),
                s.getOperationeleStatus().name(),
                s.getSiteProductieStatus().name(),
                s.getBreedtegraad(),
                s.getLengtegraad());
    }

    private WerknemerDTO toWerknemerDTO(Werknemer w) {
        return new WerknemerDTO(
                w.getId(),
                w.getVoornaam(),
                w.getAchternaam(),
                w.getJobTitel().name(),
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
        VERANTWOORDELIJKE1 = Werknemer.builder()
                .voornaam("Pieter")
                .achternaam("De Bakker")
                .jobTitel(JobTitel.VERANTWOORDELIJKE)
                .telefoon("").geboortedatum(LocalDate.of(1995, 1, 1))
                .land("België").postcode("9000").stad("Gent")
                .straat("Gentstraat").huisnummer(12).bus(null).build();

        VERANTWOORDELIJKE2 = Werknemer.builder()
                .voornaam("Katrien")
                .achternaam("De Bakker")
                .jobTitel(JobTitel.VERANTWOORDELIJKE)
                .telefoon("").geboortedatum(LocalDate.of(1995, 1, 1))
                .land("België").postcode("9000").stad("Gent")
                .straat("Gentstraat").huisnummer(12).bus(null).build();

        //mock training
        Map<Integer, SiteDTO> sites = Map.of(
                1, toSiteDTO(SITE1),
                2, toSiteDTO(SITE2),
                3, toSiteDTO(SITE2)
        );

        Map<Integer, WerknemerDTO> verantwoordelijken = Map.of(
                1, toWerknemerDTO(VERANTWOORDELIJKE1),
                2, toWerknemerDTO(VERANTWOORDELIJKE2),
                3, toWerknemerDTO(VERANTWOORDELIJKE1)
        );

        Map<Integer, Integer> teamSizes = Map.of(
                1, 8,
                2, 4,
                3, 12
        );

        for (Integer teamId : sites.keySet()) {
            Mockito.when(siteController.getSiteFromTeam(teamId))
                    .thenReturn(sites.get(teamId));

            Mockito.when(werknemerController.getVerantwoordelijkeVanTeam(teamId))
                    .thenReturn(verantwoordelijken.get(teamId));

            Mockito.when(werknemerController.getWerknemersFromTeam(teamId))
                    .thenReturn(new ArrayList<>(
                            Collections.nCopies(
                                    teamSizes.get(teamId),
                                    toWerknemerDTO(VERANTWOORDELIJKE1)
                            )
                    ));
        }

        TeamDTO t1 = new TeamDTO(1, "Team A", SITE1.getId(), EntityStatus.ACTIEF);
        TeamDTO t2 = new TeamDTO(2, "Team B", SITE2.getId(), EntityStatus.ACTIEF);
        TeamDTO t3 = new TeamDTO(3, "Team C", SITE2.getId(), EntityStatus.INACTIEF);

        table = new ObservableTeamsTable(controller, werknemerController, siteController, List.of(t1, t2, t3));
    }

    @Test
    void lijstWordtCorrectOpgevuld() {
        assertEquals(3, table.getFilteredTeams().size());
    }

    @Test
    void filterWerktOpNaam() {
        table.changeFilter("B", "", "", null, null, alleStatussen());

        assertEquals(1, table.getFilteredTeams().size());
        assertEquals("Team B", table.getFilteredTeams().getFirst().getTeam().naam());
    }

    @Test
    void filterWerktOpVerantwoordelijke() {
        table.changeFilter("", "Pi", "", null, null, alleStatussen());

        assertEquals(2, table.getFilteredTeams().size());
        assertEquals("Pieter De Bakker",  table.getFilteredTeams().getFirst().verantwoordelijkeProperty().getValue());
    }

    @Test
    void filterWerktOpSite() {
        table.changeFilter("", "", "gen", null, null, alleStatussen());

        assertEquals(1, table.getFilteredTeams().size());
        assertEquals("Site Gent", table.getFilteredTeams().getFirst().siteProperty().getValue());
    }

    @Test
    void filterWerktOpMinWerknemerCount() {
        table.changeFilter("", "", "", 8, null, alleStatussen());

        assertEquals(2, table.getFilteredTeams().size());
        assertEquals("Team A", table.getFilteredTeams().getFirst().getTeam().naam());
    }

    @Test
    void filterWerktOpMaxWerknemerCount() {
        table.changeFilter("", "", "", null, 4, alleStatussen());

        assertEquals(1, table.getFilteredTeams().size());
        assertEquals("Team B", table.getFilteredTeams().getFirst().getTeam().naam());
    }

    @Test
    void filterWerktOpWerknemerCountRange() {
        table.changeFilter("", "", "", 4, 8, alleStatussen());

        assertEquals(2, table.getFilteredTeams().size());
        assertEquals("Team A", table.getFilteredTeams().getFirst().getTeam().naam());
    }

    @Test
    void filterWerktOpStatus() {
        table.changeFilter("", "", "", null, null, Set.of("ACTIEF"));

        assertEquals(2, table.getFilteredTeams().size());
        assertEquals("Team A", table.getFilteredTeams().getFirst().getTeam().naam());
    }

    @Test
    void filterVindtGeenOvereenkomst() {
        table.changeFilter("bestaat niet", "", "", null, null, alleStatussen());

        assertEquals(0, table.getFilteredTeams().size());
    }

    @Test
    void legeFilterToontAlles() {
        table.changeFilter("", "", "", null, null, alleStatussen());
        assertEquals(3, table.getFilteredTeams().size());
    }

    @Test
    void lijstWordtGeupdatetBijToevoegen() {
        TeamDTO t3DTO = new TeamDTO(3, "Team C", 1, EntityStatus.ACTIEF);

        Mockito.when(controller.saveTeam(Mockito.any())).thenReturn(t3DTO);
        Mockito.when(siteController.getSiteFromTeam(3)).thenReturn(toSiteDTO(SITE1));
        Mockito.when(werknemerController.getVerantwoordelijkeVanTeam(3)).thenReturn(toWerknemerDTO(VERANTWOORDELIJKE1));

        TeamInputDTO dto = new TeamInputDTO("Team C", toSiteDTO(SITE1), toWerknemerDTO(VERANTWOORDELIJKE1), null);
        table.saveTeam(dto);

        assertEquals(4, table.getFilteredTeams().size());
    }

}

