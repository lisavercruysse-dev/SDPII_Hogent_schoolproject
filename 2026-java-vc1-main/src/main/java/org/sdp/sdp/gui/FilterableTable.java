package org.sdp.sdp.gui;

import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.w3c.dom.Text;

import java.util.List;
import java.util.Map;

public interface FilterableTable {

    TableView<?> getTable();

    void updateFilter();

    default Label createSortArrow(TableColumn<?, ?> col) {
        Label sortArrow = new Label("");

        col.sortTypeProperty().addListener((obs, old, nw) -> {
            if (getTable().getSortOrder().contains(col)) {
                sortArrow.setText(nw == TableColumn.SortType.ASCENDING ? " ▲" : " ▼");
            } else {
                sortArrow.setText("");
            }
        });

        getTable().getSortOrder().addListener(
                (javafx.collections.ListChangeListener<TableColumn>) change -> {
                    if (getTable().getSortOrder().contains(col)) {
                        sortArrow.setText(col.getSortType() == TableColumn.SortType.ASCENDING ? " ▲" : " ▼");
                    } else {
                        sortArrow.setText("");
                    }
                });

        return sortArrow;
    }

    default TextField setupTextFilter(TableColumn<?, ?> col, String label) {
        Label lbl = new Label(label);
        TextField tf = new TextField();

        tf.setPromptText("filter...");
        tf.setStyle("""
        -fx-background-color: transparent;
        -fx-padding: 12 6 12 6;
        -fx-border-color: #e5e5e5;
        -fx-border-width: 1 0 1 0;
        """);

        lbl.setStyle("-fx-font-weight: bold;");
        lbl.setMaxWidth(Double.MAX_VALUE);
        lbl.setAlignment(Pos.CENTER_LEFT);

        HBox header = new HBox(lbl, createSortArrow(col));
        header.setAlignment(Pos.CENTER_LEFT);

        VBox vbox = new VBox(4, header, tf);
        vbox.setMaxWidth(Double.MAX_VALUE);
        col.setGraphic(vbox);
        col.setText("");

        tf.textProperty().addListener((obs, old, newVal) -> updateFilter());

        return tf;
    }

    default List<TextField> setupRangeFilter(TableColumn<?, ?> col, String label) {
        TextField min = new TextField();
        min.setPromptText("min");
        min.setPrefWidth(52);
        min.setStyle("-fx-font-weight: normal; -fx-padding: 2 4;");

        TextField max = new TextField();
        max.setPromptText("max");
        max.setPrefWidth(52);
        max.setStyle("-fx-font-weight: normal; -fx-padding: 2 4;");

        Label dash = new Label("–");
        dash.setStyle("-fx-padding: 0 2;");

        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-weight: bold;");

        Label arrow = createSortArrow(col);
        HBox header = new HBox(lbl, arrow);
        header.setAlignment(Pos.CENTER_LEFT);

        HBox rangeBox = new HBox(4, min, dash, max);
        rangeBox.setAlignment(Pos.CENTER);
        rangeBox.setMaxWidth(Double.MAX_VALUE);

        VBox vbox = new VBox(3, header, rangeBox);
        vbox.setMaxWidth(Double.MAX_VALUE);
        col.setGraphic(vbox);
        col.setText("");

        min.textProperty().addListener((obs, old, nw) -> updateFilter());
        max.textProperty().addListener((obs, old, nw) -> updateFilter());

        return List.of(min, max);
    }

    default void setupCheckboxFilter(TableColumn<?, ?> col, String label,
                                     List<String> waarden,
                                     Map<String, CheckBox> doelMap) {
        Label arrow = createSortArrow(col);
        Label lbl  = new Label(label);
        lbl.setStyle("-fx-font-weight: bold;");
        HBox header = new HBox(lbl, arrow);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox vbox = new VBox(3, header);
        vbox.setMaxWidth(Double.MAX_VALUE);



        for (String waarde : waarden) {
            CheckBox cb = new CheckBox(
                    waarde == null || waarde.isEmpty() ? waarde
                            : waarde.substring(0, 1).toUpperCase() + waarde.substring(1).toLowerCase()
            );
            cb.setSelected(true);
            cb.setStyle("-fx-font-weight: normal; -fx-font-size: 11px;");
            doelMap.put(waarde, cb);
            cb.selectedProperty().addListener((obs, old, nw) -> updateFilter());
            vbox.getChildren().add(cb);
        }

        col.setGraphic(vbox);
        col.setText("");
    }
}
