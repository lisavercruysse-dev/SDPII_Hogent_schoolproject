package repository;

import domein.Melding;
import java.util.List;

public interface MeldingDao extends GenericDao<Melding> {
    List<Melding> findAllForWerknemer(int werknemerId);
}