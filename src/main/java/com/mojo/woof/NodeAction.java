package com.mojo.woof;

import org.neo4j.driver.Record;

import java.util.List;

public interface NodeAction {
    public NodeAction JUST_PRINT = new NodeAction() {
        @Override
        public ActionResult apply(Record node, List<ActionResult> childResults) {
            List<String> childStrings = childResults.stream().map(ActionResult::toString).toList();
            String s = NodeAccess.type(node) + " composed of [" + String.join(",", childStrings) + "]";
            System.out.println(s);
            return new SummaryActionResult(s);
        }
    };

    ActionResult apply(Record node, List<ActionResult> childResults);
}
