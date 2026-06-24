import domein.Melding;
import domein.MeldingManager;
import domein.MeldingenController;
import dto.MeldingDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import util.MeldingType;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MeldingenControllerTest {

    @Mock
    private MeldingManager meldingManager;

    private MeldingenController meldingenController;
    private final int INGELOGDE_GEBRUIKER_ID = 1;

    @BeforeEach
    void setUp() throws Exception {
        meldingenController = new MeldingenController(INGELOGDE_GEBRUIKER_ID);

        Field managerField = MeldingenController.class.getDeclaredField("meldingManager");

        managerField.setAccessible(true);

        managerField.set(meldingenController, meldingManager);
    }

    @Test
    void getGefilterdeMeldingen_NegeertWebMeldingenZoalsTaakToegewezen() {
        Melding javaMelding = new Melding(MeldingType.TEAM_AANGEMAAKT, "Team", "Detail", LocalDate.now(), false, INGELOGDE_GEBRUIKER_ID);
        Melding webMelding = new Melding(MeldingType.TAAK_TOEGEWEZEN, "Taak", "Detail", LocalDate.now(), false, INGELOGDE_GEBRUIKER_ID);

        when(meldingManager.getMeldingenVoorWerknemer(INGELOGDE_GEBRUIKER_ID)).thenReturn(List.of(javaMelding, webMelding));

        List<MeldingDTO> result = meldingenController.getGefilterdeMeldingen(null, null);

        assertEquals(1, result.size());
        assertEquals("Team aangemaakt", result.get(0).type());
    }

    @Test
    void getAantalOngelezen_TeltWebMeldingenNietMee() {
        Melding ongelezenJava = new Melding(MeldingType.SYSTEEM, "Sys", "Detail", LocalDate.now(), false, INGELOGDE_GEBRUIKER_ID);
        Melding ongelezenWeb = new Melding(MeldingType.TAAK_GEWIJZIGD, "Taak", "Detail", LocalDate.now(), false, INGELOGDE_GEBRUIKER_ID);
        Melding gelezenJava = new Melding(MeldingType.SYSTEEM, "Sys2", "Detail", LocalDate.now(), true, INGELOGDE_GEBRUIKER_ID);

        when(meldingManager.getMeldingenVoorWerknemer(INGELOGDE_GEBRUIKER_ID))
                .thenReturn(List.of(ongelezenJava, ongelezenWeb, gelezenJava));

        long aantal = meldingenController.getAantalOngelezen();

        assertEquals(1, aantal);
    }
}