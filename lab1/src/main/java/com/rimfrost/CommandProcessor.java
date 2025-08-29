package com.rimfrost;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.StringTokenizer;

public class CommandProcessor {
    private final Deque<DynamicArray> versions = new ArrayDeque<>();

    public CommandProcessor() {
        versions.push(DynamicArray.newarray());
    }

    public void run(BufferedReader in, PrintWriter out) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            StringTokenizer st = new StringTokenizer(line);
            String cmd = st.nextToken();

            switch (cmd) {
                case "get": {
                    if (!st.hasMoreTokens()) break; // assume valid input on Kattis
                    int i = Integer.parseInt(st.nextToken());
                    out.println(versions.peek().get(i));
                    break;
                }
                case "set": {
                    if (!st.hasMoreTokens()) break;
                    int i = Integer.parseInt(st.nextToken());
                    if (!st.hasMoreTokens()) break;
                    int value = Integer.parseInt(st.nextToken());

                    DynamicArray curr = versions.peek();
                    DynamicArray next = curr.set(i, value);
                    // Guard: don’t push if set was a no-op (e.g., i < 0)
                    if (next != curr) versions.push(next);
                    break;
                }
                case "unset": {
                    if (versions.size() > 1) versions.pop();
                    break;
                }
                default:
                    // ignore invalid commands silently (typical for Kattis unless specified)
            }
        }
    }
}
