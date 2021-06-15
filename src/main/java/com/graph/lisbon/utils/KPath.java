package com.graph.lisbon.utils;

import com.graph.lisbon.entities.Edge;
import com.graph.lisbon.entities.Graph;
import com.graph.lisbon.entities.Node;

import java.util.*;
import java.util.stream.Collectors;

public class KPath {

    public static List<List<Edge>> kPath(Graph graph, int K, Node source, Node target) {

        // Step 1.A
        List<Edge> sortedEdges = new LinkedList<>();
        for (Map.Entry<Node, List<Edge>> entry: graph.getAdj().entrySet()) {
            sortedEdges.addAll(entry.getValue().stream().sorted().collect(Collectors.toList()));
        }

        // Step 1.B
        List<List<Edge>> P = new ArrayList<>(sortedEdges.size());
        Graph subgraph = graph;
        for (int i = 0; i < sortedEdges.size(); i++) {
            subgraph = createSubgraph(subgraph, sortedEdges.subList(i, sortedEdges.size() - 1));
            P.add(computeODSPWithoutLoopsOnPaths(subgraph, source, target));
        }

        int k = 0;
        List<List<Edge>> results = new LinkedList<>();
        while (k < K && P.size() != 0) {
            // Step 2.A
            int minIndex = 0;
            List<Edge> minP = P.get(minIndex);
            for (int j = 0; j < P.size(); j++) {
                if (Graph.getPathCost(P.get(j)) < Graph.getPathCost(minP)) {
                    minIndex = j;
                    minP = P.get(j);
                }
            }
            List<Edge> p = minP;
            // Step B
            subgraph = createSubgraph(graph, sortedEdges.subList(minIndex, sortedEdges.size() - 1));
            P.set(minIndex, computeNextODSPWithoutLoopsOnPath(subgraph, source, target, results));
            // Step C
            if (k == 0 || results.contains(p)) {
                k += 1;
                results.add(p);
            }
        }
        return results;

    }

    public static Graph createSubgraph(Graph graph, List<Edge> edges) {
        Graph subgraph = new Graph();
        for (Map.Entry<Node, List<Edge>> entry: graph.getAdj().entrySet()) {
            edges.addAll(entry.getValue());

        }
        return subgraph;
    }

    public static List<Edge> computeODSPWithoutLoopsOnPaths(Graph graph, Node source, Node target){
        // YEN Implementation
        return new ArrayList<>();
    }

    public static List<Edge> computeNextODSPWithoutLoopsOnPath(Graph graph, Node source, Node sink, List<List<Edge>> ksp) {
        // Yen implementation
        return new ArrayList<>();
    }


}
