package com.rimfrost;

import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class CommandProcessorTest {

    private String runWithInput(String input) throws IOException {
        BufferedReader in = new BufferedReader(new StringReader(input));
        StringWriter sw = new StringWriter();
        PrintWriter out = new PrintWriter(sw);
        new CommandProcessor().run(in, out);
        out.flush();
        return sw.toString();
    }

    @Test
    void labExampleFromSpec() throws IOException {
        String input = String.join("\n",
            "set 3 17",
            "set 3 4711",
            "get 3",
            "set 3 1000",
            "unset",
            "get 3",
            "unset",
            "get 3",
            ""
        );
        String expected = String.join("\n", "4711", "4711", "17", "");
        assertEquals(expected, runWithInput(input));
    }

    @Test
    void unsetBoundsAndBlanks() throws IOException {
        String input = String.join("\n",
            "   ",
            "unset",
            "get 9",
            "set 9 9",
            "unset",
            "unset",
            "get 9",
            ""
        );
        String expected = String.join("\n", "0", "0", "");
        assertEquals(expected, runWithInput(input));
    }

    @Test
    void multipleIndicesAndLarge() throws IOException {
        String input = String.join("\n",
            "set 1000000 7",
            "set 3 42",
            "get 1000000",
            "get 3",
            ""
        );
        String expected = String.join("\n", "7", "42", "");
        assertEquals(expected, runWithInput(input));
    }

    @Test
    void overwriteThenUnsetStepwise() throws IOException {
        String input = String.join("\n",
            "set 5 1",
            "set 5 2",
            "set 5 3",
            "get 5",
            "unset",
            "get 5",
            "unset",
            "get 5",
            "unset",
            "get 5",
            ""
        );
        String expected = String.join("\n", "3", "2", "1", "0", "");
        assertEquals(expected, runWithInput(input));
    }
}
