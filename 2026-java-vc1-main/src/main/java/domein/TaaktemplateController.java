package domein;

import dto.TaaktemplateDTO;
import dto.TaaktemplateInputDTO;
import repository.TaaktemplateDaoJpa;

import java.util.List;

public class TaaktemplateController {
    private final TaaktemplateManager taaktemplateManager;

    public TaaktemplateController() {
        this.taaktemplateManager = new TaaktemplateManager(new TaaktemplateDaoJpa());
    }

    public TaaktemplateDTO toDTO(Taaktemplate t) {
        return new TaaktemplateDTO(t.getId(), t.getType(), t.getOmschrijving(), t.duurTijd, t.getStatus());
    }

    public List<TaaktemplateDTO> getAllTemplates() {
        List<Taaktemplate> templates = taaktemplateManager.getAllTemplates();
        return templates.stream().map(this::toDTO).toList();
    }

    public TaaktemplateDTO saveTemplate(TaaktemplateInputDTO dto) {
        Taaktemplate t = taaktemplateManager.saveTemplate(dto);
        return toDTO(t);
    }

    public TaaktemplateDTO deleteTemplate(TaaktemplateDTO template) {
        return toDTO(taaktemplateManager.deleteTemplate(template));
    }
}
