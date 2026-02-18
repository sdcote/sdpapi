package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.ListInfo;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.SearchCriteria;
import com.sdcote.sdp.asset.Asset;
import com.sdcote.sdp.asset.AssetModule;
import coyote.commons.log.Log;
import coyote.commons.vault.VaultEntry;

import java.util.Arrays;
import java.util.List;

/**
 * As of 2026/02/18 - SDP Cloud is not honoring the `is_in` condition. It is correctly formatted according to the API documentation.
 *
 * <p>The only workaround for this is an OR operation of all the values.</p>
 */
public class SearchIsInExample {

    static {
        Log.initDevelopmentLogging();
    }

    public static void main(String[] args) {

        // Get our client ID, secret, and refresh token and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("Asset Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));

        // Create the ListInfo object
        ListInfo listInfo = new ListInfo(5, 1);

        // Create the list of names
        List<String> assetNames = Arrays.asList("Mylaptop123", "Mylaptop456", "Server-09");

        // Create Criteria using 'is_in'
        // The constructor detects the List and maps it to the "values" field
        SearchCriteria criteria = new SearchCriteria("name", "is_in", assetNames);

        // Attach criteria to ListInfo
        listInfo.setSearchCriteria(criteria);

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, listInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }


    /**
     * This is an example to combine the above query with a Status filter
     * (e.g., "Find these 3 laptops, ONLY if they are 'In Store'")
     */
    public void searchAssetsWithStatus() {
        ListInfo listInfo = new ListInfo(100, 1);

        // 1. Define the list of asset names
        List<String> assetNames = Arrays.asList("Mylaptop123", "Mylaptop456", "Server-09");

        // 2. Create the Root Criteria (The Names)
        // Condition: Name is in the list
        SearchCriteria rootCriteria = new SearchCriteria("name", "is_in", assetNames);

        // 3. Create the Child Criteria (The Status)
        // Condition: Status is "In Store"
        SearchCriteria statusCriteria = new SearchCriteria("state.name", "is", "In Store");

        // 4. Set the Logical Operator
        // This tells the API: "AND this condition with the previous one"
        statusCriteria.setLogicalOperator("and");

        // 5. Link them
        rootCriteria.addChild(statusCriteria);

        // 6. Attach to ListInfo
        listInfo.setSearchCriteria(rootCriteria);

        // Generate and print URL
        System.out.println(listInfo.toQueryParam());
    }
}
