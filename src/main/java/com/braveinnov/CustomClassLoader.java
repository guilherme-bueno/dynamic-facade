package com.braveinnov;

public class CustomClassLoader extends ClassLoader{
    
    private final Class<? extends Object> simpleType;

    public CustomClassLoader(Class<? extends Object> simpleType) {
        this.simpleType = simpleType;
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        System.out.println("loadClass: " + className + " - " + simpleType.getName());
        if (className.equalsIgnoreCase(simpleType.getName())) { 
            return simpleType;
        } else {
            return super.loadClass(className);
        }
    }
}
