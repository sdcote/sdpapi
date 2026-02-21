package cookbook;

import com.sdcote.sdp.OldListInfo;
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

        OldListInfo oldListInfo = new OldListInfo();
        oldListInfo.setRowCount(5);
        oldListInfo.setSortField("name");
        oldListInfo.setSortOrder(OldListInfo.ASCENDING);

        // Optional: Add filtering
        // Map<String, Object> search = new HashMap<>();
        // search.put("name", "MacBook");
        // oldListInfo.setSearchFields(search);
        oldListInfo.putSearchField("name"," P2-VRN-DB.ion.llc"); // convenience method

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, oldListInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }
}
