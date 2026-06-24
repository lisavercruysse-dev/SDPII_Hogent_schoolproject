package org.sdp.sdp.gui;

import domein.TaaktemplateController;
import dto.TaaktemplateDTO;
import dto.TaaktemplateInputDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;

public class ObservableTemplateTable {

    private final TaaktemplateController templateController;
    private final ObservableList<ObservableTemplate> observableTemplates;
    @Getter
    private final FilteredList<ObservableTemplate> filteredTemplates;

    public ObservableTemplateTable(TaaktemplateController templateController){
        this.templateController = templateController;
        this.observableTemplates = FXCollections.observableArrayList(templateController.getAllTemplates().stream().map(ObservableTemplate::new).toList());
        this.filteredTemplates = new FilteredList<>(observableTemplates, p -> true);
    }

    public void changeFilter(String type, String omschrijving, Integer min, Integer max, Set<String> toegestaneStatussen) {
        filteredTemplates.setPredicate(t -> {
            if (type != null && !type.isBlank()) {
                if (!t.typeProperty().get().toLowerCase().contains(type.toLowerCase())) return false;
            }

            if (omschrijving != null && !omschrijving.isBlank()) {
                if (!t.omschrijvingProperty().get().toLowerCase().contains(omschrijving.toLowerCase())) return false;
            }

            int duurtijd = Integer.parseInt(t.duurTijdProperty().get());
            if (min != null && duurtijd < min) return false;
            if (max != null && duurtijd > max) return false;

            if (toegestaneStatussen != null &&
                    !toegestaneStatussen.contains(t.statusProperty().get().toUpperCase())
            ) {
                return false;
            }
            return true;
        });
    }

    private boolean matchesFilter(String value, String filter) {
        if (filter == null || filter.isBlank()) {
            return true;
        }
        return Objects.toString(value, "").toLowerCase().contains(filter.toLowerCase());
    }

    public ObservableTemplate saveTemplate(TaaktemplateInputDTO dto) {
        TaaktemplateDTO template = templateController.saveTemplate(dto);

        if (dto.id() == null) {
            ObservableTemplate ot = new ObservableTemplate(template);
            observableTemplates.add(ot);
            return ot;
        } else {
            ObservableTemplate existing = observableTemplates.stream().filter(ot -> ot.getId() == dto.id()).findFirst().orElse(null);
            if (existing != null) {
                existing.update(template);
            }
            return existing;
        }
    }

    public void removeTemplate(ObservableTemplate template) {
        TaaktemplateDTO updated = templateController.deleteTemplate(template.getTemplate());
        template.statusProperty().set(updated.status().toString().toLowerCase());
    }
}
