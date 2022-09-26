package com.braveinnov.routes;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.context.annotation.Configuration;

import com.braveinnov.models.BasicResponse;
import com.google.gson.Gson;

@Configuration
public class RestfulRoutesExample extends RouteBuilder{

  @Override
  public void configure() throws Exception {
    rest("/example")
      .get("proxy")
        .to("http://localhost:3000/hello?bridgeEndpoint=true")
      .get("v1/resource/{id}")
        .description("Example of Restful endpoint using a path param")
        .produces("application/json")
        .outType(BasicResponse.class)
        .param()
          .name("id")
          .description("Description of the param")
          .type(RestParamType.path)
          .example("12345")
          .required(true)
        .endParam()
        .param()
          .name("size")
          .description("Number of items to return - ")
          .type(RestParamType.query)
          .example("10")
        .endParam()
        .to("direct:path-param-example");

    from("direct:path-param-example")
      .process(exchange -> {
        String id = exchange.getIn().getHeader("id", String.class);
        System.out.println("Receving request with id = "+ id);

        Map<String, Object> headers = new HashMap<>();
        headers.put(Exchange.HTTP_QUERY, exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class));
        BasicResponse response = new BasicResponse();
        response.setCode("200");
        response.setMessage("Received id: " + id);
        response.setData(headers);

        String content = new Gson().toJson(response);
        exchange.getIn().setBody(content);
      });
  }
}
