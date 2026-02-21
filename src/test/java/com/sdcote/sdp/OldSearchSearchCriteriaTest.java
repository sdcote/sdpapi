package com.sdcote.sdp;

import org.junit.jupiter.api.Test;

class OldSearchSearchCriteriaTest {

    @Test
    void searchCriteriaTest()    {
        OldListInfo retval = new OldListInfo(100, 1);

        // Start with the first concrete condition as the Root
        OldSearchCriteria root = new OldSearchCriteria("name", "contains", "MacBook");

        // Chain the next condition (AND)
        // This defines: Name contains MacBook AND Status is In Store
        OldSearchCriteria condition2 = new OldSearchCriteria("state.name", "is", "In Store");
        condition2.setLogicalOperator("and"); // How this relates to the Root
        root.addChild(condition2);

        // Chain the final condition (OR)
        // This defines: (Previous Logic) OR Site is New York
        OldSearchCriteria condition3 = new OldSearchCriteria("site.name", "is", "New York");
        condition3.setLogicalOperator("or"); // How this relates to the Root chain
        root.addChild(condition3);

        // Attach to OldListInfo
        retval.setSearchCriteria(root);
    }

}