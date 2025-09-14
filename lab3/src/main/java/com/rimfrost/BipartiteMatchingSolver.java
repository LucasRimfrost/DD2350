package com.rimfrost;
import java.util.ArrayList;
import java.util.List;

public class BipartiteMatchingSolver {
    private Kattio io;
    private int xNodes, yNodes;
    private int source, sink, totalNodes;
        private List<long[]> matching = new ArrayList<>();

    void readBipartiteGraphAndSolve() {
        xNodes = io.getInt();
        yNodes = io.getInt();
        int edges = io.getInt();

        totalNodes = xNodes + yNodes + 2;
        source = xNodes + yNodes + 1;
        sink = xNodes + yNodes + 2;

        // Create solver immediately - no need to store edges separately
        EdmondsKarpMaxFlow solver = new EdmondsKarpMaxFlow(totalNodes);

        // Build the flow network: source connects to all X nodes
        for (int i = 1; i <= xNodes; i++) {
            solver.addEdge(source - 1, i - 1, 1);  // Convert to 0-based indexing
        }

        // All Y nodes connect to sink
        for (int j = xNodes + 1; j <= xNodes + yNodes; j++) {
            solver.addEdge(j - 1, sink - 1, 1);
        }

        // Add the original bipartite graph edges X -> Y
        for (int i = 0; i < edges; ++i) {
            int fromX = io.getInt();
            int toY = io.getInt();
            solver.addEdge(fromX - 1, toY - 1, 1);  // All capacity 1 for matching
        }

        // Run max flow algorithm - this fills in the flow values
        solver.maxFlow(source - 1, sink - 1);

        // Extract which edges actually got flow - these form our matching
        List<long[]> flowResult = solver.getFlowEdges();
        matching.clear();

        for (long[] edge : flowResult) {
            long from = edge[0];  // Already converted back to 1-based
            long to = edge[1];

            // We only want X->Y edges (ignore source->X and Y->sink edges)
            // getFlowEdges() already filtered out zero-flow edges for us
            if (1 <= from && from <= xNodes &&
                (xNodes + 1) <= to && to <= (xNodes + yNodes)) {
                matching.add(new long[]{from, to});
            }
        }
    }

    void writeBipartiteMatchingSolution() {
        io.println(xNodes + " " + yNodes);
        io.println(matching.size());
        for (long[] pair : matching) {
            io.println(pair[0] + " " + pair[1]);
        }
        io.println();
    }

    BipartiteMatchingSolver() {
        io = new Kattio(System.in, System.out);
        readBipartiteGraphAndSolve();
        writeBipartiteMatchingSolution();
        io.close();
    }

    public static void main(String args[]) {
        new BipartiteMatchingSolver();
    }
}
