package repository;

import domein.Machine;
import java.util.List;

public interface MachineDao extends GenericDao<Machine> {
    List<Machine> findAllActive();
    void insertWithLocation(Machine machine, int siteId, String location);
    void updateWithLocation(Machine machine, int siteId, String location);
}