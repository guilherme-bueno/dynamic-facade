package com.braveinnov.graphql;

import java.util.List;

public class TypeScaffold {

    private final String name;
    private final List<FieldScaffold> fields;

    public TypeScaffold(String name, List<FieldScaffold> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<FieldScaffold> getFields() {
        return fields;
    }
}
