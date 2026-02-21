package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.OldListInfo;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.OldSearchCriteria;
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

        // Create the OldListInfo object
        OldListInfo oldListInfo = new OldListInfo(5, 1);

        // Create the list of names
        List<String> assetNames = Arrays.asList("Mylaptop123", "Mylaptop456", "Server-09");

        // Create SearchCriteria using 'is_in'
        // The constructor detects the List and maps it to the "values" field
        OldSearchCriteria criteria = new OldSearchCriteria("name", "is_in", assetNames);

        // Attach criteria to OldListInfo
        oldListInfo.setSearchCriteria(criteria);

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, oldListInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }


    /**
     * This is an example to combine the above query with a Status filter
     * (e.g., "Find these 3 laptops, ONLY if they are 'In Store'")
     */
    public void searchAssetsWithStatus() {
        OldListInfo oldListInfo = new OldListInfo(100, 1);

        // 1. Define the list of asset names
        List<String> assetNames = Arrays.asList("Mylaptop123", "Mylaptop456", "Server-09");

        // 2. Create the Root SearchCriteria (The Names)
        // Condition: Name is in the list
        OldSearchCriteria rootCriteria = new OldSearchCriteria("name", "is_in", assetNames);

        // 3. Create the Child SearchCriteria (The Status)
        // Condition: Status is "In Store"
        OldSearchCriteria statusCriteria = new OldSearchCriteria("state.name", "is", "In Store");

        // 4. Set the Logical Operator
        // This tells the API: "AND this condition with the previous one"
        statusCriteria.setLogicalOperator("and");

        // 5. Link them
        rootCriteria.addChild(statusCriteria);

        // 6. Attach to OldListInfo
        oldListInfo.setSearchCriteria(rootCriteria);

        // Generate and print URL
        System.out.println(oldListInfo.toQueryParam());
    }
}
