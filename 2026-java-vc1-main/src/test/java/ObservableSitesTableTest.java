import domein.*;
import dto.SiteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdp.sdp.gui.ObservableSitesTable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableSitesTableTest {
    private SiteController controller;
    private ObservableSitesTable table;
    private Site s1;
    private Site s2;
    private Site s3;

    @BeforeEach
    void setUp(){
        controller = mock(SiteController.class);

        s1 = Site.builder()
                .name("Site A")
                .locatie("Gent")
                .capaciteit(100)
                .land("België")
                .operationeleStatus(OperationeleStatus.ACTIEF)
                .siteProductieStatus(SiteProductieStatus.GEZOND)
                .build();
        s2 = Site.builder()
                .name("Site B")
                .locatie("Aalst")
                .capaciteit(120)
                .land("België")
                .operationeleStatus(OperationeleStatus.INACTIEF)
                .siteProductieStatus(SiteProductieStatus.OFFLINE)
                .build();
        s3 = Site.builder()
                .name("Site C")
                .locatie("Parijs")
                .capaciteit(150)
                .land("Frankrijk")
                .operationeleStatus(OperationeleStatus.INACTIEF)
                .siteProductieStatus(SiteProductieStatus.PROBLEMEN)
                .build();

        when(controller.getAllSites()).thenReturn(List.of(
                toDTO(s1), toDTO(s2), toDTO(s3)
        ));

        table = new ObservableSitesTable(controller);
    }

    // ── Hulpmethoden ─────────────────────────────────────────────────────────
    private SiteDTO toDTO(Site s) {
        return new SiteDTO(
                s.getId(),
                s.getName(),
                s.getLocatie(),
                s.getLand(),
                s.getCapaciteit(),
                s.getOperationeleStatus().name(),
                s.getSiteProductieStatus().name(),
                s.getBreedtegraad(),
                s.getLengtegraad()
        );
    }

    /** Alle waarden van een enum als Set — "geen filter op deze kolom". */
    private Set<String> alleOpStatussen() {
        return Arrays.stream(OperationeleStatus.values())
                .map(Enum::name).collect(Collectors.toSet());
    }

    private Set<String> alleProdStatussen() {
        return Arrays.stream(SiteProductieStatus.values())
                .map(Enum::name).collect(Collectors.toSet());
    }

    // ════════════════════════════════════════════════════════════════════════

    @Test
    void lijstWordtCorrectOpgevuld() {
        assertEquals(3, table.getFilteredList().size());
    }

    @Test
    void filterWerktOpNaam() {
        table.changeFilter("a", "", "",null, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site A", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpLocatie() {
        table.changeFilter("", "Gen","",null, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site A", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpLand() {
        table.changeFilter("", "","Fr",null, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site C", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpMinCapaciteit() {
        table.changeFilter("", "", "",130, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site C", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpMaxCapaciteit() {
        table.changeFilter("", "", "", null, 110, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site A", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpCapaciteitBereik() {
        table.changeFilter("", "", "",110, 140, alleOpStatussen(), alleProdStatussen());

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site B", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpOperationeleStatus() {
        table.changeFilter("","","",null, null, Set.of("INACTIEF"), alleProdStatussen());

        assertEquals(2, table.getFilteredList().size());
    }

    @Test
    void filterWerktOpProductieStatus() {
        table.changeFilter("","","",null, null, alleOpStatussen(), Set.of("GEZOND"));

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site A", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterWerktOpComboOperationeleEnProductieStatus() {
        table.changeFilter("","","",null, null, Set.of("INACTIEF"), Set.of("OFFLINE"));

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site B", table.getFilteredList().get(0).getName());

        table.changeFilter("","","", null, null, Set.of("INACTIEF"), Set.of("PROBLEMEN"));

        assertEquals(1, table.getFilteredList().size());
        assertEquals("Site C", table.getFilteredList().get(0).getName());
    }

    @Test
    void filterVindtGeenOvereenkomst() {
        table.changeFilter("abc", "","",null, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(0, table.getFilteredList().size());
    }

    @Test
    void legeFilterToontAlles() {
        table.changeFilter("", "","",null, null, alleOpStatussen(), alleProdStatussen());

        assertEquals(3, table.getFilteredList().size());
    }


}
