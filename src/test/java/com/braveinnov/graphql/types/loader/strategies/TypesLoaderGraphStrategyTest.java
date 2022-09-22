package com.braveinnov.graphql.types.loader.strategies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.braveinnov.graphql.FieldScaffold;
import com.braveinnov.graphql.TypeMap;
import com.braveinnov.graphql.TypeScaffold;

public class TypesLoaderGraphStrategyTest {

    @Test
    public void test() throws Exception { 
        TypesLoaderGraphStrategy strategy = new TypesLoaderGraphStrategy();
        Map<String,Class> classes = new HashMap<>();

        TypeScaffold typeA = new TypeScaffold("A");
        TypeScaffold typeB = new TypeScaffold("B");
        TypeScaffold typeC = new TypeScaffold("C");
        TypeScaffold typeD = new TypeScaffold("D");
        TypeScaffold typeE = new TypeScaffold("E");

        
        List<FieldScaffold> fieldsA = new ArrayList<>();
        fieldsA.add(new FieldScaffold("propB", TypeMap.UNKNOW, "B"));
        typeA.setFields(fieldsA);

        List<FieldScaffold> fieldsB = new ArrayList<>();
        fieldsB.add(new FieldScaffold("propC", TypeMap.String, "String"));
        typeB.setFields(fieldsB);

        List<TypeScaffold> types = new ArrayList<>();
        types.add(typeA);
        types.add(typeB);
        strategy.loadTypes(classes, types);
    }
    
}
