package com.sdcote.sdp;

import coyote.commons.log.Log;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 */
public class OAuthAccessTokenTracker {

    private final String tokenProviderUrl;

    /** The number of seconds before the access token expiration we want the token refresh to occur.*/
    private final long expiryWindowSeconds;
    private final HttpClient httpClient;

    // Thread-safe map to store client token data
    private final Map<String, ClientTokenData> clientDataMap = new ConcurrentHashMap<>();

    /**
     * @param tokenProviderUrl    The URL of the OAuth token endpoint.
     * @param expiryWindowSeconds The buffer time in seconds to refresh the token before it actually expires.
     */
    public OAuthAccessTokenTracker(String tokenProviderUrl, long expiryWindowSeconds) {
        this.tokenProviderUrl = tokenProviderUrl;
        this.expiryWindowSeconds = expiryWindowSeconds;
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Registers a client with an existing refresh token and secret.
     *
     * @param credentials The object containing client credentials
     */
    public void registerClient(ClientCredentials credentials) {
        ClientTokenData data = new ClientTokenData();
        data.clientSecret = credentials.getSecret();
        data.refreshToken = credentials.getRefreshToken();
        clientDataMap.put(credentials.getIdentifier(), data);
    }


    /**
     * Retrieves a valid access token. Refreshes the token if expired or within the expiry window.
     *
     * @param credentials The object containing client credentials
     * @return The valid access token.
     * @throws IOException              If the refresh network request fails.
     * @throws InterruptedException     If the operation is interrupted.
     * @throws IllegalArgumentException If the client is not registered.
     */
    public String getAccessToken(ClientCredentials credentials) throws IOException, InterruptedException {
        ClientTokenData data = clientDataMap.get(credentials.getIdentifier());
        if (data == null) {
            registerClient(credentials);
            data = clientDataMap.get(credentials.getIdentifier());
        }

        synchronized (data) {
            if (shouldRefreshToken(data)) {
                refreshAccessToken(credentials.getIdentifier(), data);
            }
            return data.accessToken;
        }
    }


    /**
     * Check to see if the token is expired or about to expire
     * @param data
     * @return
     */
    private boolean shouldRefreshToken(ClientTokenData data) {
        if (data.accessToken == null || data.expirationTime == null) {
            return true;
        }
        // Check if current time + window is after the expiration time
        return Instant.now().plusSeconds(expiryWindowSeconds).isAfter(data.expirationTime);
    }


    /**
     * Perform a refresh of the access token.
     *
     * @param clientId THe identifier of the client to refresh
     * @param data the data containing the refresh token and client secret
     * @throws IOException if network operations fail
     * @throws InterruptedException if a timeout occurs.
     */
    private void refreshAccessToken(String clientId, ClientTokenData data) throws IOException, InterruptedException {
        if (data.refreshToken == null) {
            throw new IllegalStateException("No refresh token available for client: " + clientId);
        }
        String url = tokenProviderUrl+"/token";

        // Prepare the parameters
        Map<String, String> parameters = Map.of(
                "refresh_token", data.refreshToken,
                "grant_type", "refresh_token",
                "client_id", clientId,
                "client_secret", data.clientSecret
        );

        // Convert the map to an encoded form string
        String formBody = parameters.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        // Build the Request
        try  {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(formBody))
                    .build();

            // Send the request and receive the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Log.debug("Token refreshed successfully:");
                Log.debug(response.body());

                data.accessToken = extractJsonValue(response.body(), "access_token");
                String newRefreshToken = extractJsonValue(response.body(), "refresh_token");

                if (newRefreshToken != null && !newRefreshToken.isEmpty()) {
                    data.refreshToken = newRefreshToken;
                }

                String expiresInStr = extractJsonValue(response.body(), "expires_in");
                long expiresIn = (expiresInStr != null) ? Long.parseLong(expiresInStr) : 3600;

                data.expirationTime = Instant.now().plusSeconds(expiresIn);

            } else {
                Log.error("Error: Received HTTP " + response.statusCode());
                Log.error("Body: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            Log.error("Request failed: " + e.getMessage());
        }

    }


    /**
     * Simple helper to extract values from JSON strings
     */
    private String extractJsonValue(String json, String key) {
        if (json == null) return null;

        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }

        pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(\\d+)");
        matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    /**
     * Inner class to hold state
     */
    private static class ClientTokenData {
        String clientSecret;
        String refreshToken;
        String accessToken;
        Instant expirationTime;
    }
}