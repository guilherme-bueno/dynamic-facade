package com.braveinnov;

import java.util.Map;

public class GraphQLTypesClassLoader extends ClassLoader{

    private final Map<String,Class> classes;

    public GraphQLTypesClassLoader(Map<String,Class> classes) {
        this.classes = classes;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        System.out.println("loadClass: " + className);
        Class clazz = this.classes.get(className);
        if (clazz != null) {
            return clazz;
        } else {
            return super.loadClass(className);
        }
    }
    
}
