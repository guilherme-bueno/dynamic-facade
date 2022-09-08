package com.braveinnov.routes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestDefinition;
import org.springframework.context.annotation.Configuration;

import com.braveinnov.models.PongRequest;
import com.braveinnov.models.PongResponse;
import com.google.gson.Gson;

@Configuration
public class DynamicRoutesBasedOnGraphQLSchema extends RouteBuilder{

    private static final String PREFIX = "/dynamic/";

    @Override
    public void configure() throws Exception {

        List<DynamicRestRouteDefinition> definitions = new ArrayList<>();
        definitions.add(new DynamicRestRouteDefinition("ping", PongRequest.class, PongResponse.class));

        RestDefinition rest = rest();

        definitions.forEach(definition -> {
            rest.post(PREFIX + definition.getPath())
                .consumes("application/json")
                .produces("application/json")
                .type(definition.getRequestType())
                .outType(definition.getResponseType())
                .to("direct:process-request");
        });

        from("direct:process-request")
            .process(exchange -> {
                String request = exchange.getIn().getBody(String.class);
                System.out.println("Receiving request..." + request);
                String content = new Gson().toJson(new PongResponse("200", "Success"));
                System.out.println("Response: " + content);
                exchange.getIn().setBody(content);
                // exchange.getIn().setBody("Hello Wolrd!!!!");
            });
    }

    static class DynamicRestRouteDefinition { 
        
        private final String path;
        private final Class requestType;
        private final Class responseType;

        public DynamicRestRouteDefinition(String path, Class requestType, Class responseType) {
            this.path = path;
            this.requestType = requestType;
            this.responseType = responseType;
        }
        public String getPath() {
            return path;
        }
        public Class getRequestType() {
            return requestType;
        }
        public Class getResponseType() {
            return responseType;
        }

        

    }
}
