package com.mojo.woof;

import org.neo4j.driver.AuthToken;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

public class Neo4JDriverBuilder {
    private String uri;
    private String user;
    private String password;

    public Neo4JDriverBuilder credentials(String uri, String user, String password) {
        this.uri = uri;
        this.user = user;
        this.password = password;

        return this;
    }

    public Neo4JDriverBuilder fromEnv() {
        uri = System.getenv("NEO4J_URI");
        user = System.getenv("NEO4J_USERNAME");
        password = System.getenv("NEO4J_PASSWORD");

        return this;
    }

    private AuthToken auth() {
        return AuthTokens.basic(user, password);
    }

    public Driver driver() {
        return GraphDatabase.driver(uri, auth());
    }
}
