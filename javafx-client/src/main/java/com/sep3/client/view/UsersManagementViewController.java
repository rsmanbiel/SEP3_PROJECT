package com.sep3.client.view;

import com.sep3.client.model.User;
import com.sep3.client.service.UserService;
import com.sep3.client.viewmodel.ViewModelFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * Controller for the users management view.
 */
public class UsersManagementViewController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsersManagementViewController.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button refreshButton;
    @FXML private Button addButton;
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> nameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdColumn;
    @FXML private Label pageInfoLabel;
    @FXML private Button prevButton;
    @FXML private Button nextButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label errorLabel;
    
    private ViewModelFactory viewModelFactory;
    private ViewHandler viewHandler;
    private UserService userService;
    
    private final ObservableList<User> users = FXCollections.observableArrayList();
    private int currentPage = 0;
    private int totalPages = 0;
    private int totalElements = 0;
    private final int pageSize = 20;
    
    public void init(ViewModelFactory viewModelFactory, ViewHandler viewHandler) {
        this.viewModelFactory = viewModelFactory;
        this.viewHandler = viewHandler;
        this.userService = UserService.getInstance();
        
        // Setup table columns
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
        statusColumn.setCellValueFactory(cellData -> {
            Boolean isActive = cellData.getValue().getIsActive();
            return new javafx.beans.property.SimpleStringProperty(isActive != null && isActive ? "Active" : "Inactive");
        });
        createdColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getCreatedAt() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getCreatedAt().format(DATE_FORMATTER));
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        // Bind table to observable list
        userTable.setItems(users);
        
        // Clear error label initially
        errorLabel.setText("");
        
        // Add context menu
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        editItem.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) editUser(selected);
        });
        MenuItem deleteItem = new MenuItem("Delete");
        deleteItem.setOnAction(e -> {
            User selected = userTable.getSelectionModel().getSelectedItem();
            if (selected != null) deleteUser(selected);
        });
        contextMenu.getItems().addAll(editItem, deleteItem);
        userTable.setContextMenu(contextMenu);
        
        // Load users
        loadUsers();
        
        logger.debug("Users management view controller initialized");
    }
    
    private void loadUsers() {
        loadingIndicator.setVisible(true);
        errorLabel.setText("");
        
        String query = searchField.getText();
        CompletableFuture<?> future;
        
        if (query != null && !query.trim().isEmpty()) {
            future = userService.searchUsers(query.trim(), currentPage, pageSize);
        } else {
            future = userService.getAllUsers(currentPage, pageSize);
        }
        
        future.thenAccept(response -> Platform.runLater(() -> {
            @SuppressWarnings("unchecked")
            com.sep3.client.service.ProductService.PageResponse<User> pageResponse = 
                    (com.sep3.client.service.ProductService.PageResponse<User>) response;
            
            users.clear();
            if (pageResponse.content != null) {
                users.addAll(pageResponse.content);
            }
            totalPages = pageResponse.totalPages;
            totalElements = pageResponse.totalElements;
            
            updatePageInfo();
            loadingIndicator.setVisible(false);
            
            logger.info("Loaded {} users", users.size());
        })).exceptionally(throwable -> {
            Platform.runLater(() -> {
                loadingIndicator.setVisible(false);
                errorLabel.setText("Failed to load users: " + throwable.getMessage());
                logger.error("Failed to load users", throwable);
            });
            return null;
        });
    }
    
    @FXML
    private void handleSearch() {
        logger.debug("Searching users");
        currentPage = 0;
        loadUsers();
    }
    
    @FXML
    private void handleRefresh() {
        logger.debug("Refreshing users");
        searchField.clear();
        currentPage = 0;
        loadUsers();
    }
    
    @FXML
    private void handleAdd() {
        logger.debug("Adding new user");
        showUserDialog(null);
    }
    
    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            loadUsers();
        }
    }
    
    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadUsers();
        }
    }
    
    private void editUser(User user) {
        logger.debug("Editing user: {}", user.getUsername());
        showUserDialog(user);
    }
    
    private void showUserDialog(User user) {
        Dialog<User> dialog = new Dialog<>();
        dialog.setTitle(user == null ? "Add New User" : "Edit User");
        dialog.setHeaderText(user == null ? "Enter user details" : "Update user details");
        
        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        javafx.scene.control.TextField usernameField = new javafx.scene.control.TextField();
        usernameField.setPromptText("Username");
        javafx.scene.control.TextField emailField = new javafx.scene.control.TextField();
        emailField.setPromptText("Email");
        javafx.scene.control.PasswordField passwordField = new javafx.scene.control.PasswordField();
        passwordField.setPromptText("Password");
        javafx.scene.control.TextField firstNameField = new javafx.scene.control.TextField();
        firstNameField.setPromptText("First Name");
        javafx.scene.control.TextField lastNameField = new javafx.scene.control.TextField();
        lastNameField.setPromptText("Last Name");
        javafx.scene.control.TextField phoneField = new javafx.scene.control.TextField();
        phoneField.setPromptText("Phone");
        javafx.scene.control.ComboBox<RoleItem> roleCombo = new javafx.scene.control.ComboBox<>();
        roleCombo.getItems().addAll(
                new RoleItem(1L, "ADMIN"),
                new RoleItem(2L, "SUPERVISOR"),
                new RoleItem(3L, "WAREHOUSE_OPERATOR"),
                new RoleItem(4L, "CUSTOMER")
        );
        roleCombo.setPromptText("Select Role");
        roleCombo.setCellFactory(listView -> new javafx.scene.control.ListCell<RoleItem>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        roleCombo.setButtonCell(new javafx.scene.control.ListCell<RoleItem>() {
            @Override
            protected void updateItem(RoleItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        
        if (user != null) {
            usernameField.setText(user.getUsername() != null ? user.getUsername() : "");
            emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            firstNameField.setText(user.getFirstName() != null ? user.getFirstName() : "");
            lastNameField.setText(user.getLastName() != null ? user.getLastName() : "");
            phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
            // Find and set the role
            if (user.getRoleId() != null) {
                roleCombo.getItems().stream()
                        .filter(r -> r.getId().equals(user.getRoleId()))
                        .findFirst()
                        .ifPresent(roleCombo::setValue);
            }
            usernameField.setEditable(false);
            passwordField.setPromptText("Leave empty to keep current password");
        }
        
        grid.add(new javafx.scene.control.Label("Username:"), 0, 0);
        grid.add(usernameField, 1, 0);
        grid.add(new javafx.scene.control.Label("Email:"), 0, 1);
        grid.add(emailField, 1, 1);
        grid.add(new javafx.scene.control.Label("Password:"), 0, 2);
        grid.add(passwordField, 1, 2);
        grid.add(new javafx.scene.control.Label("First Name:"), 0, 3);
        grid.add(firstNameField, 1, 3);
        grid.add(new javafx.scene.control.Label("Last Name:"), 0, 4);
        grid.add(lastNameField, 1, 4);
        grid.add(new javafx.scene.control.Label("Phone:"), 0, 5);
        grid.add(phoneField, 1, 5);
        grid.add(new javafx.scene.control.Label("Role:"), 0, 6);
        grid.add(roleCombo, 1, 6);
        
        dialog.getDialogPane().setContent(grid);
        
        javafx.scene.control.ButtonType saveButtonType = new javafx.scene.control.ButtonType("Save", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, javafx.scene.control.ButtonType.CANCEL);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    // Validate required fields
                    if (user == null) {
                        if (usernameField.getText().trim().isEmpty()) {
                            showError("Username is required");
                            return null;
                        }
                        if (emailField.getText().trim().isEmpty()) {
                            showError("Email is required");
                            return null;
                        }
                        if (passwordField.getText().isEmpty()) {
                            showError("Password is required");
                            return null;
                        }
                        if (firstNameField.getText().trim().isEmpty()) {
                            showError("First name is required");
                            return null;
                        }
                        if (lastNameField.getText().trim().isEmpty()) {
                            showError("Last name is required");
                            return null;
                        }
                        if (roleCombo.getValue() == null) {
                            showError("Role is required");
                            return null;
                        }
                        
                        UserService.CreateUserRequest createRequest = new UserService.CreateUserRequest(
                                usernameField.getText().trim(),
                                emailField.getText().trim(),
                                passwordField.getText(),
                                firstNameField.getText().trim(),
                                lastNameField.getText().trim(),
                                phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim(),
                                null, null, null, null,
                                roleCombo.getValue().getId()
                        );
                        
                        userService.createUser(createRequest)
                                .thenAccept(u -> Platform.runLater(() -> {
                                    loadUsers();
                                    logger.info("User created: {}", u.getUsername());
                                    showSuccess("User created successfully");
                                }))
                                .exceptionally(throwable -> {
                                    Platform.runLater(() -> {
                                        String errorMsg = throwable.getMessage();
                                        if (errorMsg != null && errorMsg.contains("duplicate")) {
                                            showError("Username or email already exists");
                                        } else {
                                            showError("Failed to create user: " + (errorMsg != null ? errorMsg : "Unknown error"));
                                        }
                                    });
                                    return null;
                                });
                    } else {
                        if (emailField.getText().trim().isEmpty()) {
                            showError("Email is required");
                            return null;
                        }
                        if (firstNameField.getText().trim().isEmpty()) {
                            showError("First name is required");
                            return null;
                        }
                        if (lastNameField.getText().trim().isEmpty()) {
                            showError("Last name is required");
                            return null;
                        }
                        if (roleCombo.getValue() == null) {
                            showError("Role is required");
                            return null;
                        }
                        
                        UserService.UpdateUserRequest updateRequest = new UserService.UpdateUserRequest(
                                emailField.getText().trim(),
                                passwordField.getText().isEmpty() ? null : passwordField.getText(),
                                firstNameField.getText().trim(),
                                lastNameField.getText().trim(),
                                phoneField.getText().trim().isEmpty() ? null : phoneField.getText().trim(),
                                null, null, null, null,
                                roleCombo.getValue().getId(),
                                true
                        );
                        
                        userService.updateUser(user.getId(), updateRequest)
                                .thenAccept(u -> Platform.runLater(() -> {
                                    loadUsers();
                                    logger.info("User updated: {}", u.getUsername());
                                    showSuccess("User updated successfully");
                                }))
                                .exceptionally(throwable -> {
                                    Platform.runLater(() -> {
                                        String errorMsg = throwable.getMessage();
                                        if (errorMsg != null && errorMsg.contains("duplicate")) {
                                            showError("Email already exists");
                                        } else {
                                            showError("Failed to update user: " + (errorMsg != null ? errorMsg : "Unknown error"));
                                        }
                                    });
                                    return null;
                                });
                    }
                } catch (Exception e) {
                    showError("Invalid input: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void deleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Delete User");
        confirmAlert.setHeaderText("Are you sure you want to deactivate this user?");
        confirmAlert.setContentText("User: " + user.getUsername() + " - " + user.getFullName());
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.deleteUser(user.getId())
                        .thenAccept(v -> Platform.runLater(() -> {
                            loadUsers();
                            logger.info("User deleted: {}", user.getUsername());
                        }))
                        .exceptionally(throwable -> {
                            Platform.runLater(() -> {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Error");
                                alert.setContentText("Failed to delete user: " + throwable.getMessage());
                                alert.showAndWait();
                            });
                            return null;
                        });
            }
        });
    }
    
    private void updatePageInfo() {
        int current = currentPage + 1;
        pageInfoLabel.setText(String.format("Page %d of %d (%d items)", current, totalPages, totalElements));
        
        prevButton.setDisable(currentPage == 0);
        nextButton.setDisable(currentPage >= totalPages - 1);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Helper class for role selection
    private static class RoleItem {
        private final Long id;
        private final String name;
        
        public RoleItem(Long id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public Long getId() { return id; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return name;
        }
    }
}

