package cookbook;

import com.sdcote.sdp.OldListInfo;

import java.util.HashMap;
import java.util.Map;

public class ListInfoUsage {
    public static void main(String[] args) {
        // 1. Create the OldListInfo object
        OldListInfo oldListInfo = new OldListInfo(100, 1);
        oldListInfo.setSortField("name");
        oldListInfo.setSortOrder(OldListInfo.ASCENDING);

        // Optional: Add filtering
        Map<String, Object> search = new HashMap<>();
        search.put("name", "MacBook");
        oldListInfo.setSearchFields(search);

        // 2. Generate the encoded query parameter
        String encodedParam = oldListInfo.toQueryParam();

        // 3. Build the URL
        String url = "https://sdpondemand.manageengine.com/api/v3/assets?list_info=" + encodedParam;

        // ... proceed with HttpClient request ...
        System.out.println("Request URL: " + url);
    }
}
