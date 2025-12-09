package com.sep3.client.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

/**
 * HTTP Client service for REST API communication.
 */
public class HttpClientService {
    
    private static final Logger logger = LoggerFactory.getLogger(HttpClientService.class);
    private static final String BASE_URL = "http://localhost:8080/api";
    
    private final HttpClient httpClient;
    private final Gson gson;
    private String authToken;
    
    private static HttpClientService instance;
    
    private HttpClientService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>)
                        (json, type, context) -> LocalDateTime.parse(json.getAsString(), 
                                DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .create();
    }
    
    public static synchronized HttpClientService getInstance() {
        if (instance == null) {
            instance = new HttpClientService();
        }
        return instance;
    }
    
    public void setAuthToken(String token) {
        this.authToken = token;
    }
    
    public void clearAuthToken() {
        this.authToken = null;
    }
    
    public Gson getGson() {
        return gson;
    }
    
    /**
     * Perform GET request.
     */
    public <T> CompletableFuture<T> get(String endpoint, Class<T> responseType) {
        HttpRequest request = buildRequest(endpoint)
                .GET()
                .build();
        
        return sendRequest(request, responseType);
    }
    
    /**
     * Perform POST request.
     */
    public <T> CompletableFuture<T> post(String endpoint, Object body, Class<T> responseType) {
        String jsonBody = gson.toJson(body);
        
        HttpRequest request = buildRequest(endpoint)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        return sendRequest(request, responseType);
    }
    
    /**
     * Perform PUT request.
     */
    public <T> CompletableFuture<T> put(String endpoint, Object body, Class<T> responseType) {
        String jsonBody = gson.toJson(body);
        
        HttpRequest request = buildRequest(endpoint)
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
        
        return sendRequest(request, responseType);
    }
    
    /**
     * Perform DELETE request.
     */
    public CompletableFuture<Void> delete(String endpoint) {
        HttpRequest request = buildRequest(endpoint)
                .DELETE()
                .build();
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() >= 400) {
                        logger.error("DELETE request failed: {} - {}", response.statusCode(), response.body());
                        throw new RuntimeException("Request failed with status: " + response.statusCode());
                    }
                });
    }
    
    private HttpRequest.Builder buildRequest(String endpoint) {
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(30));
        
        if (authToken != null) {
            builder.header("Authorization", "Bearer " + authToken);
        }
        
        return builder;
    }
    
    private <T> CompletableFuture<T> sendRequest(HttpRequest request, Class<T> responseType) {
        logger.debug("Sending {} request to: {}", request.method(), request.uri());
        
        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    logger.debug("Response status: {}", response.statusCode());
                    
                    if (response.statusCode() >= 400) {
                        logger.error("Request failed: {} - {}", response.statusCode(), response.body());
                        throw new RuntimeException("Request failed: " + response.body());
                    }
                    
                    if (responseType == Void.class) {
                        return null;
                    }
                    
                    return gson.fromJson(response.body(), responseType);
                });
    }
}
