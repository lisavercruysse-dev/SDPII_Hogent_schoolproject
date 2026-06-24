import domein.*;

import dto.SiteInputDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.SiteDao;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SiteManagerTest {

    @Mock
    private SiteDao siteDao;

    private SiteManager siteManager;

    // Vaste testwaarden
    private static final String     NAAM         = "Site Gent";
    private static final String     LOCATIE      = "Gent";
    private static final String     LAND         = "België";
    private static final int        CAPACITEIT   = 250;
    private static final String     OP_STATUS    = "ACTIEF";
    private static final String     PROD_STATUS  = "GEZOND";
    private static final BigDecimal BREEDTEGRAAD = new BigDecimal("51.0543");
    private static final BigDecimal LENGTEGRAAD  = new BigDecimal("3.7174");

    @BeforeEach
    void setUp() {
        siteManager = new SiteManager(siteDao);
    }

    // ── Hulpmethoden ─────────────────────────────────────────────────────────
    private SiteInputDTO maakDTO() {
        return new SiteInputDTO(
                NAAM, LOCATIE, LAND, CAPACITEIT,
                OP_STATUS, PROD_STATUS,
                BREEDTEGRAAD, LENGTEGRAAD
        );
    }

    private Site maakSite(int id) {
        Site site = new Site(
                NAAM, LOCATIE, CAPACITEIT, LAND,
                OperationeleStatus.ACTIEF, SiteProductieStatus.GEZOND,
                BREEDTEGRAAD, LENGTEGRAAD
        );
        site.setId(id);
        return site;
    }


    @Test
    void addSite_transactionCommitted() {
        siteManager.addSite(maakDTO());

        verify(siteDao).startTransaction();
        verify(siteDao).commitTransaction();
        verify(siteDao, never()).rollbackTransaction();
    }

    @Test
    void addSite_insertWordtAangeroepen() {
        siteManager.addSite(maakDTO());

        verify(siteDao, times(1)).insert(any(Site.class));
    }

    @Test
    void addSite_transactionRolledBackOnError() {
        doThrow(new RuntimeException("DB fout")).when(siteDao).insert(any());

        assertThrows(RuntimeException.class, () -> siteManager.addSite(maakDTO()));

        verify(siteDao).rollbackTransaction();
        verify(siteDao, never()).commitTransaction();
    }

    @Test
    void addSite_ongeldigeStatus_gooitException() {
        SiteInputDTO ongeldig = new SiteInputDTO(
                NAAM, LOCATIE, LAND, CAPACITEIT,
                "BESTAAT_NIET", PROD_STATUS,
                BREEDTEGRAAD, LENGTEGRAAD
        );

        assertThrows(IllegalArgumentException.class, () -> siteManager.addSite(ongeldig));
        verify(siteDao).rollbackTransaction();
    }


    @Test
    void updateSite_wijzigtAlleVelden() {
        Site bestaand = maakSite(1);
        when(siteDao.get(1)).thenReturn(bestaand);

        SiteInputDTO gewijzigd = new SiteInputDTO(
                "Nieuwe Naam", "Antwerpen", "Nederland", 500,
                "INACTIEF", "OFFLINE",
                new BigDecimal("51.2194"), new BigDecimal("4.4025")
        );
        siteManager.updateSite(1, gewijzigd);

        assertEquals("Nieuwe Naam",              bestaand.getName());
        assertEquals("Antwerpen",               bestaand.getLocatie());
        assertEquals("Nederland",               bestaand.getLand());
        assertEquals(500,                       bestaand.getCapaciteit());
        assertEquals(OperationeleStatus.INACTIEF,   bestaand.getOperationeleStatus());
        assertEquals(SiteProductieStatus.OFFLINE,   bestaand.getSiteProductieStatus());
        assertEquals(new BigDecimal("51.2194"), bestaand.getBreedtegraad());
        assertEquals(new BigDecimal("4.4025"),  bestaand.getLengtegraad());
    }

    @Test
    void updateSite_roeptUpdateAan() {
        Site bestaand = maakSite(1);
        when(siteDao.get(1)).thenReturn(bestaand);

        siteManager.updateSite(1, maakDTO());

        verify(siteDao).update(bestaand);
    }

    @Test
    void updateSite_transactionCommitted() {
        when(siteDao.get(1)).thenReturn(maakSite(1));

        siteManager.updateSite(1, maakDTO());

        verify(siteDao).startTransaction();
        verify(siteDao).commitTransaction();
        verify(siteDao, never()).rollbackTransaction();
    }

    @Test
    void updateSite_transactionRolledBackOnError() {
        when(siteDao.get(1)).thenReturn(maakSite(1));
        doThrow(new RuntimeException("DB fout")).when(siteDao).update(any());

        assertThrows(RuntimeException.class, () -> siteManager.updateSite(1, maakDTO()));

        verify(siteDao).rollbackTransaction();
        verify(siteDao, never()).commitTransaction();
    }

    @Test
    void updateSite_onbestaandeId_doetNiets() {
        when(siteDao.get(99)).thenReturn(null);

        siteManager.updateSite(99, maakDTO());

        verify(siteDao, never()).update(any());
    }


    @Test
    void deleteSite_zetIsDeletedOpTrue() {
        Site site = maakSite(1);
        when(siteDao.get(1)).thenReturn(site);

        siteManager.deleteSite(1);

        assertTrue(site.isDeleted());
    }

    @Test
    void deleteSite_roeptUpdateAan() {
        Site site = maakSite(1);
        when(siteDao.get(1)).thenReturn(site);

        siteManager.deleteSite(1);

        verify(siteDao).update(site);
    }

    @Test
    void deleteSite_roeptGeenHardDeleteAan() {
        Site site = maakSite(1);
        when(siteDao.get(1)).thenReturn(site);

        siteManager.deleteSite(1);

        verify(siteDao, never()).delete(any());
    }

    @Test
    void deleteSite_transactionCommitted() {
        when(siteDao.get(1)).thenReturn(maakSite(1));

        siteManager.deleteSite(1);

        verify(siteDao).startTransaction();
        verify(siteDao).commitTransaction();
        verify(siteDao, never()).rollbackTransaction();
    }

    @Test
    void deleteSite_transactionRolledBackOnError() {
        when(siteDao.get(1)).thenReturn(maakSite(1));
        doThrow(new RuntimeException("DB fout")).when(siteDao).update(any());

        assertThrows(RuntimeException.class, () -> siteManager.deleteSite(1));

        verify(siteDao).rollbackTransaction();
        verify(siteDao, never()).commitTransaction();
    }

    @Test
    void deleteSite_onbestaandeId_doetNiets() {
        when(siteDao.get(99)).thenReturn(null);

        siteManager.deleteSite(99);

        verify(siteDao, never()).update(any());
    }

    @Test
    void deleteSite_verschijntNietMeerInLijst() {
        Site teVerwijderen = maakSite(1);
        Site andere        = maakSite(2);
        when(siteDao.get(1)).thenReturn(teVerwijderen);
        when(siteDao.findAllActive()).thenReturn(List.of(andere));

        siteManager.deleteSite(1);

        List<Site> actief = siteDao.findAllActive();
        assertFalse(actief.contains(teVerwijderen));
        assertTrue(actief.contains(andere));
    }

    @Test
    void addSite_slaagtMetGeldigeData() {
        SiteInputDTO dto = maakDTO();

        assertDoesNotThrow(() -> siteManager.addSite(dto));

        verify(siteDao).startTransaction();
        verify(siteDao).insert(any(Site.class));
        verify(siteDao).commitTransaction();
    }

    @Test
    void addSite_gooitExceptionBijOngeldigeEnum() {
        SiteInputDTO ongeldig = new SiteInputDTO(
                NAAM, LOCATIE, LAND, CAPACITEIT,
                "DIT_BESTAAT_NIET", PROD_STATUS,
                BREEDTEGRAAD, LENGTEGRAAD
        );

        assertThrows(IllegalArgumentException.class, () -> siteManager.addSite(ongeldig));
        verify(siteDao).rollbackTransaction();
    }

    @Test
    void addSite_rollbackBijInsertFout() {
        SiteInputDTO dto = maakDTO();
        doThrow(new RuntimeException("Insert mislukt")).when(siteDao).insert(any(Site.class));

        assertThrows(RuntimeException.class, () -> siteManager.addSite(dto));

        verify(siteDao).rollbackTransaction();
    }
}