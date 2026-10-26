package benchmark;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;

public class Plots {
    static String[] names = {"Kruskal", "Prim", "Kosaraju SCC",
            "Topological DAG", "Topological Cycle", "Dijkstra"};

    static Color[] colors = {Color.BLUE, Color.RED, Color.GREEN.darker(),
            Color.ORANGE.darker(), Color.MAGENTA, Color.CYAN.darker()};


    public static void save(ArrayList<Benchmark.Row> rows) throws IOException {
        System.setProperty("java.awt.headless", "true");
        draw(rows, true, "results/plots/time_vs_vertices.png");
        draw(rows, false, "results/plots/time_vs_edges.png");
    }

    private static void draw(ArrayList<Benchmark.Row> rows, boolean byVertices,
                             String fileName) throws IOException {
        int width = 1000;
        int height = 560;
        int left = 85;
        int right = 240;
        int top = 80;
        int bottom = 80;
        int graphWidth = width - left - right;
        int graphHeight = height - top - bottom;

        int[] sizes = byVertices ? new int[]{10, 100, 1000} : new int[]{30, 500, 5000};
        double maxTime = 0;
        for (Benchmark.Row row : rows) {
            if (row.timeMs > maxTime) {
                maxTime = row.timeMs;
            }
        }
        maxTime = Math.max(maxTime * 1.15, 0.01);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));

        String horizontal = byVertices ? "Number of vertices" : "Number of edges";
        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 19));
        g.drawString("Execution time vs " + horizontal.toLowerCase(), left, 36);
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.drawString("Average time (ms), 5 runs", left, 59);


        for (int i = 0; i <= 5; i++) {
            int y = top + graphHeight - i * graphHeight / 5;
            g.setColor(new Color(225, 225, 225));
            g.drawLine(left, y, left + graphWidth, y);
            g.setColor(Color.DARK_GRAY);
            g.drawString(String.format(Locale.US, "%.3f", maxTime * i / 5), 15, y + 5);
        }


        int[] x = new int[3];
        double first = Math.log10(sizes[0]);
        double last = Math.log10(sizes[2]);
        for (int i = 0; i < sizes.length; i++) {
            x[i] = left + (int) ((Math.log10(sizes[i]) - first)
                    / (last - first) * graphWidth);
            g.setColor(Color.LIGHT_GRAY);
            g.drawLine(x[i], top, x[i], top + graphHeight);
            g.setColor(Color.BLACK);
            g.drawString("" + sizes[i], x[i] - 13, top + graphHeight + 23);
        }
        g.setColor(Color.BLACK);
        g.drawLine(left, top, left, top + graphHeight);
        g.drawLine(left, top + graphHeight, left + graphWidth, top + graphHeight);
        g.drawString(horizontal + " (log scale)", left + graphWidth / 2 - 80,
                height - 24);

        for (int algorithm = 0; algorithm < names.length; algorithm++) {
            int[] y = new int[3];
            for (int i = 0; i < sizes.length; i++) {
                double time = 0;
                for (Benchmark.Row row : rows) {
                    int size = byVertices ? row.vertices : row.edges;
                    if (row.algorithm.equals(names[algorithm]) && size == sizes[i]) {
                        time = row.timeMs;
                        break;
                    }
                }
                y[i] = top + graphHeight - (int) (time / maxTime * graphHeight);
            }

            g.setColor(colors[algorithm]);
            g.setStroke(new BasicStroke(2));
            for (int i = 0; i < 2; i++) {
                g.drawLine(x[i], y[i], x[i + 1], y[i + 1]);
            }
            for (int i = 0; i < 3; i++) {
                g.fillOval(x[i] - 4, y[i] - 4, 8, 8);
            }
            int legendY = top + 35 + algorithm * 38;
            g.drawLine(width - right + 25, legendY, width - right + 55, legendY);
            g.drawString(names[algorithm], width - right + 68, legendY + 5);
        }
        g.dispose();

        File file = new File(fileName);
        file.getParentFile().mkdirs();
        ImageIO.write(image, "png", file);
    }
}
