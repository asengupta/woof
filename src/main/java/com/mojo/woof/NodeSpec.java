package com.mojo.woof;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

public class NodeSpec {

    private final ImmutableList<String> labels;
    private final Map<String, Object> properties;

    public NodeSpec(ImmutableList<String> labels, Map<String, Object> properties) {
        this.labels = labels;
        this.properties = properties;
    }

    public List<String> labels() {
        return labels;
    }

    public Map<String, Object> properties() {
        return properties;
    }
}
