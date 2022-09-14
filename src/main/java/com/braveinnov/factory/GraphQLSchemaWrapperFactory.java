package com.braveinnov.factory;

import java.io.IOException;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.braveinnov.graphql.GraphQLSchemaWrapper;
import com.braveinnov.graphql.types.loader.TypesLoader;
import com.google.gson.Gson;

import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.introspection.IntrospectionResultToSchema;

@Configuration
public class GraphQLSchemaWrapperFactory {

    //@Value("classpath:pistachio.json")
    @Value("classpath:schema.json")
    private Resource resource;

    @Autowired
    private CamelContext ctx;

    @Autowired
    private TypesLoader loader;

    @Bean
    public GraphQLSchemaWrapper schemaWrapper() throws IOException {
        String schema = IOUtils.toString(resource.getInputStream());
        IntrospectionResultToSchema parser = new IntrospectionResultToSchema();
        Map<String, Object> map = new Gson().fromJson(schema, Map.class);
        ExecutionResult result = new ExecutionResultImpl(map, null);

        Map<String, Object>  data = result.getData();
        Map<String, Object> sc = (Map<String, Object>) data.get("data");
        return new GraphQLSchemaWrapper(parser.createSchemaDefinition(sc), loader);
    }


    @Bean
    ServletRegistrationBean servletRegistrationBean() {
        ServletRegistrationBean servlet = new ServletRegistrationBean
        (new CamelHttpTransportServlet(), "/ws/*");
        servlet.setName("CamelServlet");
        return servlet;
    }
    
}
