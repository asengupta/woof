package com.mojo.woof;

import org.neo4j.driver.Record;

import java.util.Arrays;
import java.util.List;

import static com.mojo.woof.NodeAccess.source;

public class DataStructureSummariseAction implements NodeAction {
    private final Advisor advisor;
    private final GraphSDK sdk;

    public DataStructureSummariseAction(Advisor advisor, GraphSDK sdk) {
        this.advisor = advisor;
        this.sdk = sdk;
    }

    @Override
    public ActionResult apply(Record node, List<ActionResult> childResults) {
        List<String> childStrings = childResults.stream().map(ActionResult::toString).toList();
        String s = NodeAccess.name(node) + " is of type " + NodeAccess.type(node) + " and is composed of [" + String.join(",", childStrings) + "]";
        String prompt = "You are an automotive domain expert. Without any extra text, output a list (with a maximum size of 2) of possible sub-domains (delimited by commas) that this data structure could be associated with: " + s;
        System.out.println("Prompt is : " + prompt);
        List<String> advice = advisor.advise(prompt);
        String summary = advice.stream().reduce("", (a, b) -> a + b);
        List<String> domains = Arrays.asList(summary.split(","));
        domains.forEach(domain -> sdk.createSummary(domain, node));
        return new DataStructureSummaryActionResult(domains);
    }
}
