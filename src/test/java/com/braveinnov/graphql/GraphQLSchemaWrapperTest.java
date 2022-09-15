package com.braveinnov.graphql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.braveinnov.controller.GraphQLSchemaController;
import com.braveinnov.graphql.types.ComplexType;
import com.braveinnov.graphql.types.loader.strategies.TypesLoaderGraphStrategy;
import com.braveinnov.models.DynamicRestRouteDefinition;
import com.google.gson.Gson;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.FieldDefinition;
import graphql.language.ObjectTypeDefinition;

public class GraphQLSchemaWrapperTest {

    private GraphQLSchemaWrapper schema;

    @Before
    public void init() throws FileNotFoundException, IOException {
        String schemaStr = IOUtils.toString(new FileInputStream("src/test/resources/pistachio.json"), Charset.defaultCharset());
        

        IntrospectionResultToSchema parser = new IntrospectionResultToSchema();

        Map<String, Object> map = new Gson().fromJson(schemaStr, Map.class);
        ExecutionResult result = new ExecutionResultImpl(map, null);

        Map<String, Object>  data = result.getData();
        Map<String, Object> sc = (Map<String, Object>) data.get("data");
        schema = new GraphQLSchemaWrapper(parser.createSchemaDefinition(sc),  new TypesLoaderGraphStrategy());
    }

    @Test
    public void shouldGetMutation() {
        ObjectTypeDefinition mutation = schema.getMutation();
        Assert.assertNotNull(mutation);
        System.out.println(mutation);
        List<FieldDefinition> fieldDefinitions = mutation.getFieldDefinitions();
        fieldDefinitions.forEach(field -> {
            System.out.println(field.getName() + " - " + field.getClass().getName());
            System.out.println("Definitions: " + field.getInputValueDefinitions());
            field.getInputValueDefinitions().forEach(input -> {
                ComplexType complexType = GraphQLSchemaWrapper.getComplexTypeOf(input);
                System.out.println(input.getName() + " " + input.getType() + " " + complexType);
                Class dynamicType = schema.getDynamicType(complexType.getType().getName());
                System.out.println("dynamicType: " + dynamicType);
            });
        });
    }

    @Test
    public void shouldGetTypeDefinition() {
        Class generatedType = schema.getGeneratedType("AngleInputType");
        
        System.out.println("\n\ngeneratedType: " + generatedType);
        for (Field field : generatedType.getDeclaredFields()) {
            System.out.println(field.getName() + " " + field.getType());
        }
    }

    @Test
    public void shouldParseGraphQLToDynamicRestRouteDefinition() {
        List<DynamicRestRouteDefinition> definitions = new GraphQLSchemaController(schema).loadMutationsAsResourceDefinition();
        System.out.println(definitions);
    }

    
}
