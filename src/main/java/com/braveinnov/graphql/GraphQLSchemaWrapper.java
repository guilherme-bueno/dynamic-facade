package com.braveinnov.graphql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.braveinnov.controller.DynamicTypesHelper;
import com.braveinnov.graphql.types.ComplexType;
import com.braveinnov.graphql.types.loader.TypesLoader;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.ListType;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeName;

public class GraphQLSchemaWrapper {

    private final Document document;
    private final List<Definition> definitions;
    private final Predicate<Definition> onlyObjectTypeDefinitionFilter = (defO) ->  defO instanceof ObjectTypeDefinition;
    private final Predicate<Definition> onlyInputObjectTypeDefinitionFilter = (defO) ->  defO instanceof InputObjectTypeDefinition;
    private List<ObjectTypeDefinition> objectTypeDefinitions;
    private List<InputObjectTypeDefinition> inputTypeDefinitions;
    private ObjectTypeDefinition mutation;
    
    private final Map<String, Class> dynamicTypes = new HashMap<>();
    private TypesLoader loader;
    private ObjectTypeDefinition query;
    


    public Predicate<ObjectTypeDefinition> filterByName(String name){
        return item -> item.getName().equalsIgnoreCase(name);
    }

    public GraphQLSchemaWrapper(Document document, TypesLoader loader) {
        this.document = document;
        this.loader = loader;
        this.definitions = this.document.getDefinitions();
        objectTypeDefinitions = this.definitions.stream()
                                                .filter(onlyObjectTypeDefinitionFilter)
                                                .map(item -> (ObjectTypeDefinition) item)
                                                .collect(Collectors.toList());

        inputTypeDefinitions = this.definitions.stream()
                                                .filter(onlyInputObjectTypeDefinitionFilter)
                                                .map(item -> (InputObjectTypeDefinition) item)
                                                .collect(Collectors.toList());

        mutation = objectTypeDefinitions
                    .stream()
                        .filter(filterByName("Mutation"))
                        .findFirst()
                        .orElse(null);

        query = objectTypeDefinitions
                        .stream()
                            .filter(filterByName("Query"))
                            .findFirst()
                            .orElse(null);

        generateDynamicTypes();
    }

    private void generateDynamicTypes() {

        List<TypeScaffold> types = new ArrayList<>();
        
        inputTypeDefinitions.forEach(type -> {
            final String name = type.getName();
            final List<FieldScaffold> fields = new ArrayList<>();
            final TypeScaffold scaffold = new TypeScaffold(name);
            types.add(scaffold);

            type.getInputValueDefinitions().forEach(item -> {
                try {   
                    TypeName typeName = (TypeName) getComplexTypeOf(item.getType()).getType();
                    System.out.println(item.getName() + " " + typeName.getName() + " - " + TypeMap.loadType(typeName.getName()));
                    FieldScaffold f = new FieldScaffold(item.getName(), TypeMap.loadType(typeName.getName()), typeName.getName());
                    fields.add(f);
                } catch (Exception e) {
                    System.out.println("Error to parse: " + item);
                    throw e;
                }
            });

            scaffold.setFields(fields);
        });

        objectTypeDefinitions.forEach(type -> {
            final String name = type.getName();
            final List<FieldScaffold> fields = new ArrayList<>();
            final TypeScaffold scaffold = new TypeScaffold(name);

            types.add(scaffold);

            type.getFieldDefinitions().forEach(item -> {
                try {
                    if(item.getName().equalsIgnoreCase("name")) {
                        System.out.println("....");
                    }
                    ComplexType typeName = getComplexTypeOf(item);
                    System.out.println(item.getName() + " " + typeName.getType().getName() + " - " + TypeMap.loadType(typeName.getType().getName()).getType(""));
                    FieldScaffold f = new FieldScaffold(item.getName(), TypeMap.loadType(typeName.getType().getName()), typeName.getType().getName());
                    fields.add(f);
                } catch (Exception e) {
                    System.out.println(e);
                }
            });
            scaffold.setFields(fields);
        });

        types.forEach(type -> {
            System.out.println(" - " + type.getName() + " " + type.getDependencies());
        });

        this.loader.loadTypes(this.dynamicTypes, types);

        loadDynamicTypesInJVM(types);
        System.out.println(dynamicTypes.keySet());
    }

    private void loadDynamicTypesInJVM(List<TypeScaffold> types) {
        DynamicTypesHelper.loadInJVM(types, this.dynamicTypes);
    }

    public Class getGeneratedType(String name){ 
        return dynamicTypes.get(name);
    }

    public ObjectTypeDefinition getQuery(){
        return query;
    }

    public ObjectTypeDefinition getMutation(){
        return mutation;
    }

    public InputObjectTypeDefinition getInputType(String name) {
        return inputTypeDefinitions.stream().filter(typeDefinition -> {
            return typeDefinition.getName().equalsIgnoreCase(name);
        }).collect(Collectors.toList()).get(0);
    }

    public static ComplexType getComplexTypeOf(InputValueDefinition input){
        Type type = input.getType();
        if (type instanceof NonNullType) {
            return new ComplexType((TypeName)((NonNullType) type).getType());
        } else if (type instanceof ListType) {
            System.out.println("List Type: " + type);
            return getComplexTypeOf(((ListType) type).getType());
        } else {
            return new ComplexType((TypeName) type);
        }
    }

    public static ComplexType getComplexTypeOf(FieldDefinition input){
        Type type = input.getType();
        if (type instanceof NonNullType) {
            NonNullType nnType = (NonNullType) type;
            ComplexType complexType = getComplexTypeOf(nnType.getType());
            return new ComplexType(complexType.getType());
        } else if (type instanceof ListType) {
            ListType list = (ListType) type;
            ComplexType cListType = getComplexTypeOf(list.getType());
            return new ComplexType(cListType.getType(), true);
        } else {
            return new ComplexType((TypeName) type);
        }
    }

    public static ComplexType getComplexTypeOf(Type input){
        Type type = input;
        if (type instanceof NonNullType) {
            NonNullType nnType = (NonNullType) type;
            ComplexType complexType = getComplexTypeOf(nnType.getType());
            return new ComplexType(complexType.getType());
        } else if (type instanceof ListType) {
            ListType list = (ListType) type;
            ComplexType cListType = getComplexTypeOf(list.getType());
            return new ComplexType(cListType.getType(), true);
        } else {
            return new ComplexType((TypeName) type);
        }
    }

    public Class getDynamicType(String type) {
        return TypeMap.loadType(type).getType(type).apply(this.dynamicTypes);
        // return this.dynamicTypes.get(type);
    }

    public Map<String, Class> getDynamicTypes() {
        return dynamicTypes;
    }
}
