package com.rimfrost;

import java.util.ArrayList;
import java.util.List;

public class BipartiteMatchingReducer {

  private Kattio io;
  private int xNodes, yNodes;
  private int source, sink, totalNodes;
  private List<int[]> flowEdges = new ArrayList<>();
  private List<int[]> matching = new ArrayList<>();

  void readBipartiteGraph() {
    xNodes = io.getInt();
    yNodes = io.getInt();
    int edges = io.getInt();

    totalNodes = xNodes + yNodes + 2;
    source = xNodes + yNodes + 1;
    sink = xNodes + yNodes + 2;

    flowEdges.clear();

    // Source -> X nodes
    for (int i = 1; i <= xNodes; i++) {
      flowEdges.add(new int[] {source, i});
    }

    // Y nodes -> Sink
    for (int j = xNodes + 1; j <= xNodes + yNodes; j++) {
      flowEdges.add(new int[] {j, sink});
    }

    // X -> Y edges (from input)
    for (int i = 0; i < edges; ++i) {
      int fromX = io.getInt();
      int toY = io.getInt();
      flowEdges.add(new int[] {fromX, toY});
    }
  }

  void writeFlowGraph() {
    io.println(totalNodes);
    io.println(source + " " + sink);
    io.println(flowEdges.size());
    for (int[] edge : flowEdges) {
      io.println(edge[0] + " " + edge[1] + " 1");
    }
    io.flush();
  }

  void readMaxFlowSolution() {
    matching.clear();
    int UNUSED_nodesIn = io.getInt();
    int UNUSED_sourceIn = io.getInt();
    int UNUSED_sinkIn = io.getInt();
    int UNUSED_maxFlowValue = io.getInt();

    int edgesWithFlow = io.getInt();

    for (int i = 0; i < edgesWithFlow; i++) {
      int from = io.getInt();
      int to = io.getInt();
      int flow = io.getInt();

      // Only take X->Y edges with positive flow
      if (flow > 0
          && 1 <= from
          && from <= xNodes
          && (xNodes + 1) <= to
          && to <= (xNodes + yNodes)) {
        matching.add(new int[] {from, to});
      }
    }
  }

  void writeBipartiteMatchingSolution() {
    io.println(xNodes + " " + yNodes);
    io.println(matching.size());
    for (int[] pair : matching) {
      io.println(pair[0] + " " + pair[1]);
    }
    io.println();
  }

  BipartiteMatchingReducer() {
    io = new Kattio(System.in, System.out);

    readBipartiteGraph();
    writeFlowGraph();
    readMaxFlowSolution();
    writeBipartiteMatchingSolution();

    io.close();
  }

  public static void main(String args[]) {
    new BipartiteMatchingReducer();
  }
}
