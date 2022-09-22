package com.braveinnov.routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestDefinition;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.context.annotation.Configuration;

import com.braveinnov.GraphQLTypesClassLoader;
import com.braveinnov.controller.GraphQLSchemaController;
import com.braveinnov.helpers.RouteDefinitionHelper;
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

        this.schemaController
            .loadMutationsAsResourceDefinition()
            .stream()
            .filter(definition -> {
                return RouteDefinitionHelper.isEnabled(definition.getPath());
            })
            .forEach(definition -> {
            try {
                Class<? extends Object> responseType = null;
                Class<? extends Object> requestType = null;
                if (definition.getResponseType() == null) {
                    responseType = String.class;
                }

                if (definition.getRequestType() == String.class) {
                    requestType = null;
                }

                RestDefinition restDefinition = rest.post(PREFIX + definition.getPath())
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

                if(requestType != null) restDefinition = restDefinition.type(requestType);
                if(definition.getResponseType() != null) restDefinition = restDefinition.outType(definition.getResponseType());
                restDefinition.to("direct:process-request");
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        });

        this.schemaController.
            loadQueriesAsResourceDefinition()
            .stream()
            .filter(definition -> {
                return RouteDefinitionHelper.isEnabled(definition.getPath());
            })
            .forEach(definition -> {
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

                if(definition.getRequestType() != null) restDefinition = restDefinition.type(definition.getRequestType());
                if(definition.getResponseType() != null) restDefinition = restDefinition.outType(definition.getResponseType());
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
