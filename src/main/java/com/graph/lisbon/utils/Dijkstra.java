package com.graph.lisbon.utils;

import com.graph.lisbon.entities.Edge;
import com.graph.lisbon.entities.Graph;
import com.graph.lisbon.entities.Node;

import java.util.*;

public class Dijkstra {
    private Map<Node, Boolean> marked;      //Mark if the node is reached
    private Map<Node, Node> previous;       //The predecessor of the node
    private Map<Node, Double> distance;     //The distance to reach the node

    /**
     * Fill all the attributes of the class in order to find the shortest path with the next method
     * @param graph
     * @param start
     */
    public void dijkstraSP(Graph graph, Node start) {
        this.marked = new HashMap<>();
        this.previous = new HashMap<>();
        this.distance = new HashMap<>();
        Stack<Node> stack = new Stack<>();

        stack.add(start);
        marked.put(start, true);
        distance.put(start, 0.0);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            for (Map.Entry<Node, Double> neighbor : graph.getNeighboursWithWeights(node).entrySet()) {
                if (!marked.containsKey(neighbor.getKey())) {
                    previous.put(neighbor.getKey(), node);
                    distance.put(neighbor.getKey(), distance.get(node) + neighbor.getValue());
                    marked.put(neighbor.getKey(), true);
                    stack.add(neighbor.getKey());
                }
            }
        }
    }

    /**
     * Give the final distance of between two given nodes
     * @param target
     * @return
     */
    public Double distTo(Node target) {
        for (Map.Entry<Node, Double> d : this.distance.entrySet()) {
            if (d.getKey().getId().equals(target.getId())) {
                return d.getValue();
            }
        }
        return 0.0;
    }

    /**
     * Give the shortest path between the start nodes and the target nodes
     * @param target
     * @return
     */
    public List<Node> getShortestPath(Node target) {
        List<Node> path = new ArrayList<>();
        Node node = target;
        if (this.distance.get(node) != null) {
            while (this.distance.get(node) != 0) {
                path.add(node);
                node = this.previous.get(node);
            }
            path.add(node);
            Collections.reverse(path);
        } else {
            path = null;
        }
        return path;
    }

    /**
     * Print the shortest path
     * @param path
     */
    public void printShortestPath(List<Node> path) {
        System.out.println("Dijkstra Shortest Path from " + path.get(0).getId() + " to " + path.get(path.size() - 1).getId() + ": ");
        for (Node node : path) {
            System.out.print(node.getId() + " ");
        }
        System.out.println();
    }
}


