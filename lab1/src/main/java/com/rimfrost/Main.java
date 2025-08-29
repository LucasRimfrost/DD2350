package com.rimfrost;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) throws IOException{
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
             PrintWriter out = new PrintWriter(new BufferedOutputStream(System.out))) {
            new CommandProcessor().run(br, out);
            out.flush();
        }
    }
}
