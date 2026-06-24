import domein.*;
import dto.SiteDTO;
import dto.TeamDTO;
import dto.WerknemerDTO;
import exception.WerknemerInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdp.sdp.gui.ObservableTeam;
import org.sdp.sdp.gui.ObservableWerknemerFromTeamList;
import org.sdp.sdp.gui.ObservableWerknemersTable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableTeamsDetailsTest {

    @Mock private WerknemerController werknemerController;
    @Mock private SiteController siteController;
    @Mock private TeamController teamController;

    private ObservableTeam observableTeam;
    private Team team;
    private ObservableWerknemersTable table;
    private static Werknemer VERANTWOORDELIJKE;
    private static final Site SITE = new Site(
            "Site noord",
            "Gent",
            100,
            "België",
            OperationeleStatus.ACTIEF,
            SiteProductieStatus.GEZOND,
            new BigDecimal("51.0543"),
            new BigDecimal("3.7174"));

    @BeforeEach
    void setUp() throws WerknemerInformationException {
        VERANTWOORDELIJKE = Werknemer.builder()
                .voornaam("Bart")
                .achternaam("De Smedt")
                .jobTitel(JobTitel.VERANTWOORDELIJKE)
                .geboortedatum(LocalDate.of(2000, 1, 1))
                .land("België")
                .postcode("9000")
                .stad("Gent")
                .straat("Vlaanderenstraat")
                .huisnummer(12)
                .build();

        Werknemer w1 = Werknemer.builder()
                .voornaam("Simon")
                .achternaam("Van Aert")
                .jobTitel(JobTitel.WERKNEMER)
                .geboortedatum(LocalDate.of(2000, 1, 1))
                .telefoon("123456789")
                .land("België")
                .postcode("9000")
                .stad("Gent")
                .straat("Vlaanderenstraat")
                .huisnummer(12)
                .build();

        Werknemer w2 = Werknemer.builder()
                .voornaam("Katrien")
                .achternaam("De Bakker")
                .jobTitel(JobTitel.WERKNEMER)
                .geboortedatum(LocalDate.of(2000, 1, 1))
                .telefoon("123456789")
                .land("België")
                .postcode("9000")
                .stad("Gent")
                .straat("Vlaanderenstraat")
                .huisnummer(12)
                .build();

        team = new Team(VERANTWOORDELIJKE, "Team A", SITE);
        team.getWerknemers().addAll(List.of(w1, w2));

        when(werknemerController.getVerantwoordelijkeVanTeam(anyInt())).thenReturn(toWerknemerDTO(VERANTWOORDELIJKE));
        when(siteController.getSiteFromTeam(anyInt())).thenReturn(toSiteDTO(SITE));
        when(werknemerController.getWerknemersFromTeam(anyInt())).thenReturn(List.of(toWerknemerDTO(w1), toWerknemerDTO(w2)));

        observableTeam = new ObservableTeam(toTeamDTO(team), werknemerController, siteController);

        List<WerknemerDTO> werknemerDTOs = team.getWerknemers().stream()
                .map(this::toWerknemerDTO)
                .toList();

        table = new ObservableWerknemersTable(werknemerController, werknemerDTOs);
    }

    private WerknemerDTO toWerknemerDTO(Werknemer w) {
        return new WerknemerDTO(
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
                w.getStatus());
    }

    private TeamDTO toTeamDTO(Team t) {
        return new TeamDTO(
                t.getId(),
                t.getNaam(),
                t.getSite().getId(),
                t.getStatus()
        );
    }

    private SiteDTO toSiteDTO(Site s) {
        return new SiteDTO(
                s.getId(),
                s.getName(),
                s.getLocatie(),
                s.getLand(),
                s.getCapaciteit(),
                s.getOperationeleStatus().toString(),
                s.getSiteProductieStatus().toString(),
                s.getBreedtegraad(),
                s.getLengtegraad()
        );
    }

    @Test
    void lijstWordtCorrectOpgevuld() {
        assertEquals(2, table.getFilteredList().size());
    }

    @Test
    void lijstWordtCorrectGeupdatetBijToevoegen() throws  WerknemerInformationException {
        Werknemer w3 = Werknemer.builder()
                .voornaam("Jana")
                .achternaam("De Mol")
                .jobTitel(JobTitel.WERKNEMER)
                .geboortedatum(LocalDate.of(1986, 1, 2))
                .land("België")
                .postcode("9000")
                .telefoon("123456789")
                .stad("Gent")
                .straat("Vlaanderenstraat")
                .huisnummer(12)
                .build();

        WerknemerDTO w3DTO = toWerknemerDTO(w3);

        Mockito.when(werknemerController.voegWerknemerToeAanTeam(w3.getId(), team.getId()))
                .thenReturn(w3DTO);

        table.addWerknemerToTeam(w3DTO.id(), team.getId(), observableTeam);
        assertEquals(3, table.getFilteredList().size());
    }
}
