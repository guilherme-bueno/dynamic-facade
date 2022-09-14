package com.braveinnov.graphql.types;

import graphql.language.TypeName;

public class ComplexType {
    
    private final TypeName type;
    private boolean isArray;

    public ComplexType(TypeName type, boolean isArray) {
        this.type = type;
        this.isArray = isArray;
    }

    public ComplexType(TypeName type) {
        this(type, false);
    }

    public TypeName getType() {
        return type;
    }

    public boolean isArray() {
        return isArray;
    }
    
}
