package com.braveinnov.graphql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.InputValueDefinition;
import graphql.language.NonNullType;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeName;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.description.modifier.Visibility;

public class GraphQLSchemaWrapper {

    private final Document document;
    private final List<Definition> definitions;
    private final Predicate<Definition> onlyObjectTypeDefinitionFilter = (defO) ->  defO instanceof ObjectTypeDefinition;
    private final Predicate<Definition> onlyInputObjectTypeDefinitionFilter = (defO) ->  defO instanceof InputObjectTypeDefinition;
    private List<ObjectTypeDefinition> objectTypeDefinitions;
    private List<InputObjectTypeDefinition> inputTypeDefinitions;
    private ObjectTypeDefinition mutation;
    
    private final Map<String, Class> dynamicTypes = new HashMap<>();


    public Predicate<ObjectTypeDefinition> filterByName(String name){
        return item -> item.getName().equalsIgnoreCase("Mutation");
    }

    public GraphQLSchemaWrapper(Document document) {
        this.document = document;
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

        generateDynamicTypes();
    }

    private void generateDynamicTypes() {
        inputTypeDefinitions.forEach(type -> {
            final String name = type.getName();
            final List<FieldScaffold> fields = new ArrayList<>();
            final TypeScaffold scaffold = new TypeScaffold(name, fields);

            type.getInputValueDefinitions().forEach(item -> {
                TypeName typeName = (TypeName) item.getType();
                System.out.println(item.getName() + " " + typeName.getName() + " - " + TypeMap.valueOf(typeName.getName()).getType());
                FieldScaffold f = new FieldScaffold(item.getName(), TypeMap.valueOf(typeName.getName()).getType());
                fields.add(f);

                ByteBuddy bb = new ByteBuddy();
                Builder<Object> typeBuilder = 
                    bb.subclass(Object.class)
                        .name(scaffold.getName());

                ReceiverTypeDefinition<Object> typeDefinitionReceiver = null;

                for (FieldScaffold field : fields) {
                    if (typeDefinitionReceiver != null) {
                        typeDefinitionReceiver = typeDefinitionReceiver.defineField(field.getName(), field.getType(), Visibility.PRIVATE)
                        .defineMethod("get"+field.getName(), field.getType(), Visibility.PUBLIC).intercept(FieldAccessor.ofBeanProperty())
                        .defineMethod("set"+field.getName(), field.getType(), Visibility.PUBLIC).withParameter(String.class).intercept(FieldAccessor.ofBeanProperty());
                    } else {
                        typeDefinitionReceiver = typeBuilder.defineField(field.getName(), field.getType(), Visibility.PRIVATE)
                        .defineMethod("get"+field.getName(), field.getType(), Visibility.PUBLIC).intercept(FieldAccessor.ofBeanProperty())
                        .defineMethod("set"+field.getName(), field.getType(), Visibility.PUBLIC).withParameter(String.class).intercept(FieldAccessor.ofBeanProperty());
                    }
                }

                Class<? extends Object> generatedType = typeDefinitionReceiver.make().load(getClass().getClassLoader())
                .getLoaded();

                dynamicTypes.put(name, generatedType);
            });
        });
    }

    public Class getGeneratedType(String name){ 
        return dynamicTypes.get(name);
    }

    public ObjectTypeDefinition getMutation(){
        return mutation;
    }

    public InputObjectTypeDefinition getInputType(String name) {
        return inputTypeDefinitions.stream().filter(typeDefinition -> {
            return typeDefinition.getName().equalsIgnoreCase(name);
        }).collect(Collectors.toList()).get(0);
    }

    public static Type getComplexTypeOf(InputValueDefinition input){
        Type type = input.getType();
        if (type instanceof NonNullType) {
            return ((NonNullType) type).getType();
        } else {
            return type;
        }
    }

    public Class getDynamicType(String type) {
        return this.dynamicTypes.get(type);
    }
}
