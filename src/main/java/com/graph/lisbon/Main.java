package com.graph.lisbon;

import com.graph.lisbon.entities.Graph;
import com.graph.lisbon.entities.Node;
import com.graph.lisbon.utils.BFSShortestPaths;
import com.graph.lisbon.utils.Dijkstra;
import com.graph.lisbon.utils.GtfsParser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) {
        // Load resources
        GtfsParser gtfsParser = new GtfsParser("src/main/resources/");

        // Unweighted graph
        Graph graph = gtfsParser.gtfsToGraph(false);
        System.out.println("\n========== UNWEIGHTED GRAPH ==========\n\n" +
                "The graph :\n" +
                graph.toString() + "\n");

        // BFS shortest path
        BFSShortestPaths bfsSPs = new BFSShortestPaths();
        Node start = graph.findNodeById("M03");
        Node target = graph.findNodeById("M27");
        bfsSPs.bfs(graph, start);
        System.out.println("========== BFS SHORTEST PATH ==========\n\n" +
                "The BFS of the unweighted graph allows us to find the shortest path between " + start.getId() + " and " + target.getId() + ":\n");
        bfsSPs.printShortestPath(bfsSPs.getShortestPath(target));
        System.out.println("\n");

        // Weighted graph
        Graph weightedGraph = gtfsParser.gtfsToGraph(true);
        System.out.println("========== WEIGHTED GRAPH ==========\n\n" +
                "The weighted graph :\n" +
                weightedGraph.toString() + "\n");

        // Dijkstra shortest path
        Dijkstra dijkstra = new Dijkstra();
        Node weightedStart = weightedGraph.findNodeById("M13");
        Node weightedTarget = weightedGraph.findNodeById("M21");
        dijkstra.dikstraSP(weightedGraph, weightedStart);
        System.out.println("========== DIJKSTRA SHORTEST PATH ==========\n\n" +
                "The Dijkstra of the weighted graph allows us to find the shortest path between " + weightedStart.getId() + " and " + weightedTarget.getId() + ":\n");
        dijkstra.printShortestPath(dijkstra.getShortestPath(weightedTarget));
        System.out.println("\nThe distance between " + weightedStart.getId() + " and " + weightedTarget.getId() + " is " + dijkstra.distTo(weightedTarget));
        System.out.println("\n");

        // Clustering
        int mawClusters = 3;
        System.out.println("========== CLUSTERING ==========\n\n" +
                "Number of clusters: " + mawClusters + "\n");
        List<List<Node>> clusters = graph.makeCluster(mawClusters, false);
        for (int i = 0; i < clusters.size(); i++){
            System.out.print("CLUSTER N°" + (i + 1) + " : ");
            for (Node node : clusters.get(i)) {
                System.out.print(node.getId() + " ");
            }
            System.out.println("\nSize = " + clusters.get(i).size());
        }
    }
}
