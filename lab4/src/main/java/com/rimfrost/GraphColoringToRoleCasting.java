package com.rimfrost;

public class GraphColoringToRoleCasting {

  // Input från graffärgning
  private int V; // Antal hörn (1 <= V <= 300)
  private int E; // Antal kanter (0 <= E <= 25000)
  private int m; // Antal färger (1 <= m <= 2^30)
  private int[] edgeU; // Kanternas första ändpunkter
  private int[] edgeV; // Kanternas andra ändpunkter
  private boolean[] rolesInScenes; // Spårar vilka roller som har scener

  // Output till rollbesättning
  private int n; // Antal roller
  private int s; // Antal scener
  private int k; // Antal skådespelare
  private int numIsolated; // Antal isolerade roller

  private Kattio io;

  public static void main(String[] args) {
    Kattio io = new Kattio(System.in, System.out);
    new GraphColoringToRoleCasting().solve(io);
    io.close();
  }

  public void solve(Kattio io) {
    this.io = io;
    readGraphColoringInput();
    computeRoleCastingParameters();
    writeRoleCastingOutput();
  }

  private void readGraphColoringInput() {
    V = io.getInt();
    E = io.getInt();
    m = io.getInt();

    edgeU = new int[E];
    edgeV = new int[E];
    rolesInScenes = new boolean[V + 1];

    for (int i = 0; i < E; i++) {
      edgeU[i] = io.getInt();
      edgeV[i] = io.getInt();
      rolesInScenes[edgeU[i]] = true;
      rolesInScenes[edgeV[i]] = true;
    }
  }

  private void computeRoleCastingParameters() {
    n = V + 2;
    k = Math.min(m, V) + 2; // Onödigt att ha fler skådisar än roller
    numIsolated = countIsolatedRoles();
    s = E + 2 + numIsolated;
  }

  private int countIsolatedRoles() {
    int count = 0;
    for (int role = 1; role <= V; role++) {
      if (!rolesInScenes[role]) {
        count++;
      }
    }
    return count;
  }

  private void writeRoleCastingOutput() {
    writeOutputHeader();
    writeActorConstraints();
    writeSceneConstraints();
  }

  private void writeOutputHeader() {
    io.println(n);
    io.println(s);
    io.println(k);
  }

  private void writeActorConstraints() {
    writeHornRoleConstraints();
    writeP1RoleConstraint();
    writeP2RoleConstraint();
  }

  private void writeHornRoleConstraints() {
    int numColorActors = k - 2;

    StringBuilder actorList = new StringBuilder();
    for (int actor = 3; actor <= k; actor++) {
      actorList.append(' ').append(actor);
    }
    String actorString = actorList.toString();

    for (int role = 1; role <= V; role++) {
      io.print(numColorActors);
      io.print(actorString);
      io.println();
    }
  }

  private void writeP1RoleConstraint() {
    io.println("1 1");
  }

  private void writeP2RoleConstraint() {
    io.println("1 2");
  }

  private void writeSceneConstraints() {
    writeScenesFromEdges();
    writeP1Scene();
    writeP2Scene();
    writeIsolatedRoleScenes();
  }

  private void writeScenesFromEdges() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < E; i++) {
      sb.setLength(0);
      sb.append("2 ").append(edgeU[i]).append(' ').append(edgeV[i]);
      io.println(sb.toString());
    }
  }

  private void writeP1Scene() {
    io.print("2 ");
    io.print(V + 1); // p1's roll
    io.print(' ');
    io.println(1); // Para med roll 1
  }

  private void writeP2Scene() {
    io.print("2 ");
    io.print(V + 2); // p2's roll
    io.print(' ');
    if (V >= 2) {
      io.println(2); // Para med roll 2
    } else {
      io.println(1); // V=1: Para med roll 1 (olika scen än p1)
    }
  }

  private void writeIsolatedRoleScenes() {
    // OPTIMERING: Använd StringBuilder
    StringBuilder sb = new StringBuilder();
    for (int role = 1; role <= V; role++) {
      if (!rolesInScenes[role]) {
        sb.setLength(0);
        sb.append("2 ").append(V + 1).append(' ').append(role);
        io.println(sb.toString());
      }
    }
  }
}
