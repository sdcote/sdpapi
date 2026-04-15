package com.sdcote;

import com.sdcote.sdp.ClientCredentials;
import com.sdcote.sdp.SDP;
import com.sdcote.sdp.Workstation;
import coyote.commons.StringUtil;
import coyote.commons.log.Log;
import coyote.commons.vault.Vault;
import coyote.commons.vault.VaultEntry;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads workstation names from a file and retrieves them from ServiceDesk Plus.
 */
public class UpdateWorkstation {

    private static final String DEFAULT_FILE = "workstations.txt";

    static {
        Log.initDevelopmentLogging();
        Log.stopLogging(Log.DEBUG);
        Log.stopLogging(Log.TRACE);
    }

    public static void main(String[] args) {
        String filename = (args.length > 0) ? args[0] : DEFAULT_FILE;
        List<String> names = readWorkstationNames(filename);

        if (names.isEmpty()) {
            Log.info("No workstation names found in " + filename);
            return;
        }

        Vault vault = SDP.getVault();
        if (vault == null) {
            Log.error("Could not retrieve secrets vault. Check vault.password system property.");
            return;
        }

        VaultEntry entry = vault.getEntry("AssetAutomation");
        if (entry == null) {
            Log.error("Could not find 'AssetAutomation' entry in vault.");
            return;
        }

        String clientId = entry.get("Username");
        String clientSecret = entry.get("Password");
        String refreshToken = entry.get("Token");

        if (StringUtil.isBlank(clientId) || StringUtil.isBlank(clientSecret) || StringUtil.isBlank(refreshToken)) {
            Log.error("Missing credentials in 'AssetAutomation' vault entry (Username, Password, or Token).");
            return;
        }

        ClientCredentials credentials = new ClientCredentials(clientId, clientSecret, refreshToken);

        for (String name : names) {
            Log.debug("Retrieving workstation: " + name);
            Workstation workstation = SDP.getWorkstationByName(credentials, name);
            if (workstation != null) {
                Log.info("Found workstation: " + workstation.getName() + " (ID: " + workstation.getId() + ") - " + workstation.getStateName());

//                Workstation updated = SDP.updateWorkstationState(workstation, "Expired", credentials);
//                if (updated != null) {
//                    Log.info("Updated workstation: " + updated.getName() + " to state: " + updated.getStateName());
//                } else {
//                    Log.error("Failed to update workstation: " + workstation.getName());
//                }
            } else {
                Log.warn("Workstation not found: " + name);
            }
        }
    }

    /**
     * Reads workstation names from the specified file.
     *
     * @param filename the name of the file to read.
     * @return a list of workstation names.
     */
    private static List<String> readWorkstationNames(String filename) {
        List<String> names = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtil.isNotBlank(line)) {
                    names.add(line.trim());
                }
            }
        } catch (IOException e) {
            Log.error("Error reading file: " + filename, e);
        }
        return names;
    }
}
