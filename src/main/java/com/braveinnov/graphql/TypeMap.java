package com.braveinnov.graphql;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public enum TypeMap {
    
    ID {
        public Function<Map<String, Class>, Class> getType(String name) {
            return (Map<String, Class> map) -> String.class;
        }
    },String {
        public Function<Map<String, Class>, Class> getType(String name) {
            return (Map<String, Class> map) -> String.class;
        }
    },Int {
        public Function<Map<String, Class>, Class> getType(String name) {
            return  (Map<String, Class> map) -> Integer.class;
        }
    },ISO8601DateTime {
        public Function<Map<String, Class>, Class> getType(String name) {
            return (Map<String, Class> map) -> Date.class;
        }
    },
    UNKNOW {
        public Function<Map<String, Class>, Class> getType(String name) {
            return (Map<String, Class> map) -> {
                Class clazz = map.get(name);
                if (clazz == null) {
                    System.out.println("Not found class " + name + ". Available: " + map.keySet());
                    return null;
                } else {
                    return clazz;
                }
            };
        }
    },
    Boolean {
        public Function<Map<String, Class>, Class> getType(String name) {
            return (Map<String, Class> map) -> Boolean.class;
        }
    };

    public abstract Function<Map<String, Class>, Class> getType(String name);

    public static TypeMap loadType(String name) {
        try {
            return TypeMap.valueOf(name);
        } catch(IllegalArgumentException exc) {
            return UNKNOW;
        }
    }
}
