package com.braveinnov.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyRoutesExample extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        from("servlet:/proxy?matchOnUriPrefix=true")
        .to("http://localhost:3000?bridgeEndpoint=true");
    }
    
}
