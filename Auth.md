# OAuth 2.0

To authenticate with ServiceDesk Plus Cloud via the V3 API, you must use the Zoho OAuth 2.0 protocol. This involves registering an application, granting access, and exchanging an authorization code for a refresh token.



## Overview

This API requires you to create a client in the Zoho Developer Console manually and set up a permission scope for that client. This results in a static permission model in which the client only needs the refresh token for the duration of the client's lifetime. The high-level steps are as follows:

1. You create a client attached to your account. This client has an Identifier and a secret.
2. Establish a scope of permission for the client. This is performed by sending a request to establish an authorization scope for the client. The response is a code that can be used to retrieve an access token, allowing the client to interact with the API with those permissions.
3. Use the `code`, which, when presented to the token service, returns an `access token` that can be used to authenticate as that client with that scope of permissions. That response includes a `refresh token` that can be used to obtain new access tokens when they expire.
4. The final step is to periodically use the refresh token to obtain new access tokens as they expire. Since the refresh token never expires, it can be stored to retrieve a new access token whenever API access is needed.

If a different scope of permissions is required for the client, a separate manual request for a new code to generate a new refresh token is required. This helps ensure that a human approves the change in permission scope.

## 1. Register the Application

You must first register your client with the Zoho Developer Console to obtain a **Client ID** and **Client Secret**.

- Navigate to the [Zoho Developer Console](https://api-console.zoho.com/).
- Select **Server-based Applications**.
- Enter a **Client Name** and your **Homepage URL**. These are important, but required.
- Provide an **Authorized Redirect URI**. This is the endpoint where the authorization code will be sent. For manual testing and general automation, `https://localhost` or a similar placeholder is sufficient.

The Authorized Redirect URIs are important for authenticating some requests, such as granting permissions (the next step). Record the URI, or use a common URI for all clients.

## 2. Grant Permissions (Authorization Request)

To establish identity and grant permissions, you must redirect the user (or yourself) to the Zoho OAuth authorization URL to generate a temporary `code`.

Perform an HTTP GET request with parameters encoded in the URL using a standard browser.

An easy way to do this is to use a text editor to construct a URL.

**URL Format:**

```
https://accounts.zoho.com/oauth/v2/auth?scope=SDPOnDemand.requests.ALL&client_id=YOUR_CLIENT_ID&response_type=code&access_type=offline&redirect_uri=YOUR_REDIRECT_URI
```

- **scope**: Defines the level of access. Common scopes include `SDPOnDemand.requests.ALL`, `SDPOnDemand.setup.ALL`, or `SDPOnDemand.admin.ALL`. See the "Scopes" section for more details.
- **access_type**: Must be set to `offline` to receive a refresh token.
- Use `prompt=consent` to force the consent screen and ensure a refresh token is issued.

Copy the constructed URL from the editor and paste it into the address bar of your browser.

> Do not press Enter until you are ready to complete step 3 below. You will have only a few seconds to complete step 3, as the code expires after 60 seconds.

Once you click **Accept** on the consent screen, the browser will redirect to your Redirect URI with a `code` parameter in the URL. It will look something like the following

```url
https://localhost/?code=1000.b511SoMeCoDe8c8f4d50d185f.bb598SoMeCoDe35845947a15c&location=us&accounts-server=https%3A%2F%2Faccounts.zoho.com
```

Extract that `code` parameter from the redirect URL and use it for Step 3.

## 3. Obtain the Refresh Token

The `code` is short-lived (typically 1 minute). You must **<u>immediately</u>** exchange it for an access token and a refresh token using a POST request.

An efficient way to perform this task is to use Postman and enter all the data described below in the body of a POST request. Once step 2 above is completed, the `code` value can be extracted from the redirect URI, placed in the body, and the request sent within a few seconds.

**Endpoint:**

```
https://accounts.zoho.com/oauth/v2/token
```

**POST Body (Form Data):**

- **code**: The authorization code obtained in Step 2.
- **client_id**: Your Client ID.
- **client_secret**: Your Client Secret.
- **redirect_uri**: The same Redirect URI used in Step 1.
- **grant_type**: `authorization_code`

**Response:**

The JSON response will contain the `refresh_token` and an `access_token`, which are used to authenticate subsequent requests. An example response is below:
```json
{
    "access_token": "1000.8b200b1e185df5a4b43ac253c7.2cce3c983e9fdb7a4d0bdf5983",
    "refresh_token": "1000.7f2184566204d3383e4123ca23.2d58677dbc203ecba773a3eff4",
    "scope": "SDPOnDemand.assets.ALL",
    "api_domain": "https://www.zohoapis.com",
    "token_type": "Bearer",
    "expires_in": 3600
}
```



Unlike the `access_token` (which expires in one hour), the `refresh_token` is permanent unless revoked. Clients can use this refresh token to obtain new access tokens at any time.

## 4. Using the Refresh Token

Since the `access_token` expires, you must generate a new `access_token` using the `refresh_token` when the `access_token` expires.

**POST Body to `/oauth/v2/token`:**

- **refresh_token**: Your stored refresh token.
- **client_id**: Your Client ID.
- **client_secret**: Your Client Secret.
- **grant_type**: `refresh_token`

**API Call Header:**

Include the resulting access token in the Authorization header of your ServiceDesk Plus API requests:

```
Authorization: Zoho-oauthtoken <YOUR_ACCESS_TOKEN>
```



## Scopes

To authenticate for the **Assets**, **Changes**, or **CMDB** modules, you must specify the corresponding scopes during the initial authorization request.

The scope format for ServiceDesk Plus Cloud is always `SDPOnDemand.[module].[operation]`. You can use `.ALL` for full CRUD (Create, Read, Update, Delete) access or restrict it to specific operations like `.READ`.

### Core Scopes for Assets and Changes

| **Module**  | **Scope**                 | **Access Provided**                                 |
| ----------- | ------------------------- | --------------------------------------------------- |
| **Assets**  | `SDPOnDemand.assets.ALL`  | Full access to inventory, workstations, and assets. |
| **Changes** | `SDPOnDemand.changes.ALL` | Full access to change requests and workflows.       |
| **CMDB**    | `SDPOnDemand.cmdb.ALL`    | Access to CI types, CI relationships, and the CMDB. |
| **General** | `SDPOnDemand.general.ALL` | Access to shared metadata, sites, and departments.  |

------

### Requesting Multiple Scopes

When you follow the process to establish identity, you can request multiple permissions simultaneously by separating the scopes with a comma (no spaces).

**Example Authorization URL with multiple scopes:**

```
https://accounts.zoho.com/oauth/v2/auth?scope=SDPOnDemand.assets.ALL,SDPOnDemand.changes.ALL,SDPOnDemand.cmdb.ALL&client_id=YOUR_CLIENT_ID&response_type=code&access_type=offline&redirect_uri=YOUR_REDIRECT_URI
```

### Key Permissions Details

- **Granular Control:** If you do not want to grant full delete permissions, you can use `SDPOnDemand.assets.READ,SDPOnDemand.assets.UPDATE` instead of `.ALL`.
- **Dependencies:** If you are working with Assets, it is often helpful to include `SDPOnDemand.cmdb.READ` as many assets are represented as Configuration Items (CIs) in the CMDB.
- **Setup Scopes:** If you need to access administrative settings or configurations via API, use `SDPOnDemand.setup.ALL`.



A full list of scopes is in the ServiceDesk Plus [REST API - User Guide](https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/oauth-2.0.html#scopes).



