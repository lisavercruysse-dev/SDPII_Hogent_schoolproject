import domein.*;
import dto.TaaktemplateDTO;
import dto.TaaktemplateInputDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdp.sdp.gui.ObservableTemplateTable;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableTaaktemplatesTest {

    private final TaaktemplateController controller = mock(TaaktemplateController.class);
    private ObservableTemplateTable table;

    private Set<String> alleStatussen() {
        return Arrays.stream(EntityStatus.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @BeforeEach
    void setUp() {
        Taaktemplate t1 = new Taaktemplate(TaakType.INSTALLATIE, "Installatie machine", 30);
        Taaktemplate t2 = new Taaktemplate(TaakType.HERSTELLING, "Herstelling machine", 60);
        Taaktemplate t3 = new Taaktemplate(TaakType.INSPECTIE, "Inspectie machine", 15);

        when(controller.getAllTemplates()).thenReturn(List.of(
                toDTO(t1), toDTO(t2), toDTO(t3)
        ));

        table = new ObservableTemplateTable(controller);
    }

    private TaaktemplateDTO toDTO(Taaktemplate t) {
        return new TaaktemplateDTO(t.getId(), t.getType(), t.getOmschrijving(), t.getDuurTijd(), t.getStatus());
    }

    @Test
    void lijstWordtCorrectOpgevuld() {
        assertEquals(3, table.getFilteredTemplates().size());
    }

    @Test
    void filterWerktOpType() {
        table.changeFilter("her", "", null, null, alleStatussen());

        assertEquals(1,  table.getFilteredTemplates().size());
        assertEquals(TaakType.HERSTELLING, table.getFilteredTemplates().getFirst().getTemplate().type());
    }

    @Test
    void filterWerktOpOmschrijving() {
        table.changeFilter("", "her", null, null, alleStatussen());

        assertEquals(1,  table.getFilteredTemplates().size());
        assertEquals("Herstelling machine", table.getFilteredTemplates().getFirst().getTemplate().omschrijving());
    }

    @Test
    void filterWerktOpminDuurtijd() {
        table.changeFilter("", "", 30, null, alleStatussen());

        assertEquals(2,  table.getFilteredTemplates().size());
        assertEquals(30, table.getFilteredTemplates().getFirst().getTemplate().duurTijd());
    }

    @Test
    void filterWerktOpMaxDuurtijd() {
        table.changeFilter("", "", null, 30, alleStatussen());

        assertEquals(2,  table.getFilteredTemplates().size());
        assertEquals(30, table.getFilteredTemplates().getFirst().getTemplate().duurTijd());
    }

    @Test
    void filterWerktOpRangeDuurtijd() {
        table.changeFilter("", "", 15, 30, alleStatussen());

        assertEquals(2,  table.getFilteredTemplates().size());
        assertEquals(30, table.getFilteredTemplates().getFirst().getTemplate().duurTijd());
    }

    @Test
    void filterWerktOpStatus() {
        table.changeFilter("", "", null, null, Set.of("INACTIEF"));
        assertEquals(0,  table.getFilteredTemplates().size());
    }
}
