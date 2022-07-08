package com.rochaksharma.todo;

import TodoList.ListItem;
import TodoList.TodoData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {
    private final List<ListItem> todoItems = new ArrayList<>();
    @FXML
    private ListView<ListItem> myListView;

    @FXML
    private TextArea myTextArea;

    @FXML
    private Label dueDateLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu contextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<ListItem> filteredList;

    private Predicate<ListItem> wantAllItems;
    private Predicate<ListItem> wantTodaysItems;

    public void initialize() {

        contextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ListItem item = myListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ListItem item = myListView.getSelectionModel().getSelectedItem();
                editItem(item);
            }
        });

        contextMenu.getItems().setAll(deleteMenuItem);
        contextMenu.getItems().add(editMenuItem);

        myListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ListItem>() {
            @Override
            public void changed(ObservableValue<? extends ListItem> observableValue, ListItem listItem, ListItem t1) {
                if (t1 != null) {
                    ListItem item = myListView.getSelectionModel().getSelectedItem();
                    myTextArea.setText(item.getLongDescription());

                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

                    dueDateLabel.setText(df.format(item.getDeadLine()));
                }
            }
        });

        wantAllItems = new Predicate<ListItem>() {
            @Override
            public boolean test(ListItem item) {
                return true;
            }
        };

        wantTodaysItems = new Predicate<ListItem>() {
            @Override
            public boolean test(ListItem item) {
                return item.getDeadLine().equals(LocalDate.now());
            }
        };

        filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(),wantAllItems);

        SortedList<ListItem> sortedList = new SortedList<>(filteredList, new Comparator<ListItem>() {
            @Override
            public int compare(ListItem o1, ListItem o2) {
                return o1.getDeadLine().compareTo(o2.getDeadLine());
            }
        });

        //myListView.setItems(TodoData.getInstance().getTodoItems());
        myListView.setItems(sortedList);
        myListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        myListView.getSelectionModel().selectFirst();

        myListView.setCellFactory(new Callback<ListView<ListItem>, ListCell<ListItem>>() {
            @Override
            public ListCell<ListItem> call(ListView<ListItem> listItemListView) {
                ListCell<ListItem> cell = new ListCell<>() {
                    @Override
                    protected void updateItem(ListItem item, boolean b) {
                        super.updateItem(item, b);
                        if (isEmpty()) {
                            setText(null);
                        }
                        else{
                            setText(item.getShortDescription());
                            if (item.getDeadLine().isBefore(LocalDate.now())) {
                                setTextFill(Color.RED);
                            }
                            else if (item.getDeadLine().equals(LocalDate.now())) {
                                setTextFill(Color.BROWN);
                            }
                            else if (item.getDeadLine().equals(LocalDate.now().plusDays(1))) {
                                setTextFill(Color.DARKORANGE);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if (isNowEmpty == null) {
                                cell.setContextMenu(null);
                            }
                            else {
                                cell.setContextMenu(contextMenu);
                            }
                        });
                return cell;
            }
        });
    }

    @FXML
    public void showNewItemDialogBox() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add todo item");
        dialog.setHeaderText("Use this dialog to add new todo item");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("Dialog.fxml"));
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("Dialog.fxml"));
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("can't load the dialog box");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            ListItem newItem = controller.processResults();
            myListView.getSelectionModel().select(newItem);
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        ListItem item = myListView.getSelectionModel().getSelectedItem();
        if (keyEvent.getCode().equals(KeyCode.DELETE)) {
            deleteItem(item);
        }
    }

    @FXML
    public void handleOnClick() {
        ListItem item = myListView.getSelectionModel().getSelectedItem();
        myTextArea.setText(item.getLongDescription());
        dueDateLabel.setText(item.getDeadLine().toString());
    }

    public void deleteItem(ListItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setTitle("Delete Todo item");
        alert.setContentText("Are you sure? press OK to delete the todo item");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == (ButtonType.OK)) {
            TodoData.getInstance().deleteTodoItem(item);
        }
    }

    public void editItem(ListItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Todo Item");
        dialog.setHeaderText("Use this dialog to edit the Todo item");
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("EditDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Can't load the dialog box.");
            e.getStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        EditDialogController controller = fxmlLoader.getController();
        controller.prepare(myListView.getSelectionModel().getSelectedItem());

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            ListItem newItem = controller.processResults();
            TodoData.getInstance().updateTodoItem(myListView.getSelectionModel().getSelectedItem(),newItem);
        }
    }

    @FXML
    public void handleFilterButton() {
        ListItem item = myListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantTodaysItems);
            if (filteredList.isEmpty()) {
                myTextArea.clear();
                dueDateLabel.setText("");
            }
            else if (filteredList.contains(item)) {
                myListView.getSelectionModel().select(item);
            }
            else {
                myListView.getSelectionModel().selectFirst();
            }
        }
        else {
            filteredList.setPredicate(wantAllItems);
            myListView.getSelectionModel().select(item);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
