package graph;

import java.util.*;

public class GraphGenerator {
    public static Graph connectedUndirected(int vertices, int edgeCount, long seed) {
        long maxEdges = (long) vertices * (vertices - 1) / 2;
        if (edgeCount < vertices - 1 || edgeCount > maxEdges) {
            throw new IllegalArgumentException("Wrong number of undirected edges");
        }

        Random random = new Random(seed);
        Graph graph = new Graph(vertices, false);
        HashSet<Long> used = new HashSet<>();

        for (int i = 1; i < vertices; i++) {
            int j = random.nextInt(i);
            graph.addEdge(i, j, random.nextInt(100) + 1);
            int a = Math.min(i, j);
            int b = Math.max(i, j);
            used.add((long) a * vertices + b);
        }

        while (graph.edges.size() < edgeCount) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a == b) {
                continue;
            }
            int first = Math.min(a, b);
            int second = Math.max(a, b);
            long key = (long) first * vertices + second;
            if (used.add(key)) {
                graph.addEdge(a, b, random.nextInt(100) + 1);
            }
        }
        return graph;
    }

    public static Graph dag(int vertices, int edgeCount, long seed) {
        long maxEdges = (long) vertices * (vertices - 1) / 2;
        if (edgeCount < 0 || edgeCount > maxEdges) {
            throw new IllegalArgumentException("Wrong number of DAG edges");
        }

        Random random = new Random(seed);
        Graph graph = new Graph(vertices, true);
        HashSet<Long> used = new HashSet<>();

        while (graph.edges.size() < edgeCount) {
            int a = random.nextInt(vertices);
            int b = random.nextInt(vertices);
            if (a == b) {
                continue;
            }
            int from = Math.min(a, b);
            int to = Math.max(a, b);
            long key = (long) from * vertices + to;
            if (used.add(key)) {
                graph.addEdge(from, to, 1);
            }
        }
        return graph;
    }

    public static Graph directedWithCycle(int vertices, int edgeCount, long seed) {
        long maxEdges = (long) vertices * (vertices - 1);
        if (vertices < 3 || edgeCount < 3 || edgeCount > maxEdges) {
            throw new IllegalArgumentException("Cyclic graph needs at least 3 vertices and 3 edges");
        }

        Random random = new Random(seed);
        Graph graph = new Graph(vertices, true);
        HashSet<Long> used = new HashSet<>();

        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        used.add(1L);
        used.add((long) vertices + 2);
        used.add(2L * vertices);

        while (graph.edges.size() < edgeCount) {
            int from = random.nextInt(vertices);
            int to = random.nextInt(vertices);
            if (from == to) {
                continue;
            }
            long key = (long) from * vertices + to;
            if (used.add(key)) {
                graph.addEdge(from, to, 1);
            }
        }
        return graph;
    }
}
