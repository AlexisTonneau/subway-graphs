package com.graph.lisbon.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Edge {
    private Node to; //Node where the edge go
    private double weight; //weight of the edge
}
