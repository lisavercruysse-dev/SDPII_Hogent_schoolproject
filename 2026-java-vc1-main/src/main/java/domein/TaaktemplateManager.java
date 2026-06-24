package domein;

import dto.TaaktemplateDTO;
import dto.TaaktemplateInputDTO;
import exception.TaaktemplateInformationException;
import repository.TaaktemplateDao;

import java.util.List;

public class TaaktemplateManager {
    private final TaaktemplateDao taaktemplateRepo;

    public TaaktemplateManager(TaaktemplateDao taaktemplateRepository) {
        taaktemplateRepo = taaktemplateRepository;
    }

    public List<Taaktemplate>  getAllTemplates() {
        List<Taaktemplate> templates = taaktemplateRepo.findAll();
        return templates;
    }

    public Taaktemplate saveTemplate(TaaktemplateInputDTO dto) {
        Taaktemplate template;

        if (dto.id() != null) {
            template = taaktemplateRepo.get(dto.id());
            template.errors.clear();
            template.setType(dto.type());
            template.setOmschrijving(dto.omschrijving());
            template.setDuurTijd(dto.duurTijd());

            if (!template.errors.isEmpty()) {
                throw new TaaktemplateInformationException(template.errors);
            }

        } else {
            template = new Taaktemplate(dto.type(), dto.omschrijving(), dto.duurTijd());
        }

        taaktemplateRepo.startTransaction();
        try {
            if (dto.id() != null) {
                taaktemplateRepo.update(template);
            } else taaktemplateRepo.insert(template);
            taaktemplateRepo.commitTransaction();
        } catch(Exception ex) {
            taaktemplateRepo.rollbackTransaction();
            throw ex;
        }
        return template;
    }

    public Taaktemplate deleteTemplate(TaaktemplateDTO dto) {
        Taaktemplate template = taaktemplateRepo.get(dto.id());
        if (template == null) {
            throw new IllegalArgumentException("Er is geen template geselecteerd");
        }

        template.setStatus(EntityStatus.INACTIEF);

        taaktemplateRepo.startTransaction();
        try {
            taaktemplateRepo.update(template);
            taaktemplateRepo.commitTransaction();
        } catch(Exception ex) {
            taaktemplateRepo.rollbackTransaction();
            throw ex;
        }

        return template;
    }
}
