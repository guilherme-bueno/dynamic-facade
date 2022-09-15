package com.braveinnov.learning;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.xml.transform.Source;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.reflections.ReflectionUtils;

import com.google.gson.Gson;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.matcher.ElementMatchers;

public class ByteBuddyLearningTests {

    @Test
    public void test() {
        ByteBuddy bb = new ByteBuddy();
        Builder<Object> typeBuilder = 
            bb.subclass(Object.class)
                .name("com.brave.Hello");

        ReceiverTypeDefinition<Object> typeDefinitionReceiver = null;

        Class<? extends Object> loaded = 
        typeBuilder.defineField("no_universal_entity", String.class, Visibility.PUBLIC)
                    .defineMethod("getNo_universal_entity", String.class, Visibility.PUBLIC).intercept(FieldAccessor.ofField("no_universal_entity"))
                    .defineMethod("setNo_universal_entity", String.class, Visibility.PUBLIC).withParameter(String.class).intercept(FieldAccessor.ofField("no_universal_entity"))
                    .make()
                    .load(ClassLoader.getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded();
                

        System.out.println("Loaded " + loaded);
    } 
    
    @Test
    public void shouldCreateLatentFields() throws Exception {

        final int MODIFIERS = 0;

        TypeDescription typeDescription = TypeDescription.ForLoadedType.of(String.class);
        FieldDescription description = new FieldDescription.Latent(typeDescription, "latentField", MODIFIERS, TypeDescription.OBJECT.asGenericType(), null);
        
        Class<?> dynamicType = new ByteBuddy()
            .subclass(Object.class)
                .method(ElementMatchers.named("toString")).intercept(FixedValue.value("Hello World!"))
                .define(description)
            .make()
            .load(getClass().getClassLoader())
            .getLoaded();
            
        System.out.println(dynamicType.getDeclaredConstructor().newInstance().toString());
    }

    @Test
    public void test1() throws Exception {
        Class<? extends Object> loaded = new ByteBuddy()
        .subclass(Object.class)
        .name("ABC")
        .defineField("listA", 
            TypeDescription.Generic.Builder.parameterizedType(
                List.class, TargetType.class).build(),
            Visibility.PRIVATE)
        .defineField("propA", TargetType.class, Visibility.PRIVATE)
        .make().load(getClass().getClassLoader())
        .getLoaded();

        Object obj = loaded.getDeclaredConstructor().newInstance();
        Field[] declaredFields = loaded.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            System.out.println(field.getName() + " - " + field.getType());
            field.setAccessible(false);
        }
    }
}
