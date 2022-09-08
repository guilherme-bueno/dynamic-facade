package com.braveinnov;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.Definition;
import graphql.language.Document;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;

public class ParseGraphQLSchemaTest {

    private Document definition;

    @Before
    public void init() throws FileNotFoundException, IOException {
        String schema = IOUtils.toString(new FileInputStream("src/test/resources/schema.json"), Charset.defaultCharset());
        System.out.println(schema);

        IntrospectionResultToSchema parser = new IntrospectionResultToSchema();

        Map<String, Object> map = new Gson().fromJson(schema, Map.class);
        ExecutionResult result = new ExecutionResultImpl(map, null);

        Map<String, Object>  data = result.getData();
        Map<String, Object> sc = (Map<String, Object>) data.get("data");
        definition = parser.createSchemaDefinition(sc);
    }

    @Test
    public void shouldPrintAllDefinitions() { 
        List<Definition> definitions =  definition.getDefinitions();
        Predicate<Definition> onlyObjectTypeDefinitionFilter = (defO) ->  defO instanceof ObjectTypeDefinition;
        definitions.stream().filter(onlyObjectTypeDefinitionFilter).forEach(defi -> {    
            ObjectTypeDefinition type = (ObjectTypeDefinition) defi;
            System.out.println(type.getName());
            List<FieldDefinition> fieldDefinitions = type.getFieldDefinitions();
            printDefinitions(type, fieldDefinitions);
        });
    }

    @Test
    public void test() throws FileNotFoundException, IOException {
        
    }

    private void printDefinitions(ObjectTypeDefinition type, List<FieldDefinition> fieldDefinitions) {
        fieldDefinitions.forEach(typeDef -> {
            Type type2 = typeDef.getType();
            System.out.println("\t -> " + typeDef.getName() + ", " + type2);
        });
    }
}