package com.example.lmb_3_7;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

public class Main extends Application {
    private ObservableList<Product> products;
    private static final String DATA_FILE = "data.json";
    public TableView<Product> table;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Управління Складом");

        products = FXCollections.observableArrayList();

        loadDataFromJson(); // Завантаження даних з JSON файлу

        VBox grid = new VBox(10);

        // Елементи для введення даних товару
        Label nameLabel = new Label("Назва:");
        TextField nameInput = new TextField();
        nameInput.setPrefWidth(400); // Встановити ширину поле "Назва"

        Label quantityLabel = new Label("Кількість:");
        TextField quantityInput = new TextField();

        Label locationLabel = new Label("Місцерозташування:");
        TextField locationInput = new TextField();

        Label descriptionLabel = new Label("Опис:");
        TextField descriptionInput = new TextField();

        Label priceLabel = new Label("Ціна:");
        TextField priceInput = new TextField();

        Button addButton = new Button("Додати");

        Button deleteButton = new Button("Видалити");

        table = new TableView<>();
        table.setItems(products);
        // Визначення стовпців таблиці
        TableColumn<Product, String> nameColumn = new TableColumn<>("Назва");
        nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());
        TableColumn<Product, Integer> quantityColumn = new TableColumn<>("Кількість");
        quantityColumn.setCellValueFactory(data -> data.getValue().quantityProperty().asObject());
        TableColumn<Product, String> locationColumn = new TableColumn<>("Місцерозташування");
        locationColumn.setCellValueFactory(data -> data.getValue().locationProperty());
        TableColumn<Product, String> descriptionColumn = new TableColumn<>("Опис");
        descriptionColumn.setCellValueFactory(data -> data.getValue().descriptionProperty());
        TableColumn<Product, Double> priceColumn = new TableColumn<>("Ціна");
        priceColumn.setCellValueFactory(data -> data.getValue().priceProperty().asObject());
        TableColumn<Product, Void> editColumn = new TableColumn<>("Змінити");
        table.getColumns().addAll(nameColumn, quantityColumn, locationColumn, descriptionColumn, priceColumn);
        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<>() {
                    private final Button editButton = new Button("Змінити");

                    {
                        // Обробник події для кнопки "Змінити"
                        editButton.setOnAction(event -> {
                            Product product = getTableView().getItems().get(getIndex());
                            showEditDialog(product);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(editButton);
                        }
                    }
                };
                return cell;
            }
        };
        editColumn.setCellFactory(cellFactory);
        table.getColumns().add(editColumn);

        deleteButton.setOnAction(e -> {
            // Отримання виділеного товару для видалення
            Product selectedProduct = table.getSelectionModel().getSelectedItem();
            if (selectedProduct != null) {
                products.remove(selectedProduct);
                saveDataToJson(); // Збереження даних в JSON файл
            }
        });

        Button searchButton = new Button("Пошук");
        TextField searchInput = new TextField();
        searchInput.setPrefWidth(600); // Встановити ширину поле "Назва"

        addButton.setOnAction(e -> {
            // Отримання введених даних
            String name = nameInput.getText();
            String quantityText = quantityInput.getText();
            String location = locationInput.getText();
            String description = descriptionInput.getText();
            String priceText = priceInput.getText();

            // Перевірка наявності введених даних
            if (name.isEmpty() || quantityText.isEmpty() || location.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                // Виведення повідомлення про неправильне заповнення всіх полів
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, заповніть всі поля.");
                alert.showAndWait();
                return; // Припинення виконання методу
            }

            // Перевірка правильності введення числових значень
            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(quantityText);
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                // Виведення повідомлення про неправильний формат чисел
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть правильні числові значення для кількості та ціни.");
                alert.showAndWait();
                return; // Припинення виконання методу
            }

            // Додавання нового товару
            Product newProduct = new Product(name, quantity, location, description, price);
            products.add(newProduct);

            // Очищення полів введення
            nameInput.clear();
            quantityInput.clear();
            locationInput.clear();
            descriptionInput.clear();
            priceInput.clear();

            saveDataToJson(); // Збереження даних в JSON файл
        });


        // Додавання елементів до гріда
        GridPane gridPane = new GridPane();
        gridPane.setHgap(50); // Відстань між стовпцями
        gridPane.setVgap(15); // Відстань між рядками

        gridPane.addRow(0, nameLabel, nameInput);
        gridPane.addRow(1, quantityLabel, quantityInput);
        gridPane.addRow(2, locationLabel, locationInput);
        gridPane.addRow(3, descriptionLabel, descriptionInput);
        gridPane.addRow(4, priceLabel, priceInput);

        grid.getChildren().addAll(
                createCenteredHBox(gridPane),
                createCenteredHBox(addButton, deleteButton)
        );

        grid.getChildren().addAll(table, createCenteredHBox(searchButton, searchInput));

        Scene scene = new Scene(grid, 1000, 600); // Змінено розмір вікна
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Метод для створення центрованого HBox
    private HBox createCenteredHBox(Node... nodes) {
        HBox hbox = new HBox(10);
        hbox.setAlignment(Pos.CENTER);
        hbox.getChildren().addAll(nodes);
        return hbox;
    }

    private void loadDataFromJson() {
        try {
            File file = new File(DATA_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                Gson gson = new Gson();
                Type productListType = new TypeToken<List<Product>>() {}.getType();
                List<Product> productList = gson.fromJson(reader, productListType);

                if (productList != null) {
                    products.addAll(productList);
                }

                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToJson() {
        try {
            File file = new File(DATA_FILE);
            FileWriter writer = new FileWriter(file);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(products);

            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showEditDialog(Product product) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle("Редагування товару");

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        // Елементи для редагування даних товару
        Label nameLabel = new Label("Назва:");
        TextField nameInput = new TextField(product.getName());

        Label quantityLabel = new Label("Кількість:");
        TextField quantityInput = new TextField(Integer.toString(product.getQuantity()));

        Label locationLabel = new Label("Місцерозташування:");
        TextField locationInput = new TextField(product.getLocation());

        Label descriptionLabel = new Label("Опис:");
        TextField descriptionInput = new TextField(product.getDescription());

        Label priceLabel = new Label("Ціна:");
        TextField priceInput = new TextField(Double.toString(product.getPrice()));

        Button saveButton = new Button("Зберегти");
        saveButton.setOnAction(e -> {
            // Отримання введених даних товару
            String name = nameInput.getText();
            String quantityText = quantityInput.getText();
            String location = locationInput.getText();
            String description = descriptionInput.getText();
            String priceText = priceInput.getText();

            // Перевірка наявності введених даних
            if (name.isEmpty() || quantityText.isEmpty() || location.isEmpty() || description.isEmpty() || priceText.isEmpty()) {
                // Виведення повідомлення про неправильне заповнення всіх полів
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, заповніть всі поля.");
                alert.showAndWait();
                return; // Припинення виконання методу
            }

            // Перевірка правильності введення числових значень
            int quantity;
            double price;
            try {
                quantity = Integer.parseInt(quantityText);
                price = Double.parseDouble(priceText);
            } catch (NumberFormatException ex) {
                // Виведення повідомлення про неправильний формат чисел
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Помилка");
                alert.setHeaderText(null);
                alert.setContentText("Будь ласка, введіть правильні числові значення для кількості та ціни.");
                alert.showAndWait();
                return; // Припинення виконання методу
            }

            // Оновлення даних товару
            product.setName(name);
            product.setQuantity(quantity);
            product.setLocation(location);
            product.setDescription(description);
            product.setPrice(price);

            dialogStage.close();

            saveDataToJson(); // Збереження даних в JSON файл
            table.refresh();
        });

        vbox.getChildren().addAll(
                nameLabel, nameInput,
                quantityLabel, quantityInput,
                locationLabel, locationInput,
                descriptionLabel, descriptionInput,
                priceLabel, priceInput,
                saveButton
        );

        Scene scene = new Scene(vbox);
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
    }
}
