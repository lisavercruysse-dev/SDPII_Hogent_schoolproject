import domein.Melding;
import domein.MeldingManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import repository.MeldingDao;
import util.MeldingType;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MeldingManagerTest {

    @Mock
    private MeldingDao meldingDao;

    @InjectMocks
    private MeldingManager meldingManager;

    @Test
    void addMeldingVoorGroep_VerstuurtNaarAlleOntvangers_MetEénTransactie() {
        List<Integer> ontvangers = List.of(1, 2);

        meldingManager.addMeldingVoorGroep(MeldingType.SYSTEEM, "Nieuwe Machine", "Bericht", ontvangers);

        verify(meldingDao, times(1)).startTransaction();
        verify(meldingDao, times(2)).insert(any(Melding.class));
        verify(meldingDao, times(1)).commitTransaction();
    }

    @Test
    void addMeldingVoorLegeGroep_DoetNiets() {
        List<Integer> ontvangers = List.of();

        meldingManager.addMeldingVoorGroep(MeldingType.SYSTEEM, "Nieuwe Machine", "Bericht", ontvangers);

        verify(meldingDao, never()).startTransaction();
        verify(meldingDao, never()).insert(any(Melding.class));
    }

    @Test
    void markeerAllesAlsGelezen_ZetEnkelOngelezenMeldingenOpGelezen() {
        int werknemerId = 1;
        Melding gelezenMelding = new Melding(MeldingType.SYSTEEM, "T1", "D1", LocalDate.now(), true, werknemerId);
        Melding ongelezenMelding = new Melding(MeldingType.SYSTEEM, "T2", "D2", LocalDate.now(), false, werknemerId);

        when(meldingDao.findAllForWerknemer(werknemerId)).thenReturn(List.of(gelezenMelding, ongelezenMelding));

        meldingManager.markeerAllesAlsGelezen(werknemerId);

        verify(meldingDao, times(1)).startTransaction();
        verify(meldingDao, times(1)).update(ongelezenMelding);
        verify(meldingDao, never()).update(gelezenMelding);
        verify(meldingDao, times(1)).commitTransaction();
    }
}