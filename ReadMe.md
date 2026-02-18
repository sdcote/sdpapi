# ServiceDesk API

A Java API for interacting with ServiceDesk Plus Cloud.

# Features

### Request Throttling

This API includes a mechanism to help both Zoho and you prevent mistaking your automation for a DoS and to play nicely with everyone else using SDP Cloud. The API keeps all requests throttled to 30 per minute, mimicking a normal, albeit frantic, web user. Zoho should not mistake you for a DoS attack, and your use of this API should not interfere with Zoho's capacity-planning model.

### Secrets Vault

This uses the Coyote Commons MiniVault (via JVault) to protect your OAuth client identifier, client secret, and refresh token with AES-256 encryption. No need to hard-code your confidential information, place it in unprotected configuration files, or pass it as arguments with every execution.