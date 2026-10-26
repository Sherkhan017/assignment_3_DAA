package benchmark;

import graph.*;
import mst.MST;
import connectivity.Connectivity;
import shortestpath.Dijkstra;

import java.util.*;
import java.io.*;

public class Benchmark {
    static class Row {
        String size;
        int vertices;
        int edges;
        String algorithm;
        double timeMs;
        long edgeChecks;
        long otherOperations;

        Row(String size, int vertices, int edges, String algorithm,
            double timeMs, long edgeChecks, long otherOperations) {
            this.size = size;
            this.vertices = vertices;
            this.edges = edges;
            this.algorithm = algorithm;
            this.timeMs = timeMs;
            this.edgeChecks = edgeChecks;
            this.otherOperations = otherOperations;
        }
    }

    private static void addRow(ArrayList<Row> rows, String size, int vertices,
                               int edges, String name, double[] times,
                               long[] checks, long[] extra, int i) {
        rows.add(new Row(size, vertices, edges, name,
                times[i] / 5.0 / 1_000_000.0, checks[i] / 5, extra[i] / 5));
    }

    public static void run() throws IOException {
        System.setProperty("java.awt.headless", "true");
        int[] vertexSizes = {10, 100, 1000};
        int[] edgeSizes = {30, 500, 5000};
        String[] sizeNames = {"Small", "Medium", "Large"};
        String[] names = {"Kruskal", "Prim", "Kosaraju SCC",
                "Topological DAG", "Topological Cycle", "Dijkstra"};
        ArrayList<Row> rows = new ArrayList<>();

        for (int size = 0; size < vertexSizes.length; size++) {
            int v = vertexSizes[size];
            int e = edgeSizes[size];
            Graph roads = GraphGenerator.connectedUndirected(v, e, 42);
            Graph dag = GraphGenerator.dag(v, e, 43);
            Graph cyclic = GraphGenerator.directedWithCycle(v, e, 44);

            double[] times = new double[6];
            long[] checks = new long[6];
            long[] extra = new long[6];

            for (int round = -1; round < 5; round++) {
                long start = System.nanoTime();
                MST.Result kruskal = MST.kruskal(roads);
                long elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[0] += elapsed;
                    checks[0] += kruskal.edgeChecks;
                    extra[0] += kruskal.otherOperations;
                }

                start = System.nanoTime();
                MST.Result prim = MST.prim(roads);
                elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[1] += elapsed;
                    checks[1] += prim.edgeChecks;
                    extra[1] += prim.otherOperations;
                }

                start = System.nanoTime();
                Connectivity.SCCResult scc = Connectivity.kosaraju(cyclic);
                elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[2] += elapsed;
                    checks[2] += scc.edgeChecks;
                    extra[2] += scc.otherOperations;
                }

                start = System.nanoTime();
                Connectivity.TopoResult topoDag = Connectivity.topologicalSort(dag);
                elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[3] += elapsed;
                    checks[3] += topoDag.edgeChecks;
                    extra[3] += topoDag.otherOperations;
                }

                start = System.nanoTime();
                Connectivity.TopoResult topoCycle = Connectivity.topologicalSort(cyclic);
                elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[4] += elapsed;
                    checks[4] += topoCycle.edgeChecks;
                    extra[4] += topoCycle.otherOperations;
                }

                start = System.nanoTime();
                Dijkstra.Result paths = Dijkstra.shortestPaths(roads, 0);
                elapsed = System.nanoTime() - start;
                if (round >= 0) {
                    times[5] += elapsed;
                    checks[5] += paths.edgeChecks;
                    extra[5] += paths.otherOperations;
                }

                if (!kruskal.connected || !prim.connected || kruskal.weight != prim.weight) {
                    throw new RuntimeException("MST comparison failed");
                }
                if (topoDag.hasCycle || topoDag.order.size() != v || !topoCycle.hasCycle) {
                    throw new RuntimeException("Topological sorting failed");
                }
                int verticesInSCC = 0;
                for (ArrayList<Integer> component : scc.components) {
                    verticesInSCC += component.size();
                }
                if (verticesInSCC != v || paths.distance[v - 1] == Dijkstra.INF) {
                    throw new RuntimeException("SCC or Dijkstra failed");
                }
            }

            for (int i = 0; i < names.length; i++) {
                addRow(rows, sizeNames[size], v, e, names[i], times, checks, extra, i);
            }
            long kruskalCost = MST.kruskal(roads).weight;
            long primCost = MST.prim(roads).weight;
            System.out.println(sizeNames[size] + " graph: " + v + " vertices, "
                    + e + " edges completed. MST: " + kruskalCost
                    + " (Kruskal) = " + primCost + " (Prim)");
        }

        saveResults(rows);
        Plots.save(rows);
        System.out.println("Results saved in results/tables and results/plots");
    }

    private static void saveResults(ArrayList<Row> rows) throws IOException {
        File folder = new File("results/tables");
        folder.mkdirs();

        PrintWriter csv = new PrintWriter(new FileWriter("results/tables/benchmark.csv"));
        csv.println("size,vertices,edges,algorithm,avg_time_ms,edge_checks,other_operations");
        for (Row row : rows) {
            csv.printf(Locale.US, "%s,%d,%d,%s,%.6f,%d,%d%n",
                    row.size, row.vertices, row.edges, row.algorithm,
                    row.timeMs, row.edgeChecks, row.otherOperations);
        }
        csv.close();

        PrintWriter table = new PrintWriter(new FileWriter("results/tables/benchmark.md"));
        table.println("| Size | V | E | Algorithm | Average time (ms) | Edge checks | Other ops | ");
        table.println("|---|---:|---:|---|---:|---:|---:|");
        for (Row row : rows) {
            table.printf(Locale.US, "| %s | %d | %d | %s | %.4f | %d | %d |%n",
                    row.size, row.vertices, row.edges, row.algorithm,
                    row.timeMs, row.edgeChecks, row.otherOperations);
        }
        table.close();
    }

    public static void main(String[] args) throws IOException {
        run();
    }
}
