import domein.MachineController;
import domein.MachineStatus;
import domein.ProductieStatus;
import dto.MachineDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sdp.sdp.gui.ObservableMachinesTable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ObservableMachinesTableTest {

    @Mock
    private MachineController controller;
    private ObservableMachinesTable table;

    @BeforeEach
    void setUp() {
        MachineDTO m1 = new MachineDTO(1, "MC-PLT-001", "Site A", "Site Noord", "1.001", "DRAAIT", "GEZOND", "Hout", LocalDate.of(2026, 3, 2), 1);
        MachineDTO m2 = new MachineDTO(2, "MC-PLT-002", "Site B", "Site Noord", "1.002", "NOOD_AAN_ONDERHOUD", "FALEND", "Metaal", LocalDate.of(2026, 4, 15), 40);
        MachineDTO m3 = new MachineDTO(3, "MC-PLT-003", "Site C", "Site A", "2.001", "GESTOPT", "OFFLINE", "Plastic", LocalDate.of(2026, 5, 10), 45);

        when(controller.getAllMachines()).thenReturn(List.of(m1, m2, m3));

        table = new ObservableMachinesTable(controller);
    }

    private Set<String> alleProdStatussen() {
        return Arrays.stream(ProductieStatus.values()).map(Enum::name).collect(Collectors.toSet());
    }

    private Set<String> alleStatussen() {
        return Arrays.stream(MachineStatus.values()).map(Enum::name).collect(Collectors.toSet());
    }

    @Test
    void lijstWordtCorrectOpgevuld() {
        assertEquals(3, table.getFilteredList().size());
    }

    @Test
    void filterWerktOpMachineCode() {
        table.changeFilter("002", "", null, null, alleProdStatussen(), alleStatussen());
        assertEquals(1, table.getFilteredList().size());
        assertEquals("MC-PLT-002", table.getFilteredList().get(0).getCode());
    }

    @Test
    void filterWerktOpSite() {
        table.changeFilter("", "A", null, null, alleProdStatussen(), alleStatussen());
        assertEquals(1, table.getFilteredList().size());
    }

    @Test
    void filterWerktOpDatumRange() {
        table.changeFilter("", "", LocalDate.of(2026, 4, 1), LocalDate.of(2026, 5, 1), alleProdStatussen(), alleStatussen());
        assertEquals(1, table.getFilteredList().size());
        assertEquals("MC-PLT-002", table.getFilteredList().get(0).getCode());
    }

    @Test
    void filterWerktOpStatus() {
        table.changeFilter("", "", null, null, alleProdStatussen(), Set.of("DRAAIT"));
        assertEquals(1, table.getFilteredList().size());
        assertEquals("MC-PLT-001", table.getFilteredList().get(0).getCode());
    }
}