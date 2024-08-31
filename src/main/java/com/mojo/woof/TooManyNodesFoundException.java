package com.mojo.woof;

public class TooManyNodesFoundException extends RuntimeException {
    public TooManyNodesFoundException(NodeSpec spec) {
        super("Too many nodes found with spec: " + spec + " not found");
    }
}
