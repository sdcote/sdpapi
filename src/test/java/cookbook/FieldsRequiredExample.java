package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.ListInfo;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.asset.Asset;
import com.sdcote.sdp.asset.AssetModule;
import coyote.commons.vault.VaultEntry;

import java.util.List;

public class FieldsRequiredExample {

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        // Get our client ID, secret, and refresh token and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("Asset Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));

        ListInfo listInfo = new ListInfo(5, 1);

        // Request ONLY the ID and Name
        listInfo.setFieldsRequired("id", "name");

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, listInfo);

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
        When you use fields_required, any field you don't request will be null in your Java objects.

        Ensure your code checks for nulls (e.g., asset.getSite() might be null now).

        The id field is usually returned even if you don't explicitly ask for it, but it is best practice to always include "id" in
        your list.
        - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */


        for (Asset asset : assets) {
            System.out.println(asset);
        }

    }
}
