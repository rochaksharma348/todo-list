package com.rochaksharma.todo;

import TodoList.ListItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class EditDialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadLinePicker;

    public void prepare(ListItem item) {
        shortDescriptionField.setText(item.getShortDescription());
        detailsArea.setText(item.getLongDescription());
        deadLinePicker.setValue(item.getDeadLine());
    }

    public ListItem processResults() {
        String shortDescription = shortDescriptionField.getText();
        String details = detailsArea.getText();
        LocalDate deadLine = deadLinePicker.getValue();

        ListItem item = new ListItem(shortDescription, details, deadLine);
        return item;
    }
}
