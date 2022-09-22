package com.braveinnov.helpers;

import java.util.ArrayList;
import java.util.List;

public class RouteDefinitionHelper {

    private static List<String> enabledRoutes = new ArrayList<>();

    static {
        // enabledRoutes.add("mutation/project_create");
    }

    public static boolean isEnabled(String path) { 
        System.out.println(">>> " + path);
        if (enabledRoutes.size() <= 0) return true;
        return enabledRoutes.indexOf(path) >= 0;
    }
    
}
