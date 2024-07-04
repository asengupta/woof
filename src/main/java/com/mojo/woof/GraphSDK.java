package com.mojo.woof;

import com.google.common.collect.ImmutableList;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.UUID;

import static com.mojo.woof.NodeAccess.id;
import static org.neo4j.driver.Values.parameters;

public class GraphSDK {
    private final Driver driver;

    public GraphSDK(Neo4JDriverBuilder builder) {
        driver = builder.driver();
    }

    public Record rootNode() {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Query query = new Query("MATCH (n:CODE_CHUNK) " +
                        "            WHERE NOT (:CODE_CHUNK)-[:MADE_OF]->(n) " +
                        "            RETURN n");
                Result result = tx.run(query);
                return result.single();
            });
        }
    }

    public ActionResult traverse(Record node, NodeAction nodeAction) {
        List<Record> children = directChildren(node);
        if (children.isEmpty()) return nodeAction.apply(node, ImmutableList.of());
        List<ActionResult> childResults = children.stream().map(n -> traverse(n, nodeAction)).toList();
        return nodeAction.apply(node, childResults);
    }

    private List<Record> directChildren(Record node) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Query query = new Query("MATCH (start {id: $rootID})-[:MADE_OF]->(n)" +
                        "            RETURN DISTINCT n", parameters("rootID", id(node)));
                Result result = tx.run(query);
                return result.list();
            });
        }
    }

    public void createSummary(String textContent, Record node) {
        try (Session session = driver.session()) {
            session.executeWrite(tx -> {
                Query query = new Query("MATCH (p:CODE_CHUNK {id: $parentId}) " +
                        "CREATE (c:SUMMARY_NODE {id: $childId, type: 'SUMMARY', text: $text}) " +
                        "CREATE (p)-[:SUMMARISED_BY]->(c)",
                        Values.parameters("text", textContent, "parentId", id(node), "childId", UUID.randomUUID().toString()));
                return tx.run(query).list();
            });
        }
    }
}
