package domein;

import dto.MachineDTO;
import dto.MachineInputDTO;
import repository.MachineDaoJpa;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class MachineController {
    private final MachineManager machineManager;

    public MachineController() {
        this.machineManager = new MachineManager(new MachineDaoJpa());
    }

    public List<MachineDTO> getAllMachines() {
        List<Machine> machines = machineManager.getAllActiveMachines();

        return machines.stream().map(m -> {
            long dagenSinds = m.getDatumLaatsteOnderhoud() != null
                    ? ChronoUnit.DAYS.between(m.getDatumLaatsteOnderhoud(), LocalDate.now())
                    : 0;

            long dagen = m.getUptimeMinuten() / (24 * 60);
            long uren = (m.getUptimeMinuten() % (24 * 60)) / 60;
            String uptimeStr = dagen + " dagen " + uren + " uur";

            return new MachineDTO(
                    m.getId(),
                    m.getCode(),
                    m.getSite() != null ? m.getSite().getName() : "Onbekend",
                    m.getLocatieInSite(),
                    m.getProductinfo(),
                    m.getStatus() != null ? m.getStatus().name().replace("_", " ") : MachineStatus.GESTOPT.name(),
                    m.getProductieStatus() != null ? m.getProductieStatus().name() : ProductieStatus.OFFLINE.name(),
                    uptimeStr,
                    m.getDatumLaatsteOnderhoud(),
                    dagenSinds
            );
        }).toList();
    }

    public void voegMachineToe(MachineInputDTO dto) {
        machineManager.addMachine(dto);
    }

    public void wijzigMachine(int id, MachineInputDTO dto) {
        machineManager.updateMachine(id, dto);
    }

    public void verwijderMachine(int id) {
        machineManager.softDeleteMachine(id);
    }
}