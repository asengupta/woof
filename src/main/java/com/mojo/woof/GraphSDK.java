package com.mojo.woof;

import com.google.common.collect.ImmutableList;
import org.neo4j.driver.*;
import org.neo4j.driver.Record;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mojo.woof.NodeAccess.id;
import static com.mojo.woof.NodeLabels.SUMMARY_NODE;
import static com.mojo.woof.NodeProperties.TEXT;
import static com.mojo.woof.NodeRelations.*;
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

    public ActionResult traverse(Record node, NodeAction nodeAction, String parentChildRelationship) {
        List<Record> children = directChildren(node, parentChildRelationship);
        if (children.isEmpty()) return nodeAction.apply(node, ImmutableList.of());
        List<ActionResult> childResults = children.stream().map(n -> traverse(n, nodeAction, parentChildRelationship)).toList();
        return nodeAction.apply(node, childResults);
    }

    private List<Record> directChildren(Record node, String parentChildRelationship) {
        try (Session session = driver.session()) {
            return session.executeRead(tx -> {
                Query query = new Query(String.format("MATCH (start {id: $rootID})-[:%s]->(n)", parentChildRelationship) +
                        "            RETURN DISTINCT n", parameters("rootID", id(node)));
                Result result = tx.run(query);
                return result.list();
            });
        }
    }

    public Record createSummary(String textContent, Record node) {
        try (Session session = driver.session()) {
            Record record = session.executeWrite(tx -> {
                Query query = new Query("MATCH (p {id: $parentId}) " +
                        String.format("CREATE (n:%s {id: $childId, type: 'SUMMARY', text: $text}) ", SUMMARY_NODE) +
                        String.format("CREATE (p)-[:%s]->(n) ", SUMMARISED_BY) +
                        "RETURN n",
                        parameters(TEXT, textContent, "parentId", id(node), "childId", UUID.randomUUID().toString()));
                return tx.run(query).single();
            });
            return record;
        }
    }

    public Record createNode(WoofNode node) {
        try (Session session = driver.session()) {
            Record record = session.executeWrite(tx -> {
                return node.run(tx).single();
            });
            return record;
        }
    }

    public Record node(Object id) {
        try (Session session = driver.session()) {
            Record record = session.executeWrite(tx -> {
                return tx.run(new Query("MATCH (n {id: $id}) RETURN n", Values.parameters("id", id))).single();
            });
            return record;
        }
    }

    public Record connect(Record parent, Record child, String relationshipName) {
        try (Session session = driver.session()) {
            System.out.println("Connecting");
            Record record = session.executeWrite(tx -> {
                Query query = new Query("MATCH (p {id: $parentId}) " +
                        " MATCH (c {id: $childId}) " +
                        String.format("MERGE (p)-[r:%s]->(c) ", relationshipName) +
                        "RETURN p, c, r",
                        parameters("parentId", id(parent), "childId", id(child)));
                return tx.run(query).single();
            });
            return record;
        }
    }
    List<Record> findNodes(List<String> labels, Map<String, Object> propertySpec) {
        try (Session session = driver.session()) {
            List<Record> records = session.executeWrite(tx -> {
                String nodeIdentifier = labelSearchSpecs(labels);
                List<String> searchSpecs = propertySpec.entrySet().stream().map(e -> e.getKey() + ": $" + e.getKey()).toList();
                String propertySearchSpec = String.join(",", searchSpecs);
                String queryString = String.format("MATCH (%s {%s}) ", nodeIdentifier, propertySearchSpec) + " RETURN n";
                return tx.run(queryString, propertySpec).list();
            });
            return records;
        }
    }

    private String labelSearchSpecs(List<String> labels) {
        if (labels.isEmpty()) return "n";
        return "n:" + String.join(":", labels);
    }

    public Record newOrExisting(List<String> labels, Map<String, Object> propertySpec, WoofNode newNode) {
        List<Record> nodes = findNodes(labels, propertySpec);
        Record record = nodes.isEmpty() ? createNode(newNode) : nodes.getFirst();
        return record;
    }

    public void connect(WoofNode from, WoofNode to, String relationshipName) {
        connect(createNode(from), createNode(to), relationshipName);
    }

    public List<Record> findNodes(NodeSpec spec) {
        return findNodes(spec.labels(), spec.properties());
    }

    public Record newOrExisting(NodeSpec spec, WoofNode node) {
        return newOrExisting(spec.labels(), spec.properties(), node);
    }

    public void contains(Record parent, Record child) {
        connect(parent, child, CONTAINS);
    }

    public void jumpsTo(Record source, Record destination) {
        connect(source, destination, JUMPS_TO);
    }

    public void isFollowedBy(Record current, Record next) {
        connect(current, next, FOLLOWED_BY);
    }

    public void startsWith(Record start, Record end) {
        connect(start, end, STARTS_WITH);
    }

    public void modifies(Record modifier, Record modified) {
        connect(modifier, modified, MODIFIES);
    }
}
