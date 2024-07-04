package com.mojo.woof;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;

public class AppTest
{
    @Test
    @Disabled
    public void canTraverseGraph() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Advisor advisor = new Advisor(OpenAICredentials.fromEnv());
        ActionResult result = sdk.traverse(sdk.rootNode(), new SummariseAction(advisor, sdk));
        assertTrue( true );
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
        Record relationship = sdk.connect(parent, child, "IS_COOLER_THAN");
    }
}
