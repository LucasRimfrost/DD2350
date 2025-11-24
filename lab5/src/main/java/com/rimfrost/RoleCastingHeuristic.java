package com.rimfrost;

import java.util.Arrays;

public class RoleCastingHeuristic {

  // === INPUT DATA ===
  private int n; // number of roles
  private int s; // number of scenes
  private int k; // number of actors

  private int[][] canPlay; // canPlay[role] = which actors can play this role
  private int[][] scenes; // scenes[scene] = which roles are in this scene

  // === DERIVED STRUCTURES ===
  private boolean[][] conflicts; // conflicts[r1][r2] = true if r1 and r2 are in the same scene

  // === SOLUTION ===
  private int[] assignment; // assignment[role] = which actor plays this role (0 = not assigned)
  private int[][] actorRoles; // actorRoles[actor] = list of roles this actor plays
  private int[] actorRoleCount; // how many roles each actor has

  private int nextSuperActor; // next super-actor number (k+1, k+2, ...)

  private Kattio io;

  public static void main(String[] args) {
    new RoleCastingHeuristic().solve();
  }

  public void solve() {
    io = new Kattio(System.in, System.out);

    readInput();
    buildConflicts();
    assignDivas();
    assignRemainingRoles();
    printSolution();

    io.close();
  }

  private void readInput() {
    n = io.getInt();
    s = io.getInt();
    k = io.getInt();

    canPlay = new int[n + 1][];
    scenes = new int[s][];
    conflicts = new boolean[n + 1][n + 1];
    assignment = new int[n + 1];
    actorRoles = new int[k + 1][];
    actorRoleCount = new int[k + 1];
    nextSuperActor = k + 1;

    for (int i = 1; i <= k; ++i) {
      actorRoles[i] = new int[n];
    }

    // read which actors can play each role
    for (int role = 1; role <= n; ++role) {
      int numEligibleActors = io.getInt();
      canPlay[role] = new int[numEligibleActors];
      for (int j = 0; j < numEligibleActors; ++j) {
        canPlay[role][j] = io.getInt();
      }
    }

    // read which roles are in each scene
    for (int scene = 0; scene < s; ++scene) {
      int numRolesInScene = io.getInt();
      scenes[scene] = new int[numRolesInScene];
      for (int j = 0; j < numRolesInScene; ++j) {
        scenes[scene][j] = io.getInt();
      }
    }
  }

  // builds conflict matrix - two roles conflict if they're in the same scene
  private void buildConflicts() {
    for (int scene = 0; scene < s; ++scene) {
      int[] rolesInScene = scenes[scene];

      // triangular loop to get all pairs without duplicates
      for (int i = 0; i < rolesInScene.length; ++i) {
        for (int j = i + 1; j < rolesInScene.length; ++j) {
          int r1 = rolesInScene[i];
          int r2 = rolesInScene[j];
          conflicts[r1][r2] = true;
          conflicts[r2][r1] = true;
        }
      }
    }
  }

  // assigns roles to diva 1 and diva 2
  // they must both get at least one role, and their roles can never conflict
  private void assignDivas() {
    int[] diva1Roles = getRolesForActor(1);
    int[] diva2Roles = getRolesForActor(2);

    // find a pair of roles that don't conflict
    boolean found = false;
    for (int r1 : diva1Roles) {
      for (int r2 : diva2Roles) {
        if (r1 != r2 && !conflicts[r1][r2]) {
          assign(r1, 1);
          assign(r2, 2);
          found = true;
          break;
        }
      }
      if (found) break;
    }

    // try to give diva 1 more roles
    for (int role = 1; role <= n; role++) {
      if (assignment[role] == 0
          && canActorPlayRole(1, role)
          && !conflictsWithActor(role, 1)
          && !conflictsWithActor(role, 2)) {
        assign(role, 1);
      }
    }

    // try to give diva 2 more roles
    for (int role = 1; role <= n; role++) {
      if (assignment[role] == 0
          && canActorPlayRole(2, role)
          && !conflictsWithActor(role, 2)
          && !conflictsWithActor(role, 1)) {
        assign(role, 2);
      }
    }
  }

  // greedy assignment of remaining roles
  // sorts by "hardest first" (fewest eligible actors)
  private void assignRemainingRoles() {
    // collect unassigned roles
    Integer[] unassignedRoles = new Integer[n];
    int count = 0;
    for (int role = 1; role <= n; ++role) {
      if (assignment[role] == 0) {
        unassignedRoles[count++] = role;
      }
    }

    // sort: fewer eligible actors = harder = do first
    Arrays.sort(unassignedRoles, 0, count, (a, b) -> canPlay[a].length - canPlay[b].length);

    for (int i = 0; i < count; i++) {
      int role = unassignedRoles[i];
      boolean assigned = false;

      // try 1: actor who already has roles (pack roles together)
      for (int actor : canPlay[role]) {
        if (actorRoleCount[actor] > 0 && !conflictsWithActor(role, actor)) {
          // don't let divas conflict with each other
          if (actor == 1 && conflictsWithActor(role, 2)) continue;
          if (actor == 2 && conflictsWithActor(role, 1)) continue;

          assign(role, actor);
          assigned = true;
          break;
        }
      }

      // try 2: new actor
      if (!assigned) {
        for (int actor : canPlay[role]) {
          if (actorRoleCount[actor] == 0 && !conflictsWithActor(role, actor)) {
            if (actor == 1 && conflictsWithActor(role, 2)) continue;
            if (actor == 2 && conflictsWithActor(role, 1)) continue;

            assign(role, actor);
            assigned = true;
            break;
          }
        }
      }

      // try 3: super-actor (fallback, always works)
      if (!assigned) {
        if (actorRoles.length <= nextSuperActor || actorRoles[nextSuperActor] == null) {
          actorRoles = Arrays.copyOf(actorRoles, nextSuperActor + 1);
          actorRoleCount = Arrays.copyOf(actorRoleCount, nextSuperActor + 1);
          actorRoles[nextSuperActor] = new int[1];
        }
        assign(role, nextSuperActor);
        nextSuperActor++;
      }
    }
  }

  private void printSolution() {
    int numActorsUsed = 0;

    for (int actor = 1; actor <= k; actor++) {
      if (actorRoleCount[actor] > 0) numActorsUsed++;
    }
    for (int actor = k + 1; actor < nextSuperActor; actor++) {
      if (actorRoleCount[actor] > 0) numActorsUsed++;
    }

    io.println(numActorsUsed);

    // print regular actors
    for (int actor = 1; actor <= k; actor++) {
      if (actorRoleCount[actor] > 0) {
        printActorAssignment(actor);
      }
    }

    // print super-actors
    for (int actor = k + 1; actor < nextSuperActor; actor++) {
      if (actorRoleCount[actor] > 0) {
        printActorAssignment(actor);
      }
    }
  }

  private void printActorAssignment(int actor) {
    io.print(actor);
    io.print(" " + actorRoleCount[actor]);
    for (int i = 0; i < actorRoleCount[actor]; i++) {
      io.print(" " + actorRoles[actor][i]);
    }
    io.println();
  }

  // === HELPER METHODS ===

  private void assign(int role, int actor) {
    assignment[role] = actor;
    actorRoles[actor][actorRoleCount[actor]] = role;
    actorRoleCount[actor]++;
  }

  // checks if role conflicts with any of the actor's current roles
  private boolean conflictsWithActor(int role, int actor) {
    for (int i = 0; i < actorRoleCount[actor]; ++i) {
      if (conflicts[role][actorRoles[actor][i]]) {
        return true;
      }
    }
    return false;
  }

  // checks if actor is in the canPlay list for a role
  private boolean canActorPlayRole(int actor, int role) {
    for (int eligibleActor : canPlay[role]) {
      if (eligibleActor == actor) {
        return true;
      }
    }
    return false;
  }

  // returns all roles an actor can play
  private int[] getRolesForActor(int actor) {
    int count = 0;
    for (int role = 1; role <= n; role++) {
      if (canActorPlayRole(actor, role)) {
        count++;
      }
    }

    int[] roles = new int[count];
    int index = 0;
    for (int role = 1; role <= n; role++) {
      if (canActorPlayRole(actor, role)) {
        roles[index++] = role;
      }
    }
    return roles;
  }
}
