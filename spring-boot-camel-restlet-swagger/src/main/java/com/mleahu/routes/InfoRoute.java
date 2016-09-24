package com.mleahu.routes;

import static org.apache.camel.model.rest.RestParamType.body;

import static org.apache.camel.model.rest.RestParamType.path;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class InfoRoute extends RouteBuilder {

    @Value("${rest.api.base.url}")
    private String restApiBaseUrl;

    @Value("${rest.api.port}")
    private String restApiPort;

    @Override
    public void configure() throws Exception {

    	restConfiguration().component("restlet")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")                
                .contextPath(restApiBaseUrl).host("localhost").port(restApiPort)
                .enableCORS(true)
                .apiContextPath("/api-doc")
                       .apiProperty("api.title", "User API").apiProperty("api.version", "1.2.3");

        // this user REST service is json only
        rest("/user").description("User rest service")
            .consumes("application/json").produces("application/json")

            .get("/findAll").description("Find all users").outTypeList(User.class)
            	.responseMessage().code(200).message("All users").endResponseMessage()
            	.to("bean:userService?method=listUsers")
            .get("/{id}").description("Find user by id").outType(User.class)
                .param().name("id").type(path).description("The id of the user to get").dataType("integer").endParam()
                .responseMessage().code(200).message("The user").endResponseMessage()
                .to("bean:userService?method=getUser(${header.id})")
            .put().description("Updates or create a user").type(User.class)
                .param().name("body").type(body).description("The user to update or create").endParam()
                .responseMessage().code(200).message("User created or updated").endResponseMessage()
                .to("bean:userService?method=updateUser");        
    }
}