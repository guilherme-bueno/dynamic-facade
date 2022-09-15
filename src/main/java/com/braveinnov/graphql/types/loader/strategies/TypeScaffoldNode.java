package com.braveinnov.graphql.types.loader.strategies;

import java.util.ArrayList;
import java.util.List;

import com.braveinnov.graphql.TypeScaffold;

public class TypeScaffoldNode {
    
    private final TypeScaffold value;
    private final List<TypeScaffoldNode> nodes = new ArrayList<>();
    private boolean visited;
    private boolean lazy = false;

    public static TypeScaffoldNode lazy(String name) {
        TypeScaffoldNode scaffold = new TypeScaffoldNode(new TypeScaffold(name));
        scaffold.lazy = true;
        return scaffold;
    }

    public boolean isLazy() {
      return lazy;
    }

    public TypeScaffoldNode(TypeScaffold value) {
        this.value = value;
    }

    public TypeScaffold getValue() {
        return value;
    }

    public List<TypeScaffoldNode> getNodes() {
        return nodes;
    }

    public boolean add(TypeScaffoldNode node) {
        return nodes.add(node);
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public boolean isVisited() {
        return visited;
    }
}