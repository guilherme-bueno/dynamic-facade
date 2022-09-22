package com.braveinnov.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;

import com.braveinnov.graphql.GraphQLSchemaWrapper;
import com.braveinnov.graphql.types.ComplexType;
import com.braveinnov.models.ArgumentDefinition;
import com.braveinnov.models.DynamicRestRouteDefinition;

import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;

@Controller
public class GraphQLSchemaController {

    private final GraphQLSchemaWrapper schema;

    public GraphQLSchemaController(GraphQLSchemaWrapper schema) {
        this.schema = schema;
    }

    public List<DynamicRestRouteDefinition> loadMutationsAsResourceDefinition() {
        ObjectTypeDefinition mutation = schema.getMutation();
        List<FieldDefinition> fields = mutation.getFieldDefinitions();

        List<DynamicRestRouteDefinition> routes = new ArrayList<>();

        fields.forEach(field -> {
            System.out.println(field.getName() + " - " + field.getClass().getName() + " - " + field.getType());
            System.out.println("Definitions: " + field.getInputValueDefinitions());

            ComplexType responseType = GraphQLSchemaWrapper.getComplexTypeOf(field);
            Class dynamicTypeResponse = schema.getDynamicType(responseType.getType().getName());


            List<ComplexType> requestTypes = new ArrayList<>();

            if (field.getName().equalsIgnoreCase("activate_profile")) {
                System.out.println(field);
            }

            /**
             * TODO: One Mutation can receive multiple arguments. 
             * This way, we have to create a class that will wrap all these arguments. Or we can receive all as queryStrings.
             * 
             * Example: activate_profile
            **/
            List<ArgumentDefinition> arguments = new ArrayList<>();
            field.getInputValueDefinitions().forEach(input -> {
                ComplexType complexType = GraphQLSchemaWrapper.getComplexTypeOf(input);
                System.out.println(input.getName() + " " + input.getType() + " " + complexType);

                Class dynamicType = schema.getDynamicType(complexType.getType().getName());
                System.out.println("dynamicType: " + dynamicType);
                requestTypes.add(complexType);
                String description = input.getDescription() != null? input.getDescription().getContent() : "";
                arguments.add(new ArgumentDefinition(input.getName(), description));
            });

            ComplexType complexType = requestTypes.stream().findFirst().orElse(null);
            if (complexType != null){
                routes.add(new DynamicRestRouteDefinition("mutation/" + field.getName(), schema.getDynamicType(complexType.getType().getName()), dynamicTypeResponse, responseType.isArray(), arguments));
            }

        });

        System.out.println(routes);

        System.out.println("Routes...");
        routes.forEach(route -> {
            System.out.println(route.getPath() + " " + route.getRequestType() + " " + route.getResponseType() + " " + route.isResponseArray());
        });
        return routes;
    }


    public List<DynamicRestRouteDefinition> loadQueriesAsResourceDefinition() {
        ObjectTypeDefinition query = schema.getQuery();
        List<FieldDefinition> fields = query.getFieldDefinitions();

        List<DynamicRestRouteDefinition> routes = new ArrayList<>();

        fields.forEach(field -> {
            System.out.println(field.getName() + " - " + field.getClass().getName() + " - " + field.getType());
            System.out.println("Definitions: " + field.getInputValueDefinitions());

            ComplexType responseType = GraphQLSchemaWrapper.getComplexTypeOf(field);
            Class dynamicTypeResponse = schema.getDynamicType(responseType.getType().getName());


            List<ArgumentDefinition> arguments = new ArrayList<>();

            field.getInputValueDefinitions().forEach(input -> {
                ComplexType complexType = GraphQLSchemaWrapper.getComplexTypeOf(input);
                System.out.println(input.getName() + " " + input.getType() + " " + complexType);

                Class dynamicType = schema.getDynamicType(complexType.getType().getName());
                System.out.println("dynamicType: " + dynamicType);

                String description = input.getDescription() != null? input.getDescription().getContent() : "";
                arguments.add(new ArgumentDefinition(input.getName(), description));
            });

            if (arguments != null){
                routes.add(
                    new DynamicRestRouteDefinition(
                        "query/" + field.getName(), 
                        null, 
                        dynamicTypeResponse, 
                        responseType.isArray(),
                        arguments));
            }
            //TODO: Should add the else too?

        });

        System.out.println(routes);

        System.out.println("Routes...");
        routes.forEach(route -> {
            System.out.println(route.getPath() + " " + route.getRequestType() + " " + route.getResponseType() + " " + route.isResponseArray());
        });
        return routes;
    }

    public Map<String, Class> getDynamicTypes() {
        return schema.getDynamicTypes();
    }

    
    
}
