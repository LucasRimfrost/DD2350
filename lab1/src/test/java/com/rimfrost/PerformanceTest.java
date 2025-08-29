package com.rimfrost;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class PerformanceTest {

    private String runWithInput(String input) throws IOException {
        BufferedReader in = new BufferedReader(new StringReader(input));
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        new CommandProcessor().run(in, out);
        out.flush();
        return sw.toString();
    }

    @Test
    void stressTimeGetsAndSets() throws IOException {
        // ~100k operations (50k sets + 50k gets)
        final int N = 50_000;

        StringBuilder sb = new StringBuilder(N * 20);
        for (int i = 0; i < N; i++) sb.append("set ").append(i).append(' ').append(i * 2).append('\n');
        for (int i = 0; i < N; i++) sb.append("get ").append(i).append('\n');

        long t0 = System.nanoTime();
        String out = runWithInput(sb.toString());
        long t1 = System.nanoTime();

        // Sanity check: should have N output lines
        int lines = out.isEmpty() ? 0 : out.split("\n", -1).length - (out.endsWith("\n") ? 1 : 0);
        assertEquals(N, lines);

        long ms = (t1 - t0) / 1_000_000;
        System.out.println("Stress time: " + ms + " ms for " + (2L * N) + " ops");
        // Very generous bound; tune down on your machine if you want
        assertTrue(ms < 10_000, "Too slow on local machine");
    }
}
