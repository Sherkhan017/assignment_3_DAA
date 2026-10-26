package connectivity;

import graph.*;

import java.util.*;

public class Connectivity {
    public static class TopoResult {
        public ArrayList<Integer> order = new ArrayList<>();
        public boolean hasCycle;
        public long edgeChecks;
        public long otherOperations;
    }

    public static class SCCResult {
        public ArrayList<ArrayList<Integer>> components = new ArrayList<>();
        public long edgeChecks;
        public long otherOperations;
    }

    public static TopoResult topologicalSort(Graph graph) {
        if (!graph.directed) {
            throw new IllegalArgumentException("Topological sort needs a directed graph");
        }

        TopoResult result = new TopoResult();
        int[] inDegree = new int[graph.vertices];
        for (Edge edge : graph.edges) {
            inDegree[edge.to]++;
            result.edgeChecks++;
        }

        Queue<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < graph.vertices; i++) {
            if (inDegree[i] == 0) {
                queue.add(i);
                result.otherOperations++;
            }
        }

        while (!queue.isEmpty()) {
            int vertex = queue.poll();
            result.otherOperations++;
            result.order.add(vertex);

            for (Edge edge : graph.adj.get(vertex)) {
                result.edgeChecks++;
                inDegree[edge.to]--;
                if (inDegree[edge.to] == 0) {
                    queue.add(edge.to);
                    result.otherOperations++;
                }
            }
        }

        result.hasCycle = result.order.size() != graph.vertices;
        return result;
    }

    public static SCCResult kosaraju(Graph graph) {
        if (!graph.directed) {
            throw new IllegalArgumentException("SCC needs a directed graph");
        }

        SCCResult result = new SCCResult();
        boolean[] visited = new boolean[graph.vertices];
        ArrayList<Integer> finishOrder = new ArrayList<>();

        for (int i = 0; i < graph.vertices; i++) {
            if (!visited[i]) {
                dfs1(i, graph, visited, finishOrder, result);
            }
        }

        ArrayList<ArrayList<Integer>> reverse = new ArrayList<>();
        for (int i = 0; i < graph.vertices; i++) {
            reverse.add(new ArrayList<>());
        }
        for (Edge edge : graph.edges) {
            reverse.get(edge.to).add(edge.from);
            result.edgeChecks++;
        }

        Arrays.fill(visited, false);
        for (int i = finishOrder.size() - 1; i >= 0; i--) {
            int vertex = finishOrder.get(i);
            if (!visited[vertex]) {
                ArrayList<Integer> group = new ArrayList<>();
                dfs2(vertex, reverse, visited, group, result);
                result.components.add(group);
            }
        }
        return result;
    }

    private static void dfs1(int vertex, Graph graph, boolean[] visited,
                             ArrayList<Integer> finishOrder, SCCResult result) {
        visited[vertex] = true;
        result.otherOperations++;
        for (Edge edge : graph.adj.get(vertex)) {
            result.edgeChecks++;
            if (!visited[edge.to]) {
                dfs1(edge.to, graph, visited, finishOrder, result);
            }
        }
        finishOrder.add(vertex);
    }

    private static void dfs2(int vertex, ArrayList<ArrayList<Integer>> reverse,
                             boolean[] visited, ArrayList<Integer> group, SCCResult result) {
        visited[vertex] = true;
        result.otherOperations++;
        group.add(vertex);
        for (int next : reverse.get(vertex)) {
            result.edgeChecks++;
            if (!visited[next]) {
                dfs2(next, reverse, visited, group, result);
            }
        }
    }
}
