package com.braveinnov.models;

import java.util.ArrayList;
import java.util.List;

public class DynamicRestRouteDefinition { 
    
    private final String path;
    private final Class requestType;
    private final Class responseType;
    private final boolean isResponseArray;

    private final List<ArgumentDefinition> arguments = new ArrayList<>();

    public DynamicRestRouteDefinition(String path, Class requestType, Class responseType, boolean isResponseArray) {
        this.path = path;
        this.requestType = requestType;
        this.responseType = responseType;
        this.isResponseArray = isResponseArray;
    }

    public DynamicRestRouteDefinition(String path, Class requestType, Class responseType, boolean isResponseArray, List<ArgumentDefinition> args) {
        this.path = path;
        this.requestType = requestType;
        this.responseType = responseType;
        this.isResponseArray = isResponseArray;
        this.arguments.addAll(args);
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

    public boolean isResponseArray() {
        return isResponseArray;
    }

    public List<ArgumentDefinition> getArguments() {
        return arguments;
    }
}