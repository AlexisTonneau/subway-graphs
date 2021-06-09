package com.graph.lisbon.utils;

import com.graph.lisbon.entities.Graph;
import com.graph.lisbon.entities.Node;
import lombok.AllArgsConstructor;

import java.io.*;
import java.util.Scanner;

import static java.lang.Integer.parseInt;

@AllArgsConstructor
public class GtfsParser {
    private String path;

    /**
     * Creates a graph filled with information taken from our GTFS files
     * @param isWeighted
     * @return
     */
    public Graph gtfsToGraph(boolean isWeighted) {
        Graph graph = new Graph();
        gtfsStopsToNodes(graph);
        gtfsNetworkToEdges(graph, isWeighted);
        return graph;
    }

    /**
     * Add to the graph its nodes with the stops.txt file
     * @param graph
     */
    private void gtfsStopsToNodes(Graph graph) {
        int INDEX_STOP_ID = 0;
        int INDEX_STOP_LAT = 4;
        int INDEX_STOP_LON = 5;
        try {
            File file = new File(this.path + "stops.txt");
            Scanner myReader = new Scanner(file);
            myReader.nextLine(); // Skip column names
            while (myReader.hasNextLine()) {
                String[] splitLine = myReader.nextLine().split(",");
                Node newNode = Node.builder()
                    .id(splitLine[INDEX_STOP_ID])
                    .lat(Double.parseDouble(splitLine[INDEX_STOP_LAT]))
                    .lon(Double.parseDouble(splitLine[INDEX_STOP_LON]))
                    .build();
                graph.addNode(newNode);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Add to the graph its edges with the network.txt file
     * @param graph
     * @param isWeighted
     */
    private void gtfsNetworkToEdges(Graph graph, boolean isWeighted) {
        int INDEX_TRIP_ID = 0;
        int INDEX_STOP_ID = 3;
        int INDEX_STOP_SEQUENCE = 4;

        double weight = 0;
        try {
            File file = new File(this.path + "stop_times.txt");
            Scanner myReader = new Scanner(file);
            myReader.nextLine(); // Skip column names
            String[] previousLine = new String[0];
            if (myReader.hasNextLine())
                previousLine = myReader.nextLine().split(",");
            while (myReader.hasNextLine()) {
                String[] currentLine = myReader.nextLine().split(",");
                if (previousLine[INDEX_TRIP_ID].equals(currentLine[INDEX_TRIP_ID]) &&   //Check if it's same trip and the stop follows the previous one
                    parseInt(previousLine[INDEX_STOP_SEQUENCE]) + 1 == (parseInt(currentLine[INDEX_STOP_SEQUENCE]))) {
                    Node from = graph.findNodeById(previousLine[INDEX_STOP_ID]);
                    Node to = graph.findNodeById(currentLine[INDEX_STOP_ID]);
                    if (isWeighted) {
                        weight = weightCalculation(from, to);
                    }
                    graph.addEdge(from, to, weight);
                    graph.addEdge(to, from, weight);
                    graph.setM(graph.getM() + 1);
                }
                previousLine = currentLine;
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    /**
     * Return the weight of the edge between the two input nodes
     * @param from
     * @param to
     * @return
     */
    private double weightCalculation(Node from, Node to) {
        double dx = to.getLon() - from.getLon();
        double dy = to.getLat() - from.getLat();
        return Math.sqrt((Math.pow(dx, 2)) + (Math.pow(dy, 2)));
    }
}
