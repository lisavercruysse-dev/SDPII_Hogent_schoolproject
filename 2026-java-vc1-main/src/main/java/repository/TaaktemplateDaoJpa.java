package repository;

import domein.Taaktemplate;

public class TaaktemplateDaoJpa extends GenericDaoJpa<Taaktemplate> implements TaaktemplateDao {
    public TaaktemplateDaoJpa() {super(Taaktemplate.class);}
}
