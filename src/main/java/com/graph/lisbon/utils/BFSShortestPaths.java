package com.graph.lisbon.utils;

import com.graph.lisbon.entities.Graph;
import com.graph.lisbon.entities.Node;

import java.util.*;

public class BFSShortestPaths {
    private Map<Node, Boolean> marked;      //Mark if the node has been reached
    private Map<Node, Node> previous;       //The predecessor of the node
    private Map<Node, Integer> distance;    //The distance to reach the node

    /**
     * Fill all the attributes of the class in order to find the shortest path with the next method
     * @param graph
     * @param start
     */
    public void bfs(Graph graph, Node start) {
        this.marked = new HashMap<>();
        this.previous = new HashMap<>();
        this.distance = new HashMap<>();

        Stack<Node> stack = new Stack<>();
        stack.add(start);
        marked.put(start, true);
        distance.put(start, 0);

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            for (Node neighbor : graph.getNeighbours(node)) {
                if (!marked.containsKey(neighbor)) {
                    previous.put(neighbor, node);
                    distance.put(neighbor, distance.get(node) + 1);
                    marked.put(neighbor, true);
                    stack.add(neighbor);
                }
            }
        }
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
        System.out.println("BFS Shortest Path from " + path.get(0).getId() + " to " + path.get(path.size() - 1).getId() + ": ");
        for (Node pathNode : path) {
            System.out.print(pathNode.getId() + " ");
        }
        System.out.println();
    }
}
