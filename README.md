# Woof

Woof is a dual-purpose Java library that provides:
1. A convenient wrapper for Neo4J graph database operations
2. Integration with various Large Language Model (LLM) providers

## Features

### Neo4J Graph Operations
- Simple API for creating and manipulating graph nodes and edges
- Support for traversing and querying graph structures
- Semantic relationship definitions for code and data analysis
- Utilities for managing graph connections and transactions

### LLM Integration
- Unified interface for interacting with different LLM providers
- Support for multiple LLM backends:
  - AWS Bedrock
  - Azure OpenAI
  - Ollama (local LLM deployment)
- Simple prompt-based interaction model

## Requirements
- Java 21 or higher
- Neo4J database (for graph operations)
- Access to at least one supported LLM provider (for AI features)

## Installation

Add Woof as a dependency in your Maven project:

```xml
<dependency>
    <groupId>com.mojo.woof</groupId>
    <artifactId>woof</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

## Usage Examples

### Neo4J Graph Operations

```java
// Initialize the Neo4J driver
Neo4JDriverBuilder builder = new Neo4JDriverBuilder("bolt://localhost:7687", "neo4j", "password");

// Create a GraphSDK instance
try (GraphSDK sdk = new GraphSDK(builder)) {
    // Create nodes
    WoofNode node1 = new WoofNode(List.of("Person"), Map.of("name", "Alice"));
    WoofNode node2 = new WoofNode(List.of("Person"), Map.of("name", "Bob"));

    // Add nodes to the graph
    Record record1 = sdk.createNode(node1);
    Record record2 = sdk.createNode(node2);

    // Connect nodes with a relationship
    sdk.connect(record1, record2, "KNOWS", "friendship");
}
```

### LLM Integration

```java
// Using Ollama
OllamaCredentials ollamaCredentials = new OllamaCredentials("http://localhost:11434/api/generate");
Advisor ollamaAdvisor = new OllamaAdvisor(ollamaCredentials);
List<String> responses = ollamaAdvisor.advise("Explain what a graph database is");

// Using AWS Bedrock
AWSCredentials awsCredentials = new AWSCredentials("your-region");
Advisor awsAdvisor = new AWSAdvisor(awsCredentials);
List<String> responses = awsAdvisor.advise("Summarize the key features of Neo4J");
```

## Dependencies
- Neo4j Java Driver
- Google Gson (for JSON processing)
- Lombok
- Apache Commons Lang
- Google Guava
- Azure AI OpenAI Client
- AWS Bedrock Runtime

## License
See the LICENSE file for details.
