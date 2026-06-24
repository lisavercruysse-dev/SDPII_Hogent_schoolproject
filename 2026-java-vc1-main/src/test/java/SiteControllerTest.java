import domein.*;
import dto.SiteDTO;
import dto.SiteInputDTO;
import exception.SiteInformationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import util.SiteElement;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SiteControllerTest {

    @Mock
    private SiteManager siteManager;

    private SiteController siteController;

    private SiteInputDTO geldigDto;
    private Site bestaandeSite;

    @BeforeEach
    void setUp() {
        siteController = new SiteController(siteManager);

        geldigDto = new SiteInputDTO(
                "Site Gent", "Gent", "België", 250,
                "ACTIEF", "GEZOND",
                new BigDecimal("51.0543"), new BigDecimal("3.7174")
        );

        bestaandeSite = new Site(
                "Site Gent", "Gent", 250, "België",
                OperationeleStatus.ACTIEF, SiteProductieStatus.GEZOND,
                new BigDecimal("51.0543"), new BigDecimal("3.7174")
        );
        bestaandeSite.setId(1);
    }

    // ==================== TOEVOEGEN ====================

    @Test
    void voegSiteToe_slaagtBijUniekeNaam() throws SiteInformationException {
        when(siteManager.existsByNaamIgnoreCase(anyString())).thenReturn(false);
        doNothing().when(siteManager).addSite(any(SiteInputDTO.class));

        assertDoesNotThrow(() -> siteController.voegSiteToe(geldigDto));

        verify(siteManager).addSite(any(SiteInputDTO.class));
    }

    @Test
    void voegSiteToe_gooitExceptionBijBestaandeNaam() {
        when(siteManager.existsByNaamIgnoreCase(anyString())).thenReturn(true);

        SiteInformationException ex = assertThrows(SiteInformationException.class,
                () -> siteController.voegSiteToe(geldigDto));

        assertEquals("Er bestaat al een site met deze naam.",
                ex.getInformationRequired().get(SiteElement.NAME));
    }

    // ==================== WIJZIGEN ====================

    @Test
    void wijzigSite_slaagtBijZelfdeNaam() throws SiteInformationException {
        when(siteManager.getSiteById(1)).thenReturn(bestaandeSite);
        doNothing().when(siteManager).updateSite(anyInt(), any(SiteInputDTO.class));

        SiteInputDTO dto = new SiteInputDTO("Site Gent", "Nieuwe Locatie", "Frankrijk", 300,
                "INACTIEF", "OFFLINE", new BigDecimal("51.1"), new BigDecimal("4.4"));

        assertDoesNotThrow(() -> siteController.wijzigSite(1, dto));

        verify(siteManager).updateSite(1, dto);
    }

    @Test
    void wijzigSite_slaagtBijNaamwijzigingNaarUniekeNaam() throws SiteInformationException {
        when(siteManager.getSiteById(1)).thenReturn(bestaandeSite);
        when(siteManager.existsByNaamIgnoreCase("Nieuwe Unieke Site")).thenReturn(false);
        doNothing().when(siteManager).updateSite(anyInt(), any());

        SiteInputDTO dto = new SiteInputDTO("Nieuwe Unieke Site", "Gent", "België", 250,
                "ACTIEF", "GEZOND", new BigDecimal("51.0543"), new BigDecimal("3.7174"));

        assertDoesNotThrow(() -> siteController.wijzigSite(1, dto));
        verify(siteManager).updateSite(1, dto);
    }

    @Test
    void wijzigSite_gooitExceptionBijNaamwijzigingNaarBestaandeNaam() {
        when(siteManager.getSiteById(1)).thenReturn(bestaandeSite);
        when(siteManager.existsByNaamIgnoreCase(anyString())).thenReturn(true);

        SiteInputDTO dto = new SiteInputDTO("Bestaande Andere Site", "Gent", "België", 250,
                "ACTIEF", "GEZOND", new BigDecimal("51"), new BigDecimal("4"));

        SiteInformationException ex = assertThrows(SiteInformationException.class,
                () -> siteController.wijzigSite(1, dto));

        assertEquals("Er bestaat al een site met deze naam.",
                ex.getInformationRequired().get(SiteElement.NAME));
    }
}