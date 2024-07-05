package com.mojo.woof;

import org.neo4j.driver.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WoofNode {

    private final Map<String, Object> properties;
    private final List<String> labels;

    public WoofNode(Map<String, Object> properties, List<String> labels) {
        this.properties = properties;
        this.labels = labels;
    }

    public Result run(TransactionContext tx) {
        String labelSpec = "n:" + String.join(":", labels);
        String idSpec = "id: \"" + UUID.randomUUID() + "\"";
        String propertySpec = "{" + idSpec + ", " + String.join(", ", properties.keySet().stream().map(k -> k + ": $" + k).toList()) + "}";
        String queryString = String.format("CREATE (%s %s) RETURN n", labelSpec, propertySpec);
        return tx.run(queryString, properties);
    }
}
