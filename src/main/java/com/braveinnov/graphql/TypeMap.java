package com.braveinnov.graphql;

import java.util.Date;

public enum TypeMap {
    
    ID {
        Class getType() {
            return String.class;
        }
    },String {
        Class getType() {
            return String.class;
        }
    },Int {
        Class getType() {
            return Integer.class;
        }
    },ISO8601DateTime {
        Class getType() {
            return Date.class;
        }
    };

    abstract Class getType();

}
