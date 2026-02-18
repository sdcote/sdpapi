package cookbook;

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

        // Use the AssetModule class to access the data
        List<Asset> assets = AssetModule.listAssets(clientCreds);

        assets.forEach(System.out::println);
        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }
}
