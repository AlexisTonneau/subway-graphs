package com.graph.lisbon.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    private Node to; //Edge's destination
    private double weight;
}
