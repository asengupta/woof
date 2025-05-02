package com.mojo.woof;

import java.util.List;
import java.util.Map;

public record WoofEdge(WoofNode from,
                       WoofNode to,
                       Map<String, Object> properties,
                       List<String> labels) {
}
