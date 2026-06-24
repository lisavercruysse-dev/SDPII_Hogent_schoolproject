package repository;

import domein.Machine;
import domein.Site;
import java.util.List;

public class MachineDaoJpa extends GenericDaoJpa<Machine> implements MachineDao {

    public MachineDaoJpa() {
        super(Machine.class);
    }

    @Override
    public List<Machine> findAllActive() {
        em.clear();
        List<Machine> machines = em.createQuery("SELECT m FROM Machine m WHERE m.isDeleted = false", Machine.class).getResultList();

        for (Machine m : machines) {
            try {
                Object[] row = (Object[]) em.createNativeQuery(
                        "SELECT siteId, location FROM site_machines WHERE machineId = ?"
                ).setParameter(1, m.getId()).getSingleResult();

                if (row != null) {
                    int siteId = ((Number) row[0]).intValue();
                    m.setLocatieInSite((String) row[1]);

                    if (m.getSite() == null) {
                        Site site = em.find(Site.class, siteId);
                        m.setSite(site);
                    }
                }
            } catch (Exception e) {}
        }
        return machines;
    }

    @Override
    public void insertWithLocation(Machine machine, int siteId, String location) {
        this.insert(machine);
        em.flush();
        em.createNativeQuery("INSERT INTO site_machines (siteId, machineId, location) VALUES (?, ?, ?)")
                .setParameter(1, siteId)
                .setParameter(2, machine.getId())
                .setParameter(3, location)
                .executeUpdate();
    }

    @Override
    public void updateWithLocation(Machine machine, int siteId, String location) {
        this.update(machine);
        em.flush();
        em.createNativeQuery("UPDATE site_machines SET siteId = ?, location = ? WHERE machineId = ?")
                .setParameter(1, siteId)
                .setParameter(2, location)
                .setParameter(3, machine.getId())
                .executeUpdate();
    }
}