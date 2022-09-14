package com.braveinnov.graphql.types.loader;

import java.util.List;
import java.util.Map;

import com.braveinnov.graphql.TypeScaffold;

public interface TypesLoader {
    
    public void loadTypes(Map<String, Class> classes, List<TypeScaffold> types);
    
}
