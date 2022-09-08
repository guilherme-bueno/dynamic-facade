package com.braveinnov;

import java.lang.reflect.Field;

import org.junit.Test;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.Visibility;

public class ByteBuddyLearningTest{

    @Test
    public void test() {
        System.out.println("Hello World");

        Class<? extends Object> simpleType = new ByteBuddy()
                .subclass(Object.class)
                    .defineField("name", String.class, Visibility.PRIVATE)
                .make()
                .load(getClass().getClassLoader())
                .getLoaded();

        Field [] fields = simpleType.getDeclaredFields();
        System.out.println(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            System.out.println("-" + field.getName());
            field.setAccessible(false);
        }
    }
    
}
