package com.braveinnov.graphql;

import java.util.List;

import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;

public class MutationWrapper {

    private final ObjectTypeDefinition mutation;
    private List<FieldDefinition> fieldDefinitions;

    public MutationWrapper(ObjectTypeDefinition mutation) {
        this.mutation = mutation;
    }

    public void getServices() {
        fieldDefinitions = this.mutation.getFieldDefinitions();
    }
}
