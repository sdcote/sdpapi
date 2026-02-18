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

public class SearchCriteriaExample {

    static {
        Log.initDevelopmentLogging();
    }

    public static void main(String[] args) {
        // Get our client ID, secret, and refresh token and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("Asset Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));

        ListInfo listInfo = complexSearchCriteria();

        // This will produce valid nested JSON in the input_data parameter
        String urlParam = listInfo.toQueryParam();
        System.out.println(urlParam);

        // Use the AssetModule class to access the data (just one page of data)
        List<Asset> assets = AssetModule.retrieveAssets(clientCreds, listInfo);

        for (Asset asset : assets) {
            System.out.println(asset);
        }
    }


    /**
     * Here is how you build a complex query:
     * "(Name contains 'MacBook' AND Status is 'In Store') OR (Site is 'New York')"
     */
    public static ListInfo complexSearchCriteria() {
        ListInfo retval = new ListInfo(100, 1);

        // Start with the first concrete condition as the Root
        SearchCriteria root = new SearchCriteria("name", "contains", "MacBook");

        // Chain the next condition (AND)
        // This defines: Name contains MacBook AND Status is In Store
        SearchCriteria condition2 = new SearchCriteria("state.name", "is", "In Store");
        condition2.setLogicalOperator("and"); // How this relates to the Root
        root.addChild(condition2);

        // Chain the final condition (OR)
        // This defines: (Previous Logic) OR Site is New York
        SearchCriteria condition3 = new SearchCriteria("site.name", "is", "New York");
        condition3.setLogicalOperator("or"); // How this relates to the Root chain
        root.addChild(condition3);

        // Attach to ListInfo
        retval.setSearchCriteria(root);

        return retval;
    }
}
