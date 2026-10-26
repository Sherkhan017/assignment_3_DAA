package shortestpath;

import graph.*;

import java.util.*;

public class Dijkstra {
    public static final long INF = Long.MAX_VALUE / 4;

    static class Node implements Comparable<Node> {
        int vertex;
        long distance;

        Node(int vertex, long distance) {
            this.vertex = vertex;
            this.distance = distance;
        }

        public int compareTo(Node other) {
            return Long.compare(distance, other.distance);
        }
    }

    public static class Result {
        public long[] distance;
        public int[] parent;
        public long edgeChecks;
        public long otherOperations;

        Result(int n) {
            distance = new long[n];
            parent = new int[n];
            Arrays.fill(distance, INF);
            Arrays.fill(parent, -1);
        }

        public ArrayList<Integer> getPath(int target) {
            ArrayList<Integer> path = new ArrayList<>();
            if (distance[target] == INF) {
                return path;
            }
            for (int v = target; v != -1; v = parent[v]) {
                path.add(v);
            }
            Collections.reverse(path);
            return path;
        }
    }

    public static Result shortestPaths(Graph graph, int source) {
        if (source < 0 || source >= graph.vertices) {
            throw new IllegalArgumentException("Wrong start vertex");
        }
        for (Edge edge : graph.edges) {
            if (edge.weight < 0) {
                throw new IllegalArgumentException("Dijkstra cannot use negative weights");
            }
        }

        Result result = new Result(graph.vertices);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        result.distance[source] = 0;
        queue.add(new Node(source, 0));
        result.otherOperations++;

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            result.otherOperations++;
            if (current.distance != result.distance[current.vertex]) {
                continue;
            }

            for (Edge edge : graph.adj.get(current.vertex)) {
                result.edgeChecks++;
                long newDistance = current.distance + edge.weight;
                if (newDistance < result.distance[edge.to]) {
                    result.distance[edge.to] = newDistance;
                    result.parent[edge.to] = current.vertex;
                    queue.add(new Node(edge.to, newDistance));
                    result.otherOperations++;
                }
            }
        }
        return result;
    }
}
