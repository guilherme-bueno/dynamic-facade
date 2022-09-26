package com.braveinnov.graphql.types.loader.strategies;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import com.braveinnov.graphql.GraphQLSchemaWrapper;
import com.google.gson.Gson;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.introspection.IntrospectionResultToSchema;

public class TypesLoaderLatentStrategyTest {

    private GraphQLSchemaWrapper schema;

    @Before
    public void init() throws Exception {
        String schemaStr = IOUtils.toString(new FileInputStream("src/test/resources/schema.json"), Charset.defaultCharset());
        IntrospectionResultToSchema parser = new IntrospectionResultToSchema();

        Map<String, Object> map = new Gson().fromJson(schemaStr, Map.class);
        ExecutionResult result = new ExecutionResultImpl(map, null);

        Map<String, Object>  data = result.getData();
        Map<String, Object> sc = (Map<String, Object>) data.get("data");
        schema = new GraphQLSchemaWrapper(parser.createSchemaDefinition(sc), new TypesLoaderLatentStrategy());
    }

    @Test
    public void shouldGetTypeDefinition() {
        Class generatedType = schema.getGeneratedType("AngleInputType");
        
        System.out.println("\n\ngeneratedType: " + generatedType);
        for (Field field : generatedType.getDeclaredFields()) {
            System.out.println(field.getName() + " " + field.getType());
        }
    }
    
}
