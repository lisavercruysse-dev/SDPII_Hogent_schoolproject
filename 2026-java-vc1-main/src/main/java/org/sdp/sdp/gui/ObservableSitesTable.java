package org.sdp.sdp.gui;

import domein.*;
import dto.MachineDTO;
import dto.SiteDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class ObservableSitesTable {

    private final SiteController controller;
    private final ObservableList<ObservableSite> sitesObservableList;
    @Getter
    private final FilteredList<ObservableSite> filteredList;

    public ObservableSitesTable(SiteController controller) {
        this.controller = controller;
        this.sitesObservableList = FXCollections.observableArrayList();
        this.filteredList   = new FilteredList<>(sitesObservableList, p -> true);
        laadData();
    }

    // ── Data laden / verversen ─────────────────────────────────────────────────
    private void laadData() {
        List<ObservableSite> sites = controller.getAllSites().stream()
                .map(ObservableSite::new)
                .toList();
        sitesObservableList.setAll(sites);
    }

    public void refresh() {
        laadData();
    }

    public FilteredList<ObservableSite> getFilteredList() {
        return filteredList;
    }

    // ── Filter ─────────────────────────────────────────────────────────────────
    /**
     * Past het predikaat van de FilteredList aan op basis van alle filterparameters.
     *
     * @param naam                 vrije tekst op naam (leeg = geen filter)
     * @param locatie              vrije tekst op locatie (leeg = geen filter)
     * @param land                 vrije tekst op land (leeg = geen filter)
     * @param minCapaciteit        ondergrens capaciteit (null = geen ondergrens)
     * @param maxCapaciteit        bovengrens capaciteit (null = geen bovengrens)
     * @param toegestaneOpStatussen  set van toegestane OperationeleStatus-namen (leeg = niets zichtbaar)
     * @param toegestaneProdStatussen set van toegestane ProductieStatus-namen (leeg = niets zichtbaar)
     */
    public void changeFilter(String naam,
                             String locatie,
                             String land,
                             Integer minCapaciteit,
                             Integer maxCapaciteit,
                             Set<String> toegestaneOpStatussen,
                             Set<String> toegestaneProdStatussen) {
        filteredList.setPredicate(site -> {
            // ── Naam ──────────────────────────────────────────────────────────
            if (naam != null && !naam.isBlank()) {
                if (!site.nameProperty().get().toLowerCase()
                        .contains(naam.trim().toLowerCase())) return false;
            }

            // ── Locatie ───────────────────────────────────────────────────────
            if (locatie != null && !locatie.isBlank()) {
                if (!site.locatieProperty().get().toLowerCase()
                        .contains(locatie.trim().toLowerCase())) return false;
            }

            // ── Land ───────────────────────────────────────────────────────
            if (land != null && !land.isBlank()) {
                if (!site.landProperty().get().toLowerCase()
                        .contains(land.trim().toLowerCase())) return false;
            }

            // ── Capaciteit range ──────────────────────────────────────────────
            int cap = parseIntOrZero(site.getCapaciteit());
            if (minCapaciteit != null && cap < minCapaciteit) return false;
            if (maxCapaciteit != null && cap > maxCapaciteit) return false;

            // ── Operationele Status (checkboxes) ──────────────────────────────
            if (toegestaneOpStatussen != null
                    && !toegestaneOpStatussen.contains(
                    site.operationeleStatusProperty().get().toUpperCase())) {
                return false;
            }

            // ── Productie Status (checkboxes) ─────────────────────────────────
            if (toegestaneProdStatussen != null
                    && !toegestaneProdStatussen.contains(
                    site.productieStatusProperty().get().toUpperCase())) {
                return false;
            }

            return true;
        });
    }

    private int parseIntOrZero(String s) {
        try { return (s == null || s.isBlank()) ? 0 : Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return 0; }
    }
}