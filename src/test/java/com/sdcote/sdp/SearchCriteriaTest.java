package com.sdcote.sdp;

import coyote.commons.dataframe.DataFrame;
import coyote.commons.dataframe.marshal.JSONMarshaler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchCriteriaTest {

    @Test
    void basicTest() {
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

        System.out.println(root.toString());

    }


    @Test
    void parse1() {
        //  (status = 'Open' AND (created_time > 1488451440000))
        String json = "{\n" +
                "      \"field\": \"status\",\n" +
                "      \"condition\": \"is\",\n" +
                "      \"value\": {\n" +
                "        \"id\": \"100000000000008033\",\n" +
                "        \"name\": \"Open\"\n" +
                "      },\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"field\": \"created_time\",\n" +
                "          \"condition\": \"greater than\",\n" +
                "          \"value\": \"1488451440000\",\n" +
                "          \"logical_operator\": \"AND\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";

        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
        SearchCriteria root = new SearchCriteria(frames.get(0));
        assertEquals("status",root.getField());
        assertEquals("is",root.getCondition());
        System.out.println(root.getValue());


    }

    @Test
    void parse2() {
        // A&B – A(and)B
        String json = "[\n" +
                "      {\n" +
                "        \"field\": \"group.name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"values\": [\n" +
                "          \"Network\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"status.in_progress\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"logical_operator\": \"and\",\n" +
                "        \"values\": [\n" +
                "          true\n" +
                "        ]\n" +
                "      }\n" +
                "    ]";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
        SearchCriteria root = new SearchCriteria(frames.get(0));
    }


    @Test
    void parse3() {
        // A&B – A(and)B
        String json = "[\n" +
                "      {\n" +
                "        \"field\": \"group.name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"values\": [\n" +
                "          \"Network\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"status.internal_name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"logical_operator\": \"and\",\n" +
                "        \"values\": [\n" +
                "          \"Open\"\n" +
                "        ]\n" +
                "      }\n" +
                "    ]";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }


    @Test
    void parse4() {
        // A&B&(C&D) - A(and)B(and)[C(and)D]
        String json = "[\n" +
                "      {\n" +
                "        \"field\": \"group.name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"values\": [\n" +
                "          \"Network\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"status.internal_name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"logical_operator\": \"and\",\n" +
                "        \"values\": [\n" +
                "          \"Open\"\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"created_time\",\n" +
                "        \"condition\": \"greater than\",\n" +
                "        \"logical_operator\": \"and\",\n" +
                "        \"values\": [\n" +
                "          \"1553731200000\"\n" +
                "        ],\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"field\": \"created_time\",\n" +
                "            \"condition\": \"lesser than\",\n" +
                "            \"logical_operator\": \"and\",\n" +
                "            \"values\": [\n" +
                "              \"1553817599000\"\n" +
                "            ]\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }


    @Test
    void parse5() {
        // 3)(AB)&(CD) – [A(or)B]and[C(or)D]
        String json = " [\n" +
                "      {\n" +
                "        \"field\": \"status.in_progress\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"values\": [\n" +
                "          true\n" +
                "        ],\n" +
                "        \"logical_operator\": \"AND\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"field\": \"status.internal_name\",\n" +
                "            \"condition\": \"is\",\n" +
                "            \"values\": [\n" +
                "              \"Resolved\"\n" +
                "            ],\n" +
                "            \"logical_operator\": \"OR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"priority.name\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"logical_operator\": \"AND\",\n" +
                "        \"values\": [\n" +
                "          \"High\"\n" +
                "        ],\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"field\": \"urgency.name\",\n" +
                "            \"condition\": \"is\",\n" +
                "            \"values\": [\n" +
                "              \"Urgent\"\n" +
                "            ],\n" +
                "            \"logical_operator\": \"OR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }

    @Test
    void parse6() {
        // The following will retrieve all the open requests created after a period of time
        String json = " {\n" +
                "      \"field\": \"status\",\n" +
                "      \"condition\": \"is\",\n" +
                "      \"value\": {\n" +
                "        \"id\": \"100000000000008033\",\n" +
                "        \"name\": \"Open\"\n" +
                "      },\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"field\": \"created_time\",\n" +
                "          \"condition\": \"greater than\",\n" +
                "          \"value\": \"1488451440000\",\n" +
                "          \"logical_operator\": \"AND\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }


    @Test
    void parse7() {
        // The following will retrieve all the open requests matching the below conditions, created_time greater than a period of time subject must starts with a specific value
        String json = "{\n" +
                "      \"field\": \"status\",\n" +
                "      \"condition\": \"is\",\n" +
                "      \"values\": [\n" +
                "        {\n" +
                "          \"id\": \"100000000000008033\",\n" +
                "          \"name\": \"Open\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"field\": \"created_time\",\n" +
                "          \"condition\": \"greater than\",\n" +
                "          \"values\": [\n" +
                "            {\n" +
                "              \"value\": \"1488451440000\",\n" +
                "              \"display_value\": \"02 Mar 2017, 16:14:00\"\n" +
                "            }\n" +
                "          ],\n" +
                "          \"logical_operator\": \"AND\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"field\": \"subject\",\n" +
                "          \"condition\": \"starts with\",\n" +
                "          \"values\": [\n" +
                "            \"quick\"\n" +
                "          ],\n" +
                "          \"logical_operator\": \"OR\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }

    @Test
    void parse8() {
        // The following will retrieve the requests matching the conditions
        // ((status is open) and (created time greater than OR subject starts with “quick”)) OR (group is not network and technician is not empty)
        String json = " [\n" +
                "      {\n" +
                "        \"field\": \"status\",\n" +
                "        \"condition\": \"is\",\n" +
                "        \"values\": [\n" +
                "          {\n" +
                "            \"id\": \"100000000000008033\",\n" +
                "            \"name\": \"Open\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"logical_operator\": \"AND\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"field\": \"created_time\",\n" +
                "            \"condition\": \"greater than\",\n" +
                "            \"values\": [\n" +
                "              {\n" +
                "                \"value\": \"1488451440000\",\n" +
                "                \"display_value\": \"02 Mar 2017, 16:14:00\"\n" +
                "              }\n" +
                "            ],\n" +
                "            \"logical_operator\": \"AND\"\n" +
                "          },\n" +
                "          {\n" +
                "            \"field\": \"subject\",\n" +
                "            \"condition\": \"starts with\",\n" +
                "            \"values\": [\n" +
                "              \"quick\"\n" +
                "            ],\n" +
                "            \"logical_operator\": \"OR\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"field\": \"group\",\n" +
                "        \"condition\": \"is not\",\n" +
                "        \"values\": [\n" +
                "          {\n" +
                "            \"id\": \"100000000000008057\",\n" +
                "            \"name\": \"Network\"\n" +
                "          }\n" +
                "        ],\n" +
                "        \"logical_operator\": \"OR\",\n" +
                "        \"children\": [\n" +
                "          {\n" +
                "            \"field\": \"technician\",\n" +
                "            \"condition\": \"is not\",\n" +
                "            \"logical_operator\": \"AND\"\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ]";
        List<DataFrame> frames = JSONMarshaler.marshal(json);
        System.out.println(frames);
    }

}