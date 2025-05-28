package com.mojo.woof.llm;

import java.util.List;

public interface Advisor {
    List<String> advise(String prompt);
}
