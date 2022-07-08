package com.rochaksharma.todo;

import TodoList.ListItem;
import TodoList.TodoData;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsArea;
    @FXML
    private DatePicker deadLinePicker;

    public ListItem processResults() {
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsArea.getText().trim();
        LocalDate deadLine = deadLinePicker.getValue();

        ListItem item = new ListItem(shortDescription, details, deadLine);
        TodoData.getInstance().addTodoItem(item);
        return item;
    }
}
