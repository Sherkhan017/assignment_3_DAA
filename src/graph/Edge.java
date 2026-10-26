package graph;

public class Edge implements Comparable<Edge> {
    public int from;
    public int to;
    public int weight;

    public Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public int compareTo(Edge other) {
        if (weight != other.weight) {
            return Integer.compare(weight, other.weight);
        }
        if (from != other.from) {
            return Integer.compare(from, other.from);
        }
        return Integer.compare(to, other.to);
    }
}
