package domein;

import dto.MachineInputDTO;
import repository.MachineDao;
import repository.SiteDao;
import repository.SiteDaoJpa;
import java.time.LocalDate;
import java.util.List;

public class MachineManager {
    private final MachineDao machineRepo;
    private final SiteDao siteRepo;

    public MachineManager(MachineDao machineRepo) {
        this.machineRepo = machineRepo;
        this.siteRepo = new SiteDaoJpa();
    }

    public Machine getMachineById(int id) {
        return machineRepo.get(id);
    }

    public List<Machine> getAllActiveMachines() {
        List<Machine> machines = machineRepo.findAllActive();
        machines.forEach(Machine::controleerOnderhoudStatus);
        return machines;
    }

    public void addMachine(MachineInputDTO dto) {
        machineRepo.startTransaction();
        try {
            MachineStatus ms = MachineStatus.valueOf(dto.status().replace(" ", "_").toUpperCase());
            ProductieStatus ps = ProductieStatus.valueOf(dto.productieStatus().toUpperCase());

            Machine newMachine = Machine.builder()
                    .code(dto.code())
                    .productinfo(dto.productinfo())
                    .status(ms)
                    .productieStatus(ps)
                    .uptimeMinuten(0)
                    .datumLaatsteOnderhoud(LocalDate.now())
                    .build();

            machineRepo.insertWithLocation(newMachine, dto.siteId(), dto.locatieInSite());
            machineRepo.commitTransaction();
        } catch (Exception e) {
            machineRepo.rollbackTransaction();
            throw e;
        }
    }

    public void updateMachine(int id, MachineInputDTO dto) {
        machineRepo.startTransaction();
        try {
            Machine m = machineRepo.get(id);
            if (m != null) {
                MachineStatus ms = MachineStatus.valueOf(dto.status().replace(" ", "_").toUpperCase());
                ProductieStatus ps = ProductieStatus.valueOf(dto.productieStatus().toUpperCase());

                m.setCode(dto.code());
                m.setProductinfo(dto.productinfo());
                m.setStatus(ms);
                m.setProductieStatus(ps);

                machineRepo.updateWithLocation(m, dto.siteId(), dto.locatieInSite());
            }
            machineRepo.commitTransaction();
        } catch (Exception e) {
            machineRepo.rollbackTransaction();
            throw e;
        }
    }

    public void softDeleteMachine(int id) {
        Machine m = machineRepo.get(id);
        if (m != null) {
            m.softDelete();
            machineRepo.startTransaction();
            machineRepo.update(m);
            machineRepo.commitTransaction();
        }
    }
}