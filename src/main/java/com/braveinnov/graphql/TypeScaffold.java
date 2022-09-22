package com.braveinnov.graphql;

import java.util.ArrayList;
import java.util.List;

public class TypeScaffold {

    private final String name;
    private List<FieldScaffold> fields = new ArrayList<>();
    private final List<String> dependencies = new ArrayList<>();
    private final boolean isEnum;

    public TypeScaffold(String name, boolean isEnum) {
        this.name = name;
        this.isEnum = isEnum;
    }

    public TypeScaffold(String name) {
        this.name = name;
        this.isEnum = false;
    }

    public void setFields(List<FieldScaffold> fields) {
        this.fields = fields;
        this.fields.forEach(field -> {
            if (TypeMap.UNKNOW.equals(field.getType())) {
                dependencies.add(field.getTypeName());
            }
        });
    }

    public boolean isEnum() {
        return isEnum;
    }

    public String getName() {
        return name;
    }

    public List<FieldScaffold> getFields() {
        return fields;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "TypeScaffold [dependencies=" + dependencies + ", fields=" + fields + ", name=" + name + "]";
    }
}
