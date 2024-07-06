package com.mojo.woof;

import org.neo4j.driver.Record;

public class NodeAccess {
    public static String type(Record node) {
        return node.get("n").get("type").toString();
    }

    public static Object id(Record node) {
        return node.get("n").get("id");
    }

    public static String source(Record node) {
        return node.get("n").get("text").toString();
    }

    public static String name(Record node) {
        return node.get("n").get("name").toString();
    }
}
