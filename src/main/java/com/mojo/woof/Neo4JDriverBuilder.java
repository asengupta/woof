package com.mojo.woof;

import org.neo4j.driver.*;

public class Neo4JDriverBuilder {
    private String uri;
    private String user;
    private String password;
    private String database;

    public Neo4JDriverBuilder credentials(String uri, String user, String password, String database) {
        this.uri = uri;
        this.user = user;
        this.password = password;
        this.database = database;
        return this;
    }

    public Neo4JDriverBuilder fromEnv() {
        uri = System.getenv("NEO4J_URI");
        user = System.getenv("NEO4J_USERNAME");
        password = System.getenv("NEO4J_PASSWORD");
        database = System.getenv("NEO4J_DATABASE") != null ? System.getenv("NEO4J_DATABASE") : "neo4j";

        System.out.println("Neo4J URI: " + uri);
        System.out.println("Neo4J DATABASE: " + database);

        return this;
    }

    private AuthToken auth() {
        return AuthTokens.basic(user, password);
    }

    public Driver driver() {
        return GraphDatabase.driver(uri, auth());
    }

    public SessionConfig sessionConfig() {
        return SessionConfig.forDatabase(database);
    }
}
