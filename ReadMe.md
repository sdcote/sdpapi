# ServiceDesk API

A Java API for interacting with ServiceDesk Plus Cloud.

# Features

### Request Throttling

This API includes a mechanism to help both Zoho and you prevent mistaking your automation for a DoS and to play nicely with everyone else using SDP Cloud. The API keeps all requests throttled to 30 per minute, mimicking a normal, albeit frantic, web user. Zoho should not mistake you for a DoS attack, and your use of this API should not interfere with Zoho's capacity-planning model.

### Secrets Vault

This uses the Coyote Commons MiniVault (via JVault) to protect your OAuth client identifier, client secret, and refresh token with AES-256 encryption. No need to hard-code your confidential information, place it in unprotected configuration files, or pass it as arguments with every execution.


# To Do List

These are items that should be addressed.

## Searching (In Progress)
Update `OldListInfo` to support search searchCriteria. See [Search Criteria](https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/search-searchCriteria.html)

## Fields Required (In Progress)
Update `OldListInfo` to support `fields_required`. See [Input Data](https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/input-data.html)

## Response Parsing (In Progress)
There is additional data in the response that is not accessible to the current retrieval design.
```json
{
  "assets": [
 ],
  "response_status": [
    {
      "status_code": 2000,
      "status": "success"
    }
  ],
  "list_info": {
    "has_more_rows": true,
    "sort_field": "name",
    "row_count": 10
  }
}
```
It may be advantageous to redesign the web service calls to return this information.

**Update:** The `ApiResponse` class makes this data available if the `AssetModule.callApi()` method is called.


## Total Count (Low Priority)
Update `OldListInfo` to support `get_total_count`. See [Input Data](https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/input-data.html)

## Entity Attributes (Low Priority)
Input data might need to be its own class, holding `entity` and `list_info`. It is not yet clear how `entity` might be used. See [Input Data](https://www.manageengine.com/products/service-desk/sdpod-v3-api/getting-started/input-data.html)

