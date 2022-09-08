package com.braveinnov;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLSchemaRoutes extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        
        rest()
        .get("/graphql/schema")
            .produces("application/json")
        .to("direct:load-schema");

        from("direct:load-schema")
        .to("graphql://http://localhost:3000/graphql?queryFile=queries/introspection.graphql");
    }
    
}
