package cookbook;

import com.sdcote.sdp.ListInfo;
import com.sdcote.sdp.asset.Asset;
import com.sdcote.sdp.asset.AssetModule;
import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.SDP;
import coyote.commons.log.Log;
import coyote.commons.vault.VaultEntry;

import java.util.List;

public class AssetList {

    static {
        Log.initDevelopmentLogging();
    }

    public static void main(String[] args) {

        // Get our client ID, secret, and refresh token and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("Asset Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));

        ListInfo listInfo = new ListInfo();
        listInfo.setRowCount(5);
        listInfo.setSortField("name");
        listInfo.setSortOrder(ListInfo.ASCENDING);

        // Optional: Add filtering
        // Map<String, Object> search = new HashMap<>();
        // search.put("name", "MacBook");
        // listInfo.setSearchFields(search);
        listInfo.putSearchField("name"," P2-VRN-DB.ion.llc"); // convenience method

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, listInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }
}
