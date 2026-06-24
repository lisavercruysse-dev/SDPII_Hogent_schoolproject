package org.sdp.sdp.gui;

import domein.MachineController;
import dto.MachineDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ObservableMachinesTable {

    private final MachineController controller;
    private final ObservableList<ObservableMachine> machinesObservableList;
    @Getter
    private final FilteredList<ObservableMachine> filteredList;

    public ObservableMachinesTable(MachineController controller) {
        this.controller = controller;
        this.machinesObservableList = FXCollections.observableArrayList();
        this.filteredList = new FilteredList<>(machinesObservableList, m -> true);
        refresh();
    }

    public void refresh() {
        List<MachineDTO> machines = this.controller.getAllMachines();
        machinesObservableList.clear();
        if (machines != null) {
            machines.forEach(machine -> machinesObservableList.add(new ObservableMachine(machine)));
        }
    }

    public void changeFilter(String zoekCode, String zoekSite, LocalDate minDatum, LocalDate maxDatum,
                             Set<String> toegestaneProdStatus, Set<String> toegestaneStatus) {

        filteredList.setPredicate(machine -> {

            boolean matchCode = zoekCode == null || zoekCode.isBlank() ||
                    machine.getCode().toLowerCase().contains(zoekCode.toLowerCase());

            boolean matchSite = zoekSite == null || zoekSite.isBlank() ||
                    machine.siteNaamProperty().get().toLowerCase().contains(zoekSite.toLowerCase());

            boolean matchDatum = true;
            LocalDate machDatum = machine.getRuweDatumLaatsteOnderhoud();
            if (minDatum != null) {
                if (machDatum == null || machDatum.isBefore(minDatum)) matchDatum = false;
            }
            if (maxDatum != null) {
                if (machDatum == null || machDatum.isAfter(maxDatum)) matchDatum = false;
            }

            Set<String> upperProdStatussen = toegestaneProdStatus.stream().map(s -> s.toUpperCase().replace("_", " ")).collect(Collectors.toSet());
            Set<String> upperStatussen = toegestaneStatus.stream().map(s -> s.toUpperCase().replace("_", " ")).collect(Collectors.toSet());

            boolean matchProdStatus = upperProdStatussen.isEmpty() ||
                    upperProdStatussen.contains(machine.productieStatusProperty().get().toUpperCase().replace("_", " "));

            boolean matchStatus = upperStatussen.isEmpty() ||
                    upperStatussen.contains(machine.statusProperty().get().toUpperCase().replace("_", " "));

            return matchCode && matchSite && matchDatum && matchProdStatus && matchStatus;
        });
    }
}