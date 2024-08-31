package com.mojo.woof;

public class NodeNotFoundException extends RuntimeException {
    public NodeNotFoundException(NodeSpec spec) {
        super("Node with spec: " + spec + " not found");
    }
}
