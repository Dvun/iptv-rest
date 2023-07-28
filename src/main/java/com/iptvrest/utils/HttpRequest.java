package com.iptvrest.utils;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.Duration;

@Getter
@Service
public class HttpRequest {

    private Integer statusCode;
    private Object response;

    public void start(String urlString) {
        HttpClient httpClient = HttpClient.newHttpClient();
        java.net.http.HttpRequest httpRequest = java.net.http.HttpRequest.newBuilder().uri(URI.create(urlString)).GET().timeout(Duration.ofSeconds(5)).build();

        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200) {
                response = httpResponse.body();
                statusCode = httpResponse.statusCode();
            } else {
                response = "HTTP request failed with response code: " + httpResponse.statusCode();
                statusCode = httpResponse.statusCode();
            }
        } catch (IOException | InterruptedException e) {
            response = "Error: " + e.getMessage();
        }
    }

}
