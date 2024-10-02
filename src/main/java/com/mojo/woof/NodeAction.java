package com.mojo.woof;

import org.neo4j.driver.Record;

import java.util.List;
import java.util.logging.Logger;

public interface NodeAction {
    java.util.logging.Logger LOGGER = Logger.getLogger(NodeAction.class.getName());

    NodeAction JUST_PRINT = (node, childResults) -> {
        List<String> childStrings = childResults.stream().map(ActionResult::toString).toList();
        String s = NodeAccess.type(node) + " composed of [" + String.join(",", childStrings) + "]";
        LOGGER.info(s);
        return new SummaryActionResult(s);
    };

    ActionResult apply(Record node, List<ActionResult> childResults);
}
