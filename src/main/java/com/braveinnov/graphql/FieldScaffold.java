package com.braveinnov.graphql;

public class FieldScaffold {

    private final String name;
    private final Class type;
    
    public FieldScaffold(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Class getType() {
        return type;
    }
}