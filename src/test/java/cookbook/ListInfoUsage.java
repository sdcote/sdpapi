package cookbook;

import com.sdcote.sdp.ListInfo;

import java.util.HashMap;
import java.util.Map;

public class ListInfoUsage {
    public static void main(String[] args) {
        // 1. Create the ListInfo object
        ListInfo listInfo = new ListInfo(100, 1);
        listInfo.setSortField("name");
        listInfo.setSortOrder(ListInfo.ASCENDING);

        // Optional: Add filtering
        Map<String, Object> search = new HashMap<>();
        search.put("name", "MacBook");
        listInfo.setSearchFields(search);

        // 2. Generate the encoded query parameter
        String encodedParam = listInfo.toQueryParam();

        // 3. Build the URL
        String url = "https://sdpondemand.manageengine.com/api/v3/assets?list_info=" + encodedParam;

        // ... proceed with HttpClient request ...
        System.out.println("Request URL: " + url);
    }
}
