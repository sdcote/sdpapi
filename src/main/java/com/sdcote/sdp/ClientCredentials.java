package com.sdcote.sdp;

public class ClientCredentials {
    private final String secret;
    private final String identifier;
    private final String refreshToken;

    /**
     * @param id           Client Identifier
     * @param secret       Client Secret
     * @param refreshToken Token to refresh the access token
     */
    public ClientCredentials(String id, String secret, String refreshToken) {
        this.identifier = id;
        this.secret = secret;
        this.refreshToken = refreshToken;
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getSecret() {
        return secret;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
