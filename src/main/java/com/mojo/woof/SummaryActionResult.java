package com.mojo.woof;

public class SummaryActionResult implements ActionResult {
    private final String summary;

    public SummaryActionResult(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return summary;
    }

    @Override
    public String shortDescription() {
        return summary.substring(0, 15);
    }
}
