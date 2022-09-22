package com.braveinnov.helpers;

import java.util.HashMap;
import java.util.Map;

public class FieldNameHelper {

    private static final Map<String, String> names = new HashMap<>();

    static { 
        names.put("default", "_default");
        names.put("public", "_public");
    }
    
    public static String getFieldName(String name) {
        return names.getOrDefault(name, name);
    }
}
