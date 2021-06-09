package com.graph.lisbon.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
    private String id;      //Id of the node
    private double lat;     //Latitude of the node
    private double lon;     //Longitude of the node
}
