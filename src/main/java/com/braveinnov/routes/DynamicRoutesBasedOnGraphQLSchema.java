package com.braveinnov.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.context.annotation.Configuration;

import com.braveinnov.GraphQLTypesClassLoader;
import com.braveinnov.controller.GraphQLSchemaController;
import com.braveinnov.models.ArgumentDefinition;
import com.braveinnov.models.DynamicRestRouteDefinition;
import com.braveinnov.models.PongRequest;
import com.braveinnov.models.PongResponse;
import com.google.gson.Gson;

@Configuration
public class DynamicRoutesBasedOnGraphQLSchema extends RouteBuilder{

    private static final String PREFIX = "/dynamic/";

    private final GraphQLSchemaController schemaController;
    private final CamelContext ctx;

    public DynamicRoutesBasedOnGraphQLSchema(GraphQLSchemaController controller, CamelContext ctx) {
        this.schemaController = controller;
        this.ctx = ctx;
    }
    

    @Override
    public void configure() throws Exception {

        List<DynamicRestRouteDefinition> definitions = new ArrayList<>();
        definitions.add(new DynamicRestRouteDefinition("ping", PongRequest.class, PongResponse.class, false));

        RestDefinition rest = rest();

        definitions.forEach(definition -> {
            rest.post(PREFIX + definition.getPath())
                .consumes("application/json")
                .produces("application/json")
                .type(definition.getRequestType())
                .outType(definition.getResponseType())
                .to("direct:process-request");
        });

        Map<String, Class> classes = this.schemaController.getDynamicTypes();
        this.ctx.getClassResolver().addClassLoader(new GraphQLTypesClassLoader(classes));

        this.schemaController.loadMutationsAsResourceDefinition().forEach(definition -> {
            rest.post(PREFIX + definition.getPath())
                .consumes("application/json")
                .produces("application/json")
                .type(definition.getRequestType())
                .outType(definition.getResponseType())
                .to("direct:process-request");
        });

        this.schemaController.loadQueriesAsResourceDefinition().forEach(definition -> {
            RestDefinition restDefinition = rest.get(PREFIX + definition.getPath())
                .consumes("application/json")
                .produces("application/json");

                if(definition.getArguments().size() > 0) {
                    for (ArgumentDefinition argument : definition.getArguments()) {
                        restDefinition = restDefinition
                                                    .param()
                                                        .name(argument.getName())
                                                        .type(RestParamType.query)
                                                        .description(argument.getDescription())
                                                    .endParam();   
                    }
                }

                if(definition.getRequestType() != null) restDefinition.type(definition.getRequestType());
                if(definition.getResponseType() != null) restDefinition.outType(definition.getResponseType());
                restDefinition.to("direct:process-request");
        });

        

        from("direct:process-request")
            .process(exchange -> {
                String request = exchange.getIn().getBody(String.class);
                System.out.println("Receiving request..." + request);
                String content = new Gson().toJson(new PongResponse("200", "Success"));
                System.out.println("Response: " + content);
                exchange.getIn().setBody(content);
            });
    }
}
