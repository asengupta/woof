package com.mojo.woof;

import java.util.List;

public class DataStructureSummaryActionResult implements ActionResult {
    private final List<String> domains;

    public DataStructureSummaryActionResult(List<String> domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return String.join(",", domains);
    }

    @Override
    public String shortDescription() {
        return toString();
    }
}
