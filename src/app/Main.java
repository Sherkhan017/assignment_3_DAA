package app;

import graph.*;
import mst.MST;
import connectivity.Connectivity;
import shortestpath.Dijkstra;
import tests.GraphTests;
import benchmark.Benchmark;

public class Main {
    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        System.out.println("Assignment 3 ");

        Graph roads = new Graph(5, false);
        roads.addEdge(0, 1, 3);
        roads.addEdge(0, 2, 2);
        roads.addEdge(1, 2, 1);
        roads.addEdge(1, 3, 5);
        roads.addEdge(2, 3, 8);
        roads.addEdge(3, 4, 3);
        roads.addEdge(2, 4, 10);

        MST.Result kruskal = MST.kruskal(roads);
        MST.Result prim = MST.prim(roads);
        System.out.println("Kruskal cost: " + kruskal.weight);
        System.out.println("Prim cost: " + prim.weight);
        System.out.println("Graph connected: " + kruskal.connected);
        System.out.println("Kruskal selected roads:");
        for (Edge road : kruskal.chosenRoads) {
            System.out.println("Road " + road.from + " - " + road.to
                    + ", cost " + road.weight);
        }

        Dijkstra.Result paths = Dijkstra.shortestPaths(roads, 0);
        for (int i = 0; i < roads.vertices; i++) {
            System.out.println("District " + i + ": distance = "
                    + paths.distance[i] + ", path = " + paths.getPath(i));
        }

        Graph services = new Graph(5, true);
        services.addEdge(0, 2, 1);
        services.addEdge(1, 2, 1);
        services.addEdge(2, 3, 1);
        services.addEdge(3, 4, 1);
        Connectivity.TopoResult topo = Connectivity.topologicalSort(services);
        System.out.println("Topological order: " + topo.order);

        Graph oneWayRoads = new Graph(5, true);
        oneWayRoads.addEdge(0, 1, 1);
        oneWayRoads.addEdge(1, 2, 1);
        oneWayRoads.addEdge(2, 0, 1);
        oneWayRoads.addEdge(2, 3, 1);
        oneWayRoads.addEdge(3, 4, 1);
        oneWayRoads.addEdge(4, 3, 1);
        System.out.println("SCC groups: " + Connectivity.kosaraju(oneWayRoads).components);
        System.out.println("Directed graph has cycle: "
                + Connectivity.topologicalSort(oneWayRoads).hasCycle);

        System.out.println("\n Tests");
        GraphTests.run();

        System.out.println("\nBenchmark");
        Benchmark.run();
        System.out.println("Tables and plots are saved in the results folder.");
    }
}
