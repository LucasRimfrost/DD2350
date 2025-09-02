package com.rimfrost;
import java.util.*;

public class EdmondsKarpMaxFlow {
    private static class Edge {
        int to, rev;
        long capacity;
        boolean isOriginal;

        Edge(int to, int rev, long capacity, boolean isOriginal) {
            this.to = to;
            this.rev = rev;
            this.capacity = capacity;
            this.isOriginal = isOriginal;
        }
    }

    private List<Edge>[] graph;
    private int numNodes;

    // Need this to create generic array - Java doesn't like it but it works fine here
    @SuppressWarnings("unchecked")
    public EdmondsKarpMaxFlow(int numNodes) {
        this.numNodes = numNodes;
        graph = new List[numNodes];
        for (int i = 0; i < numNodes; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    public void addEdge(int from, int to, long capacity) {
        // This was confusing at first - need to add BOTH directions for max flow to work
        // The reverse edge starts with 0 capacity but gets updated as we find flow
        graph[from].add(new Edge(to, graph[to].size(), capacity, true));
        graph[to].add(new Edge(from, graph[from].size() - 1, 0, false));
    }

    public long maxFlow(int source, int sink) {
        long totalFlow = 0;
        int[] parent = new int[numNodes];
        int[] parentEdgeIndex = new int[numNodes];

        // Keep finding paths from source to sink until no more paths exist
        while (findAugmentingPath(source, sink, parent, parentEdgeIndex)) {
            // Walk back through the path to find the smallest capacity (bottleneck)
            long pathFlow = Long.MAX_VALUE;
            for (int node = sink; node != source; node = parent[node]) {
                Edge edge = graph[parent[node]].get(parentEdgeIndex[node]);
                pathFlow = Math.min(pathFlow, edge.capacity);
            }

            // Now push that much flow through the path
            for (int node = sink; node != source; node = parent[node]) {
                Edge forwardEdge = graph[parent[node]].get(parentEdgeIndex[node]);
                forwardEdge.capacity -= pathFlow;
                // This part took me a while to understand - we increase the reverse capacity
                // so we can "undo" flow later if we find a better path
                graph[node].get(forwardEdge.rev).capacity += pathFlow;
            }

            totalFlow += pathFlow;
        }
        return totalFlow;
    }

    private boolean findAugmentingPath(int source, int sink, int[] parent, int[] parentEdgeIndex) {
        Arrays.fill(parent, -1);
        Queue<Integer> queue = new ArrayDeque<>();
        queue.offer(source);
        parent[source] = source;

        while (!queue.isEmpty()) {
            int currentNode = queue.poll();

            for (int i = 0; i < graph[currentNode].size(); i++) {
                Edge edge = graph[currentNode].get(i);

                // Can only use edges that still have capacity left
                if (parent[edge.to] == -1 && edge.capacity > 0) {
                    parent[edge.to] = currentNode;
                    parentEdgeIndex[edge.to] = i;
                    queue.offer(edge.to);

                    // Found the sink! No need to keep searching
                    if (edge.to == sink) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<int[]> getFlowEdges() {
        List<int[]> result = new ArrayList<>();

        for (int u = 0; u < numNodes; u++) {
            for (Edge edge : graph[u]) {
                if (edge.isOriginal) {
                    // Tricky part: the actual flow on edge (u,v) is stored in reverse edge (v,u)
                    // because we've been adding flow to the reverse edges
                    Edge reverseEdge = graph[edge.to].get(edge.rev);
                    if (reverseEdge.capacity > 0) {
                        // Convert back to 1-based indexing for output format
                        result.add(new int[]{u + 1, edge.to + 1, (int)reverseEdge.capacity});
                    }
                }
            }
        }
        return result;
    }

    public static void solveFromIO(Kattio io) {
        int numVertices = io.getInt();
        // Input uses 1-based indexing but I'm using 0-based internally
        int source = io.getInt() - 1;
        int sink = io.getInt() - 1;
        int numEdges = io.getInt();

        EdmondsKarpMaxFlow maxFlowSolver = new EdmondsKarpMaxFlow(numVertices);

        for (int i = 0; i < numEdges; i++) {
            int from = io.getInt() - 1;
            int to = io.getInt() - 1;
            long capacity = io.getLong();
            maxFlowSolver.addEdge(from, to, capacity);
        }

        long maxFlowValue = maxFlowSolver.maxFlow(source, sink);
        List<int[]> flowEdges = maxFlowSolver.getFlowEdges();

        // Output needs to match the exact format from the assignment
        io.println(numVertices);
        io.println((source + 1) + " " + (sink + 1) + " " + maxFlowValue);
        io.println(flowEdges.size());
        for (int[] edge : flowEdges) {
            io.println(edge[0] + " " + edge[1] + " " + edge[2]);
        }
    }

    public static void main(String[] args) {
        Kattio io = new Kattio(System.in, System.out);
        solveFromIO(io);
        io.close();
    }
}
