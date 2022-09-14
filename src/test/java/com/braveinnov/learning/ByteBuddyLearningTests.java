package com.braveinnov.learning;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.FieldAccessor;

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
}
