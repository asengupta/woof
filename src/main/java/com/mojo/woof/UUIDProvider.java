package com.mojo.woof;

import java.util.UUID;

public class UUIDProvider {
    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
