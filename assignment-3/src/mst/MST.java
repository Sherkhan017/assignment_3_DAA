package mst;

import graph.*;
import dsu.DSU;

import java.util.*;

public class MST {
    public static class Result {
        public long weight;
        public int usedEdges;
        public ArrayList<Edge> chosenRoads = new ArrayList<>();
        public boolean connected;
        public long edgeChecks;
        public long otherOperations;
    }

    public static Result kruskal(Graph graph) {
        if (graph.directed) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }

        Result result = new Result();
        DSU dsu = new DSU(graph.vertices);
        ArrayList<Edge> sorted = new ArrayList<>(graph.edges);
        Collections.sort(sorted);

        for (Edge edge : sorted) {
            result.edgeChecks++;
            if (dsu.union(edge.from, edge.to)) {
                result.weight += edge.weight;
                result.usedEdges++;
                result.chosenRoads.add(edge);
            }
            if (result.usedEdges == graph.vertices - 1) {
                break;
            }
        }

        result.connected = result.usedEdges == graph.vertices - 1;
        result.otherOperations = dsu.findCalls + dsu.unionCalls;
        return result;
    }

    public static Result prim(Graph graph) {
        if (graph.directed) {
            throw new IllegalArgumentException("MST needs an undirected graph");
        }

        Result result = new Result();
        boolean[] visited = new boolean[graph.vertices];
        PriorityQueue<Edge> queue = new PriorityQueue<>();

        for (int start = 0; start < graph.vertices; start++) {
            if (visited[start]) {
                continue;
            }

            visited[start] = true;
            for (Edge edge : graph.adj.get(start)) {
                if (!visited[edge.to]) {
                    queue.add(edge);
                    result.otherOperations++;
                }
            }

            while (!queue.isEmpty()) {
                Edge edge = queue.poll();
                result.otherOperations++;
                result.edgeChecks++;

                if (visited[edge.to]) {
                    continue;
                }

                visited[edge.to] = true;
                result.weight += edge.weight;
                result.usedEdges++;
                result.chosenRoads.add(edge);

                for (Edge next : graph.adj.get(edge.to)) {
                    if (!visited[next.to]) {
                        queue.add(next);
                        result.otherOperations++;
                    }
                }
            }
        }

        result.connected = result.usedEdges == graph.vertices - 1;
        return result;
    }
}
