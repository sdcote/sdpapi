package com.sdcote.sdp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaTest {

    @Test
    void searchCriteriaTest()    {
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
    }

}