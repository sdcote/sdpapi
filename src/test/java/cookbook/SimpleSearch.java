package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.ListInfo;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.SearchCriteria;
import com.sdcote.sdp.asset.Asset;
import com.sdcote.sdp.asset.AssetModule;
import coyote.commons.log.Log;
import coyote.commons.vault.VaultEntry;

import java.util.List;

public class SimpleSearch {

    static {
        Log.initDevelopmentLogging();
    }

    public static void main(String[] args) {
        // Get our client ID, secret, and refresh token and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("Asset Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));

        // Create the ListInfo object
        ListInfo listInfo = new ListInfo(10, 1); // We only expect 1 result, but 10 is safe

        // Define the Search Criteria
        // Field: "name"
        // Condition: "is" (exact match) or "contains" (partial match)
        // Value: "Mylaptop123"
        SearchCriteria criteria = new SearchCriteria("name", "is", "Mylaptop123");

        // Attach criteria to ListInfo
        listInfo.setSearchCriteria(criteria);

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, listInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }



}
