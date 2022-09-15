package com.braveinnov.controller;

import java.util.List;
import java.util.Map;

import com.braveinnov.graphql.FieldScaffold;
import com.braveinnov.graphql.TypeScaffold;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

public class DynamicTypesHelper {
    
    public static void  loadInJVM(TypeScaffold type, Map<String, Class> classes) {
        if(classes.containsKey(type.getName())) return;
        ByteBuddy bb = new ByteBuddy();
        Builder<Object> typeBuilder = 
            bb.subclass(Object.class)
                .name(type.getName());

        ReceiverTypeDefinition<Object> typeDefinitionReceiver = null;

        for (FieldScaffold field : type.getFields()) {
            if (typeDefinitionReceiver != null) {
                System.out.println("Field: " + field);
                typeDefinitionReceiver = typeDefinitionReceiver.defineField(field.getName(), field.getType().getType(field.getTypeName()).apply(classes), Visibility.PRIVATE)
                .defineMethod(
                    "get"+toCamelCase(field.getName()), 
                    field.getType().getType(
                    field.getTypeName()).apply(classes), 
                    Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofBeanProperty())
                .defineMethod(
                    "set"+toCamelCase(field.getName()), 
                    field.getType().getType(
                    field.getTypeName()).apply(classes), 
                    Visibility.PUBLIC).withParameter(String.class)
                    .intercept(FieldAccessor.ofBeanProperty());
            } else {
                typeDefinitionReceiver = typeBuilder.defineField(field.getName(), field.getType().getType(field.getTypeName()).apply(classes), Visibility.PRIVATE)
                .defineMethod(
                    "get"+ toCamelCase(field.getName()), 
                    field.getType().getType(field.getTypeName()).apply(classes), 
                    Visibility.PUBLIC)
                    .intercept(FieldAccessor.ofBeanProperty())
                .defineMethod(
                    "set"+ toCamelCase(field.getName()), 
                    field.getType().getType(field.getTypeName()).apply(classes), 
                    Visibility.PUBLIC).withParameter(String.class)
                    .intercept(FieldAccessor.ofBeanProperty());
            }
        }

        Class<? extends Object> generatedType = typeDefinitionReceiver
                                                    .make()
                                                    .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                                                        .getLoaded();

        System.out.println("Loaded " + type.getName());
        classes.put(type.getName(), generatedType);
    }

    public static void  loadInJVM(List<TypeScaffold> types, Map<String, Class> classes) {
        types.forEach(type -> loadInJVM(type, classes));
    }

    private static String toCamelCase(String text) {
        String converted = text.substring(0,1).toUpperCase() + text.substring(1);
        System.out.println("Converted from " + text + " to " + converted);
        return converted;
    }
}
