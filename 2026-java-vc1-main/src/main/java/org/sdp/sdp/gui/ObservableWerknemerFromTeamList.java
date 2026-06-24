package org.sdp.sdp.gui;

import domein.WerknemerController;
import dto.WerknemerDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;

import java.util.List;

public class ObservableWerknemerFromTeamList {

    private WerknemerController controller;
    @Getter
    private final ObservableList<ObservableWerknemer> observableList;
    private int teamId;
    private final ObservableTeam observableTeam;

    public ObservableWerknemerFromTeamList(WerknemerController controller, int teamId, ObservableTeam observableTeam) {
        this.controller = controller;
        this.observableList = FXCollections.observableArrayList();
        this.teamId = teamId;
        this.observableTeam = observableTeam;

        List<WerknemerDTO> werknemers = this.controller.getWerknemersFromTeam(teamId);
        if (werknemers != null) {
            werknemers.forEach(w -> observableList.add(new ObservableWerknemer(w)));
        }
    }

    public ObservableWerknemer addWerknemer(WerknemerDTO werknemer) {
        controller.voegWerknemerToeAanTeam(werknemer.id(), teamId);
        ObservableWerknemer ow = new ObservableWerknemer(werknemer);
        observableList.add(ow);
        return ow;
    }

    public void verwijderWerknemerUitTeam(ObservableWerknemer observableWerknemer) {
        controller.verwijderWerknemerUitTeam(observableWerknemer.getId(), teamId);
        observableList.remove(observableWerknemer);
    }
}

