package com.braveinnov.graphql.types.loader.strategies;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import com.braveinnov.controller.DynamicTypesHelper;
import com.braveinnov.graphql.TypeScaffold;
import com.braveinnov.graphql.types.loader.TypesLoader;

@Component
public class TypesLoaderGraphStrategy implements TypesLoader {

    public void loadTypes(Map<String, Class> classes, List<TypeScaffold> types) {

        List<TypeScaffoldNode> nodes = types.stream().map(type -> new TypeScaffoldNode(type)).collect(Collectors.toList());
        Map<String, TypeScaffoldNode> map = nodes.stream().collect(Collectors.toMap((type) -> type.getValue().getName(), type -> type));
        nodes.forEach(node -> {
            if (node.getValue().getName().equalsIgnoreCase("ClientSurveyInputType")) {
                System.out.println("ClientSurveyInputType....");
            }
            node.getValue().getDependencies().forEach(dependency -> {
                if (dependency.equalsIgnoreCase(node.getValue().getName())) {
                    System.out.println("Self reference for: " + dependency);

                } else {
                    TypeScaffoldNode dependencyNode = map.get(dependency);
                    /**
                     * Note: In some cases the node was not added yet.
                     * So we have to add it as a dependency anyway.
                     */
                    if (dependencyNode != null) {
                        node.add(dependencyNode);
                    } else {
                        node.add(TypeScaffoldNode.lazy(dependency));
                    }
                }
            });
        });

        System.out.println(nodes);

        load(classes, nodes);
    }

    private void load(Map<String, Class> classes, List<TypeScaffoldNode> nodes) {
        if (nodes == null || nodes.size() <= 0) return;
        nodes.forEach(node -> {
            System.out.println(node.getValue().getName() + " " + node.isVisited());
            if (!node.isVisited()) {
                if (node.getNodes().size() <= 0) {
                    node.setVisited(true);
                    System.out.println("TypesLoaderGraphStrategy loading " + node.getValue().getName());
                    DynamicTypesHelper.loadInJVM(node.getValue(), classes);
                } else {
                    load(classes, node.getNodes());
                    node.setVisited(true);
                    DynamicTypesHelper.loadInJVM(node.getValue(), classes);
                }
            }
        });
    }
}
