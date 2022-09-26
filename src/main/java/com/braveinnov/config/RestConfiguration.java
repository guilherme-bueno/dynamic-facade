package com.braveinnov.config;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfiguration extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration()
            .component("servlet")
            .contextPath("ws")
            .host("localhost")
            .enableCORS(true)
            .bindingMode(RestBindingMode.auto)
            .jsonDataFormat("json-gson")
            .dataFormatProperty("prettyPrint", "true")
            .apiContextPath("api-doc")
            .apiVendorExtension(true)
                .apiProperty("api.title", "User API")
                .apiProperty("api.version", "1.0.0")
                .apiProperty("cors", "true");
    }

}
