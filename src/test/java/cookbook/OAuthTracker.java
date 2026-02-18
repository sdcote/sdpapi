package cookbook;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.OAuthAccessTokenTracker;
import com.sdcote.sdp.SDP;
import coyote.commons.log.Log;
import coyote.commons.vault.VaultEntry;

public class OAuthTracker {

    static {
        Log.initDevelopmentLogging();
    }

    public static void main(String[] args) {

        // Get our client ID and secret and place them in a set of client credentials
        VaultEntry vaultEntry = SDP.getVault().getEntry("CMDB Automation");
        ClientCredentials clientCreds = new ClientCredentials(vaultEntry.get("Username"), vaultEntry.get("Password"), vaultEntry.get("Token"));


        try {
            OAuthAccessTokenTracker tracker = new OAuthAccessTokenTracker("https://accounts.zoho.com/oauth/v2", 300);
            String token = tracker.getAccessToken(clientCreds);
            System.out.println("Access Token: " + token);
        } catch (Exception e) {
            Log.error(e);
        }
    }


}
