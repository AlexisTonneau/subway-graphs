package com.graph.lisbon.entities;

import com.graph.lisbon.utils.BFSShortestPaths;
import com.graph.lisbon.utils.Dijkstra;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jgrapht.alg.util.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class Graph {
    private int N = 0;                // number of nodes
    private int M = 0;                // number of edges
    Map<Node, List<Edge>> adj = new HashMap<>();    // adjency list

    /**
     * Add a Node to adj
     * @param node
     */
    public void addNode(Node node) {
        if (!this.adj.containsKey(node)) {
            this.adj.put(node, new ArrayList<>());
            this.N++;
        }
    }

    /**
     * Add an Edge to adj
     * @param from
     * @param to
     * @param weight
     */
    public void addEdge(Node from, Node to, double weight) {
        Edge newEdge = new Edge(to, weight);
        boolean hasEdge = false;
        if (this.adj.containsKey(from)) {
            for (Edge edge : this.adj.get(from)) {
                if (edge.getTo().equals(to)) {
                    hasEdge = true;
                    break;
                }
            }
            if (!hasEdge) {
                this.adj.get(from).add(newEdge);
            }
        }
    }

    /**
     * Find a Node in adj with this id
     * @param id
     * @return
     */
    public Node findNodeById(String id) {
        for (Node node : this.adj.keySet()) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    /**
     * Get the neighbours of a node
     * @param node
     * @return
     */
    public List<Node> getNeighbours(Node node) {
        return this.adj.get(node).stream().map(Edge::getTo).collect(Collectors.toList());
    }

    /**
     * Get the neighbours of a node with their weight sorted by weight
     * @param node
     * @return
     */
    public Map<Node, Double> getNeighboursWithWeights(Node node) {
        return this.adj.get(node).stream()
            .sorted((e1, e2) -> Double.compare(e1.getWeight(), e2.getWeight()))
            .collect(Collectors.toMap(Edge::getTo, Edge::getWeight));
    }

    /**
     * Give all the shortests paths that exist
     * @param useDijkstra
     * @return
     */
    private Map<Pair<Node, Node>, List<Node>> findAllShortestPaths(boolean useDijkstra) {
        List<Pair<Node, Node>> allPairsOfNodes = new ArrayList<>();
        Map<Pair<Node, Node>, List<Node>> allPairsWithSP = new HashMap<>();
        Dijkstra dijkstra = new Dijkstra();
        BFSShortestPaths bfsShortestPaths = new BFSShortestPaths();

        for (Node node1 : this.adj.keySet()) {
            for (Node node2 : this.adj.keySet()) {
                if (!allPairsOfNodes.contains(new Pair<>(node2, node1)) && !allPairsOfNodes.contains(new Pair<>(node1, node2)) && node1 != node2) {
                    allPairsOfNodes.add(new Pair<>(node1, node2));
                }
            }
        }

        for (Pair<Node, Node> pair : allPairsOfNodes) {
            List<Node> shortestPath;
            if (useDijkstra) {
                dijkstra.dikstraSP(this, pair.getFirst());
                shortestPath = dijkstra.getShortestPath(pair.getSecond());
            } else {
                bfsShortestPaths.bfs(this, pair.getFirst());
                shortestPath = bfsShortestPaths.getShortestPath(pair.getSecond());
            }
            allPairsWithSP.put(pair, shortestPath);
        }

        return allPairsWithSP;
    }

    /**
     * Give all the edges betweenesses
     * @param useDijkstra
     * @return
     */
    public Map<Pair<Node, Node>, Integer> getAllEdgesBetweennesses(boolean useDijkstra) {
        Map<Pair<Node, Node>, List<Node>> allShortestPaths = this.findAllShortestPaths(useDijkstra);
        Map<Pair<Node, Node>, Integer> allEdgeBetweennesses = new HashMap<>();

        // get number of times the path between two nodes is used
        for (Map.Entry<Pair<Node, Node>, List<Node>> entry : allShortestPaths.entrySet()){
            if (Objects.nonNull(entry.getValue())) {
                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    if (allEdgeBetweennesses.containsKey(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)))) {
                        int currentBetweeness = allEdgeBetweennesses.get(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)));
                        allEdgeBetweennesses.put(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)), currentBetweeness + 1);
                    } else if (allEdgeBetweennesses.containsKey(new Pair<>(entry.getValue().get(i + 1), entry.getValue().get(i)))) {
                        int currentBetweeness = allEdgeBetweennesses.get(new Pair<>(entry.getValue().get(i + 1), entry.getValue().get(i)));
                        allEdgeBetweennesses.put(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)), currentBetweeness + 1);
                    } else {
                        allEdgeBetweennesses.put(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)), 0);
                    }
                }
            }
        }
        return sortEdgeByBetweennesses(allEdgeBetweennesses);
    }

    /**
     * Sort all the edge betweennesses in an descending way
     * @param originalMap
     * @return
     */
    private Map<Pair<Node, Node>, Integer> sortEdgeByBetweennesses(Map<Pair<Node, Node>, Integer> originalMap) {
        return originalMap.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));
    }

    /**
     * Delete an edge of adj for a given pair of node
     * @param pair
     */
    public void deleteEdge(Pair<Node, Node> pair) {
        if (this.adj.containsKey(pair.getFirst())) {
            this.adj.get(pair.getFirst()).removeIf(edge -> edge.getTo().getId().equals(pair.getSecond().getId()));
            this.M--;
        }
        if (this.adj.containsKey(pair.getSecond())) {
            this.adj.get(pair.getSecond()).removeIf(edge -> edge.getTo().getId().equals(pair.getFirst().getId()));
            this.M--;
        }
    }

    /**
     * Identify and create clusters from the graph
     * @param maxClusters useDijkstra
     * @return
     */
    public List<List<Node>> makeCluster(Integer maxClusters, boolean useDijkstra) {

        BFSShortestPaths bfsShortestPaths = new BFSShortestPaths();
        List<List<Node>> allClusters = new ArrayList<>();
        List<Pair<Node, Node>> allRemovedEdges = new ArrayList<>();

        while (allClusters.size() < maxClusters && this.M > 1) {
            Map<Pair<Node, Node>, Integer> edgeBTW = getAllEdgesBetweennesses(useDijkstra);
            Pair<Node, Node> edgeToRemove = edgeBTW.keySet().iterator().next();
            allRemovedEdges.add(edgeToRemove);
            deleteEdge(edgeToRemove);

            List<Node> remainingNodes = new ArrayList<>(this.adj.keySet());
            allClusters = new ArrayList<>();

            while (!remainingNodes.isEmpty()){
                bfsShortestPaths.bfs(this, remainingNodes.get(0)); //get a random node
                List<Node> nodesReached = bfsShortestPaths.getMarked().entrySet().stream()
                    .filter(Map.Entry::getValue)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
                remainingNodes.removeAll(nodesReached);
                allClusters.add(nodesReached);
            }
        }
        System.out.println("We have deleted " + allRemovedEdges.size() + " edges:");
        for (Pair<Node, Node> removeEdge : allRemovedEdges) {
            System.out.print(removeEdge.getFirst().getId() + "->" + removeEdge.getSecond().getId() + " ");
        }
        System.out.println("\n");
        return allClusters;
    }

    /**
     * Print a graph in a more visuable friendly way
     * @return
     */
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<Node, List<Edge>> entry : this.adj.entrySet()) {
            result.append(entry.getKey().getId()).append(" (head): ");
            for (Edge edge : entry.getValue()) {
                result.append(edge.getTo().getId()).append(" (").append(edge.getWeight()).append(")").append(", ");
            }
            result.append("\n");
        }
        return result.toString();
    }

    public static double getPathCost(List<Edge> e) {
        return e.stream().mapToDouble(edge -> edge.getWeight()).sum();
    }
}

