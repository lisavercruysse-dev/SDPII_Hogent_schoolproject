package repository;

import domein.Melding;
import java.util.List;

public class MeldingDaoJpa extends GenericDaoJpa<Melding> implements MeldingDao {

    public MeldingDaoJpa() {
        super(Melding.class);
    }

    @Override
    public List<Melding> findAllForWerknemer(int werknemerId) {
        em.clear();
        return em.createQuery("SELECT m FROM Melding m WHERE m.werknemerId = :wid", Melding.class)
                .setParameter("wid", werknemerId)
                .getResultList();
    }
}