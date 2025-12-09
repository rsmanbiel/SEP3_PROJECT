package com.sep3.client.viewmodel;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * ViewModel for the reports/analytics view.
 */
public class ReportsViewModel {
    
    private final BooleanProperty isLoading = new SimpleBooleanProperty(false);
    private final StringProperty errorMessage = new SimpleStringProperty("");
    
    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }
    
    public void setLoading(boolean loading) {
        isLoading.set(loading);
    }
    
    public StringProperty errorMessageProperty() {
        return errorMessage;
    }
    
    public void setErrorMessage(String message) {
        errorMessage.set(message);
    }
}

