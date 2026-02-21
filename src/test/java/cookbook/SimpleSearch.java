package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.OldListInfo;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.OldSearchCriteria;
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

        // Create the OldListInfo object
        OldListInfo oldListInfo = new OldListInfo(10, 1); // We only expect 1 result, but 10 is safe

        // Define the Search SearchCriteria
        // Field: "name"
        // Condition: "is" (exact match) or "contains" (partial match)
        // Value: "Mylaptop123"
        OldSearchCriteria criteria = new OldSearchCriteria("name", "is", "Mylaptop123");

        // Attach criteria to OldListInfo
        oldListInfo.setSearchCriteria(criteria);

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, oldListInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }



}
