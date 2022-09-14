package com.braveinnov.graphql;

public class FieldScaffold {

    private final String name;
    private final TypeMap type;
    private final String typeName;
    private final boolean isPrimitive;
    
    public FieldScaffold(String name, TypeMap type, String typeName) {
        this.name = name;
        this.type = type;
        this.typeName = typeName;
        this.isPrimitive = false;
    }

    public String getName() {
        return name;
    }

    public TypeMap getType() {
        return type;
    }

    public String getTypeName() {
        return typeName;
    }

    public boolean isPrimitive() {
        return isPrimitive;
    }

    @Override
    public String toString() {
        return "FieldScaffold [name=" + name + ", type=" + type + ", typeName=" + typeName + "]";
    }
}