package com.braveinnov.graphql.types.loader.strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.braveinnov.graphql.TypeScaffold;

public class TypeScaffoldNode {
    
    private final TypeScaffold value;
    private final List<TypeScaffoldNode> nodes = new ArrayList<>();
    private boolean visited;
    private boolean lazy = false;
    private final Map<String, DependencyCounter> counter;

    public boolean isLazy() {
      return lazy;
    }

    public TypeScaffoldNode(TypeScaffold value, Map<String, DependencyCounter> counter) {
        this.value = value;
        this.counter = counter;
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

    public void incrementCounter(String parent){
        DependencyCounter counter = this.counter.getOrDefault(this.value.getName(), new DependencyCounter(0, Arrays.asList(parent)));
        counter.addRequester(parent);
        counter.incrementCounter();
        this.counter.put(this.value.getName(), counter);
    }

    public int getOwnCounter() {
        return this.counter.getOrDefault(this.value.getName(), new DependencyCounter(0, null)).getCounter();
    }

    public static class DependencyCounter {
        private int counter;
        private final Set<String> requestedBy = new LinkedHashSet<>();

        public DependencyCounter(int counter, List<String> requestedBy) {
            this.counter = counter;
            if (requestedBy != null) this.requestedBy.addAll(requestedBy);
        }

        public int getCounter() {
            return counter;
        }

        public Set<String> getRequestedBy() {
            return requestedBy;
        }

        public void addRequester(String name){
            this.requestedBy.add(name);
        }

        public void incrementCounter() {
            this.counter = this.counter + 1;
        }
    }
}