package com.braveinnov.models;

import java.util.List;

import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLNamedType;
import graphql.schema.GraphQLObjectType;

public class MyGraphQLSchema {

    private GraphQLObjectType queryType;
    private GraphQLObjectType mutationType;
    private List<GraphQLObjectType> types;
    private List<GraphQLDirective> directives;

    public GraphQLObjectType getQueryType() {
        return queryType;
    }
    public void setQueryType(GraphQLObjectType queryType) {
        this.queryType = queryType;
    }
    public GraphQLObjectType getMutationType() {
        return mutationType;
    }
    public void setMutationType(GraphQLObjectType mutationType) {
        this.mutationType = mutationType;
    }
    public List<GraphQLObjectType> getTypes() {
        return types;
    }
    public void setTypes(List<GraphQLObjectType> types) {
        this.types = types;
    }
    public List<GraphQLDirective> getDirectives() {
        return directives;
    }
    public void setDirectives(List<GraphQLDirective> directives) {
        this.directives = directives;
    }


    
}
