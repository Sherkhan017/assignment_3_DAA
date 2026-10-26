package graph;

import java.util.*;

public class Graph {
    public int vertices;
    public boolean directed;
    public ArrayList<Edge> edges;
    public ArrayList<ArrayList<Edge>> adj;

    public Graph(int vertices, boolean directed) {
        if (vertices < 1) {
            throw new IllegalArgumentException("Graph needs at least one vertex");
        }

        this.vertices = vertices;
        this.directed = directed;
        edges = new ArrayList<>();
        adj = new ArrayList<>();

        for (int i = 0; i < vertices; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= vertices || to < 0 || to >= vertices) {
            throw new IllegalArgumentException("Wrong vertex number");
        }

        Edge edge = new Edge(from, to, weight);
        edges.add(edge);
        adj.get(from).add(edge);

        if (!directed) {
            adj.get(to).add(new Edge(to, from, weight));
        }
    }
}
