package com.graph.lisbon.entities;

import com.graph.lisbon.utils.BFSShortestPaths;
import com.graph.lisbon.utils.Dijkstra;
import lombok.Data;
import org.jgrapht.alg.util.Pair;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Data
public class Graph {
    private int N;                      // number of nodes
    private int M;                      // number of edges
    Map<Node, List<Edge>> adj;          // adjency list of our edges

    /**
     * Constructor of the Graph
     */
    public Graph() {
        this.N = 0;
        this.M = 0;
        this.adj = new HashMap<>();
    }

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
     * Give all the shortest path that begin with a given node
     * @param start
     * @param useDijkstra
     * @return
     */
    public Map<Pair<Node, Node>, List<Node>> findAllShortestPathsFromStartNode(Node start, boolean useDijkstra) {
        List<Pair<Node, Node>> allPairsOfNodes = new ArrayList<>();
        Map<Pair<Node, Node>, List<Node>> allPairsWithSP = new HashMap<>();
        Dijkstra dijkstra = new Dijkstra();
        BFSShortestPaths bfsShortestPaths = new BFSShortestPaths();

        for (Node node : this.adj.keySet()) {
            if (!allPairsOfNodes.contains(new Pair<>(start, node)) && !allPairsOfNodes.contains(new Pair<>(node, start)) && start != node) {
                allPairsOfNodes.add(new Pair<>(start, node));
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

        for (Map.Entry<Node, List<Edge>> entry : this.adj.entrySet()) {
            for (Edge edge : entry.getValue()) {
                if (!allEdgeBetweennesses.containsKey(new Pair<>(entry.getKey(), edge.getTo())) && !allEdgeBetweennesses.containsKey(new Pair<>(edge.getTo(), entry.getKey())) && entry.getKey() != edge.getTo()) {
                    allEdgeBetweennesses.put(new Pair<>(entry.getKey(), edge.getTo()), 0);
                }
            }
        }

        for (Map.Entry<Pair<Node, Node>, List<Node>> entry : allShortestPaths.entrySet()) {
            if (entry.getValue() != null) {

                for (int i = 0; i < entry.getValue().size() - 1; i++) {
                    if (allEdgeBetweennesses.containsKey(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)))) {
                        Integer currentBetweeness = allEdgeBetweennesses.get(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)));
                        allEdgeBetweennesses.put(new Pair<>(entry.getValue().get(i), entry.getValue().get(i + 1)), currentBetweeness + 1);
                    } else if (allEdgeBetweennesses.containsKey(new Pair<>(entry.getValue().get(i + 1), entry.getValue().get(i)))) {
                        Integer currentBetweeness = allEdgeBetweennesses.get(new Pair<>(entry.getValue().get(i + 1), entry.getValue().get(i)));
                        allEdgeBetweennesses.put(new Pair<>(entry.getValue().get(i + 1), entry.getValue().get(i)), currentBetweeness + 1);
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
        List<Map.Entry<Pair<Node, Node>, Integer>> list = new ArrayList<>(originalMap.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        Map<Pair<Node, Node>, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<Pair<Node, Node>, Integer> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Delete an edge of adj for a given pair of node
     * @param pair
     */
    public void deleteEdge(Pair<Node, Node> pair) {
        if (this.adj.containsKey(pair.getFirst())) {
            this.adj.get(pair.getFirst()).removeIf(e -> e.getTo().getId().equals(pair.getSecond().getId()));
            this.M--;
        }
        if (this.adj.containsKey(pair.getSecond())) {
            this.adj.get(pair.getSecond()).removeIf(e -> e.getTo().getId().equals(pair.getFirst().getId()));
        }
    }

    /**
     * Identify and create clusters from the graph
     * @param maxClusters
     * @param useDijkstra
     * @return
     */
    public CopyOnWriteArrayList<List<Node>> makeCluster(Integer maxClusters, boolean useDijkstra) {
        CopyOnWriteArrayList<List<Node>> allClusters = new CopyOnWriteArrayList<>();
        List<Pair<Node, Node>> allRemovedEdges = new ArrayList<>();

        while (allClusters.size() < maxClusters && this.M > 1) {
            Graph currentGraph = this;
            Map<Pair<Node, Node>, Integer> edgeBTW = currentGraph.getAllEdgesBetweennesses(useDijkstra);
            List<Node> usedNodes = new ArrayList<>();
            allClusters = new CopyOnWriteArrayList<>();

            Pair<Node, Node> edgeToRemove = edgeBTW.keySet().iterator().next();
            allRemovedEdges.add(edgeToRemove);
            currentGraph.deleteEdge(edgeToRemove);

            for (Node start : currentGraph.adj.keySet()) {
                if (!usedNodes.contains(start)) {
                    for (Node target : currentGraph.adj.keySet()) {
                        if (!usedNodes.contains(target)) {
                            if (start != target) {

                                if (allClusters.size() < maxClusters) {
                                    Map<Pair<Node, Node>, List<Node>> allSPs = currentGraph.findAllShortestPaths(useDijkstra);
                                    Map<Pair<Node, Node>, List<Node>> allSPOfTargetNode = currentGraph.findAllShortestPathsFromStartNode(target, useDijkstra);

                                    Pair<Node, Node> ride = new Pair<>(start, target);
                                    if (allSPs.get(ride) == null) {             // If there is no SP between start and target
                                            if (!usedNodes.contains(target)) {
                                                List<Node> newCluster = new ArrayList<>();
                                                newCluster.add(target);
                                                usedNodes.add(target);
                                                for (Map.Entry<Pair<Node, Node>, List<Node>> entry : allSPOfTargetNode.entrySet()) {
                                                    if (entry.getValue() != null && !usedNodes.contains(entry.getKey().getSecond())) {
                                                        newCluster.add(entry.getKey().getSecond());
                                                        usedNodes.add(entry.getKey().getSecond());
                                                    }
                                                }
                                                allClusters.add(newCluster);
                                            }
                                    } else {                                    // If there is an SP between start and targetd
                                        if (!usedNodes.contains(start)) {
                                            usedNodes.add(start);
                                            List<Node> newCluster = new ArrayList<>();
                                            newCluster.add(start);
                                            Map<Pair<Node, Node>, List<Node>> allSPOfStart = currentGraph.findAllShortestPathsFromStartNode(start, useDijkstra);
                                            for (Map.Entry<Pair<Node, Node>, List<Node>> entry : allSPOfStart.entrySet()) {
                                                if (entry.getValue() != null && !usedNodes.contains(entry.getKey().getSecond())) {
                                                    newCluster.add(entry.getKey().getSecond());
                                                    usedNodes.add(entry.getKey().getSecond());
                                                }
                                            }
                                            allClusters.add(newCluster);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("We deleted " + allRemovedEdges.size() + " edges to create the clusters : ");
        for (Pair<Node, Node> removeEdge : allRemovedEdges) {
            System.out.print(removeEdge.getFirst().getId() + ":" + removeEdge.getSecond().getId() + " ");
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
}

