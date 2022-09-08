package com.braveinnov.graphql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.introspection.IntrospectionResultToSchema;
import graphql.language.FieldDefinition;
import graphql.language.InputObjectTypeDefinition;
import graphql.language.ObjectTypeDefinition;
import graphql.language.Type;
import graphql.language.TypeName;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.implementation.FieldAccessor;

public class GraphQLSchemaWrapperTest {

    private GraphQLSchemaWrapper schema;

    @Before
    public void init() throws FileNotFoundException, IOException {
        String schemaStr = IOUtils.toString(new FileInputStream("src/test/resources/schema.json"), Charset.defaultCharset());
        

        IntrospectionResultToSchema parser = new IntrospectionResultToSchema();

        Map<String, Object> map = new Gson().fromJson(schemaStr, Map.class);
        ExecutionResult result = new ExecutionResultImpl(map, null);

        Map<String, Object>  data = result.getData();
        Map<String, Object> sc = (Map<String, Object>) data.get("data");
        schema = new GraphQLSchemaWrapper(parser.createSchemaDefinition(sc));
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
                TypeName complexType = (TypeName) GraphQLSchemaWrapper.getComplexTypeOf(input);
                System.out.println(input.getName() + " " + input.getType() + " " + complexType);
                Class dynamicType = schema.getDynamicType(complexType.getName());
                System.out.println("dynamicType: " + dynamicType);
            });
        });
    }

    @Test
    public void shouldGetTypeDefinition() {
        Class generatedType = schema.getGeneratedType("ItemInput");
        System.out.println("generatedType: " + generatedType);
    }
}
