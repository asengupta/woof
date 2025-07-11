package com.mojo.woof;


import com.google.common.collect.ImmutableList;
import com.mojo.woof.llm.Advisor;
import com.mojo.woof.llm.AzureOpenAIAdvisor;
import com.mojo.woof.llm.OpenAICredentials;
import org.junit.jupiter.api.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;

public class SDKTest
{
    @Test
    @Disabled
    public void canTraverseGraph() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Advisor advisor = new AzureOpenAIAdvisor(OpenAICredentials.fromEnv());
        Record root = sdk.findNodes(ImmutableList.of("AST_NODE"), Map.of("type", "PROCEDURE_DIVISION_BODY")).getFirst();
        ActionResult result = sdk.traverse(root, NodeAction.JUST_PRINT, "CONTAINS_CODE");
    }

    @Test
    @Disabled
    public void canCreateIndividualUnconnectedNodes() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Map<String, Object> properties = Map.of("property1", "value1", "property2", "value2", "property3", "value3");
        List<String> labels = ImmutableList.of("LABEL1", "LABEL2", "LABEL3", "LABEL4", "LABEL5", "LABEL6", "LABEL7");
        sdk.createNode(new WoofNode(properties, labels));
        sdk.createNode(new WoofNode(Map.of("x1", "y1"), ImmutableList.of("AAAA", "BBBB", "CCCC")));
    }

    @Test
    @Disabled
    public void canFindExistingNodes() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Map<String, Object> properties = Map.of("property1", "value1", "property2", "value2", "property3", "value3");
        List<String> labels = ImmutableList.of("LABEL1", "LABEL2", "LABEL3", "LABEL4", "LABEL5", "LABEL6", "LABEL7");
        Record node1 = sdk.createNode(new WoofNode(properties, labels));
        Record foundNode1 = sdk.node(NodeAccess.id(node1));
    }

    @Test
    @Disabled
    public void canConnectTwoNodes() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Map<String, Object> properties = Map.of("property1", "value1", "property2", "value2", "property3", "value3");
        List<String> labels = ImmutableList.of("LABEL1", "LABEL2", "LABEL3", "LABEL4", "LABEL5", "LABEL6", "LABEL7");
        Record parent = sdk.createNode(new WoofNode(properties, labels));
        Record child = sdk.createNode(new WoofNode(Map.of("x1", "y1"), ImmutableList.of("AAAA", "BBBB", "CCCC")));
        Record relationship = sdk.connect(parent, child, "IS_COOLER_THAN", "SOME_EDGE_TYPE");
    }

    @Test
    @Disabled
    public void canFindNodeBasedOnArbitraryProperty() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Map<String, Object> properties = Map.of("testProp", "testVal", "property2", "value2", "property3", "value3");
        List<String> labels = ImmutableList.of("LABEL1", "LABEL2", "LABEL3", "LABEL4", "LABEL5", "LABEL6", "LABEL7");
        Record node = sdk.createNode(new WoofNode(properties, labels));
        List<Record> foundNodes = sdk.findNodes(ImmutableList.of("LABEL7"), Map.of("testProp", "testVal"));
    }
}
