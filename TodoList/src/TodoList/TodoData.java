package TodoList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TodoData {
    private static final TodoData instance = new TodoData();
    private static final String fileName = "TodoListItems.txt";

    private static ObservableList<ListItem> todoItems;
    private static DateTimeFormatter formatter;

    public static TodoData getInstance() {
        return instance;
    }

    private TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    }

    public ObservableList<ListItem> getTodoItems() {
        return todoItems;
    }

    public void addTodoItem(ListItem item) {
        todoItems.add(item);
    }

    public void deleteTodoItem(ListItem item) {
        todoItems.remove(item);
    }

    public void updateTodoItem(ListItem item, ListItem newItem) {
        todoItems.set(todoItems.indexOf(item), newItem);
    }
//
//    public void setTodoItems(List<ListItem> todoItems) {
//        TodoData.todoItems = todoItems;
//    }

    public void loadTodoData() throws IOException {
        todoItems = FXCollections.observableArrayList();
        Path path = Paths.get(fileName);
        BufferedReader br = Files.newBufferedReader(path);

        String input;
        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String longDescription = itemPieces[1];
                String dateString = itemPieces[2];

                LocalDate date = LocalDate.parse(dateString, formatter);
                ListItem listItem = new ListItem(shortDescription, longDescription, date);
                todoItems.add(listItem);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    public void storeTodoData() throws IOException {
        Path path = Paths.get(fileName);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<ListItem> itr = todoItems.iterator();
            while (itr.hasNext()) {
                ListItem item = itr.next();
                bw.write(String.format("%s\t%s\t%s", item.getShortDescription(), item.getLongDescription(), item.getDeadLine().format(formatter)));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }
}
