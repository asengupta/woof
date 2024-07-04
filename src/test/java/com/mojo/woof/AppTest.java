package com.mojo.woof;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.*;
import org.neo4j.driver.Record;

public class AppTest
{
    @Test
    public void shouldAnswerWithTrue() {
        GraphSDK sdk = new GraphSDK(new Neo4JDriverBuilder().fromEnv());
        Advisor advisor = new Advisor(OpenAICredentials.fromEnv());
        ActionResult result = sdk.traverse(sdk.rootNode(), new SummariseAction(advisor, sdk));
        assertTrue( true );
    }
}
