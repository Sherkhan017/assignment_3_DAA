package tests;

import graph.*;
import dsu.DSU;
import mst.MST;
import connectivity.Connectivity;
import shortestpath.Dijkstra;

import java.util.*;

public class GraphTests {
    static int passed = 0;

    private static void check(boolean correct, String message) {
        if (!correct) {
            throw new RuntimeException("FAILED: " + message);
        }
        passed++;
    }

    private static boolean validTopologicalOrder(Graph graph, ArrayList<Integer> order) {
        if (order.size() != graph.vertices) {
            return false;
        }
        int[] position = new int[graph.vertices];
        boolean[] seen = new boolean[graph.vertices];
        for (int i = 0; i < order.size(); i++) {
            int vertex = order.get(i);
            if (vertex < 0 || vertex >= graph.vertices || seen[vertex]) {
                return false;
            }
            seen[vertex] = true;
            position[vertex] = i;
        }
        for (Edge edge : graph.edges) {
            if (position[edge.from] >= position[edge.to]) {
                return false;
            }
        }
        return true;
    }

    public static void run() {
        passed = 0;
        DSU dsu = new DSU(5);
        check(dsu.union(0, 1), "DSU joins groups");
        check(dsu.union(1, 2), "DSU joins third vertex");
        check(dsu.find(0) == dsu.find(2), "DSU find and path compression");
        check(!dsu.union(0, 2), "DSU rejects cycle");
        check(dsu.find(3) != dsu.find(0), "DSU separate groups");
        check(dsu.findCalls > 0 && dsu.unionCalls == 3, "DSU counts operations");

        Graph small = new Graph(4, false);
        small.addEdge(0, 1, 1);
        small.addEdge(0, 2, 4);
        small.addEdge(1, 2, 2);
        small.addEdge(1, 3, 5);
        small.addEdge(2, 3, 3);
        MST.Result k = MST.kruskal(small);
        MST.Result p = MST.prim(small);
        check(k.connected, "Kruskal connected");
        check(p.connected, "Prim connected");
        check(k.weight == 6, "Kruskal MST weight");
        check(p.weight == 6, "Prim MST weight");
        check(k.weight == p.weight, "Both MST results match");
        check(k.usedEdges == 3 && p.usedEdges == 3, "MST contains V-1 edges");
        check(k.chosenRoads.size() == 3 && p.chosenRoads.size() == 3,
                "MST returns the selected roads");
        check(k.edgeChecks > 0 && p.edgeChecks > 0, "MST operation counts");

        Graph square = new Graph(4, false);
        square.addEdge(0, 1, 1);
        square.addEdge(1, 2, 1);
        square.addEdge(2, 3, 1);
        square.addEdge(3, 0, 1);
        check(MST.kruskal(square).weight == 3, "Multiple valid MSTs (Kruskal)");
        check(MST.prim(square).weight == 3, "Multiple valid MSTs (Prim)");

        Graph disconnected = new Graph(4, false);
        disconnected.addEdge(0, 1, 3);
        disconnected.addEdge(2, 3, 4);
        check(!MST.kruskal(disconnected).connected, "Kruskal disconnected graph");
        check(!MST.prim(disconnected).connected, "Prim disconnected graph");
        check(MST.kruskal(disconnected).weight == 7, "Kruskal forest cost");
        check(MST.prim(disconnected).weight == 7, "Prim forest cost");

        Graph equalWeights = new Graph(3, false);
        equalWeights.addEdge(0, 1, 5);
        equalWeights.addEdge(1, 2, 5);
        equalWeights.addEdge(0, 2, 5);
        check(MST.kruskal(equalWeights).weight == 10, "Duplicate edge weights (Kruskal)");
        check(MST.prim(equalWeights).weight == 10, "Duplicate edge weights (Prim)");

        Graph dag = new Graph(5, true);
        dag.addEdge(0, 2, 1);
        dag.addEdge(1, 2, 1);
        dag.addEdge(2, 3, 1);
        dag.addEdge(2, 4, 1);
        Connectivity.TopoResult topo = Connectivity.topologicalSort(dag);
        check(!topo.hasCycle, "DAG has no cycle");
        check(validTopologicalOrder(dag, topo.order), "Correct topological order");
        check(topo.edgeChecks > 0, "Topological operation counts");

        Graph cyclic = new Graph(5, true);
        cyclic.addEdge(0, 1, 1);
        cyclic.addEdge(1, 2, 1);
        cyclic.addEdge(2, 0, 1);
        cyclic.addEdge(2, 3, 1);
        cyclic.addEdge(3, 4, 1);
        cyclic.addEdge(4, 3, 1);
        check(Connectivity.topologicalSort(cyclic).hasCycle, "Cycle is detected");
        Connectivity.SCCResult scc = Connectivity.kosaraju(cyclic);
        check(scc.components.size() == 2, "Two SCCs found");
        boolean[] inComponent = new boolean[5];
        boolean exactlyOnce = true;
        for (ArrayList<Integer> group : scc.components) {
            for (int v : group) {
                if (inComponent[v]) {
                    exactlyOnce = false;
                }
                inComponent[v] = true;
            }
        }
        for (boolean seen : inComponent) {
            if (!seen) {
                exactlyOnce = false;
            }
        }
        check(exactlyOnce, "Every vertex belongs to exactly one SCC");
        check(scc.edgeChecks > 0, "SCC operation counts");

        Graph routes = new Graph(4, true);
        routes.addEdge(0, 1, 4);
        routes.addEdge(0, 2, 1);
        routes.addEdge(2, 1, 2);
        routes.addEdge(1, 3, 1);
        routes.addEdge(2, 3, 10);
        Dijkstra.Result paths = Dijkstra.shortestPaths(routes, 0);
        check(paths.distance[0] == 0, "Dijkstra source distance");
        check(paths.distance[1] == 3, "Dijkstra shortest distance to 1");
        check(paths.distance[2] == 1, "Dijkstra shortest distance to 2");
        check(paths.distance[3] == 4, "Dijkstra shortest distance to 3");
        check(paths.getPath(3).equals(new ArrayList<>(Arrays.asList(0, 2, 1, 3))),
                "Dijkstra reconstructs the full path");
        check(paths.edgeChecks > 0, "Dijkstra operation counts");

        Graph unreachable = new Graph(4, true);
        unreachable.addEdge(0, 1, 4);
        unreachable.addEdge(1, 2, 3);
        Dijkstra.Result unreach = Dijkstra.shortestPaths(unreachable, 0);
        check(unreach.distance[3] == Dijkstra.INF, "Unreachable distance");
        check(unreach.getPath(3).isEmpty(), "Unreachable path is empty");
        check(unreach.distance[2] == 7, "Reachable vertex still works");

        Graph negative = new Graph(2, true);
        negative.addEdge(0, 1, -5);
        boolean negativeRejected = false;
        try {
            Dijkstra.shortestPaths(negative, 0);
        } catch (IllegalArgumentException e) {
            negativeRejected = true;
        }
        check(negativeRejected, "Negative edges are rejected");

        Graph single = new Graph(1, false);
        check(MST.kruskal(single).connected, "Single vertex Kruskal connected");
        check(MST.prim(single).connected, "Single vertex Prim connected");
        check(MST.kruskal(single).weight == 0, "Single vertex MST zero");
        check(Dijkstra.shortestPaths(single, 0).distance[0] == 0,
                "Single vertex Dijkstra zero");
        check(Dijkstra.shortestPaths(single, 0).getPath(0).equals(
                new ArrayList<>(Arrays.asList(0))), "Single vertex path");
        Graph singleDirected = new Graph(1, true);
        check(validTopologicalOrder(singleDirected,
                Connectivity.topologicalSort(singleDirected).order), "Single vertex topo order");
        check(Connectivity.kosaraju(singleDirected).components.size() == 1,
                "Single vertex SCC");

        Graph generated1 = GraphGenerator.connectedUndirected(10, 30, 42);
        Graph generated2 = GraphGenerator.connectedUndirected(10, 30, 42);
        boolean sameGraphs = generated1.edges.size() == generated2.edges.size();
        for (int i = 0; i < generated1.edges.size(); i++) {
            Edge a = generated1.edges.get(i);
            Edge b = generated2.edges.get(i);
            if (a.from != b.from || a.to != b.to || a.weight != b.weight) {
                sameGraphs = false;
            }
        }
        check(sameGraphs, "Generator fixed seed is reproducible");
        check(MST.kruskal(generated1).connected, "Generated MST graph connected");
        check(MST.kruskal(generated1).weight == MST.prim(generated1).weight,
                "Generated graph MST weights match");
        Graph generatedDag = GraphGenerator.dag(10, 30, 43);
        check(validTopologicalOrder(generatedDag,
                Connectivity.topologicalSort(generatedDag).order), "Generated DAG has valid order");
        Graph generatedCycle = GraphGenerator.directedWithCycle(10, 30, 44);
        check(Connectivity.topologicalSort(generatedCycle).hasCycle,
                "Generated cyclic graph detected");

        System.out.println("All " + passed + " tests passed!");
    }

    public static void main(String[] args) {
        run();
    }
}
