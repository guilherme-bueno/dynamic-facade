package com.braveinnov;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class GraphQLDynamicRoutes extends RouteBuilder{

    @Autowired
    private CamelContext ctx;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        // System.out.println("Executing GraphQLDynamicRoutes...");
        // ProducerTemplate template = ctx.createProducerTemplate();

        // String endpoint = "graphql://http://localhost:3000/graphql?queryFile=queries/introspection.graphql";
        // String response = template.requestBody(endpoint, "", String.class);

        // System.out.println("Response: " + response);

        // // GraphQLSchema.newSchema().

        // Map<String, Map<String, Map>> top = new GsonBuilder().create().fromJson(response, Map.class);
        // System.out.println("top: " + top);
        // System.out.println("data: " + top.get("data"));
        // Map source = top.get("data").get("__schema");

        // String root = new Gson().toJson(source);

        // MyGraphQLSchema schema = new GsonBuilder().create().fromJson(root, MyGraphQLSchema.class);
        // System.out.println("Schema: " + schema);
    }

    @Override
    public void configure() throws Exception {
        
    }   
}