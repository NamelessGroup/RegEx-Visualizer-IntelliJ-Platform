package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RegExParserFileTest {

    LinkedList<String> lines;
    List<String> fails;


    @Test
    public void testRegexes() {
        lines = new LinkedList<>();
        readFile("regexTest.txt");
        // readFile("SupplementaryTestCases.txt");
        // readFile("BMPTestCases.txt");

        fails = new ArrayList<>();
        System.out.println("Starting test of " + lines.size() + " regexes");
        while (!lines.isEmpty()) {
            String line = lines.remove();
            RegexParser parser = new RegexParser(line);
            try {
                parser.buildRegexNodes();
            } catch (Exception e) {
                fails.add(new Fail(line, e).toString());
            }
        }
        System.out.println("Failed: " + fails.size());
        System.out.println(String.join("\n", fails));
    }

    private void readFile(String fileName) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        LinkedList<String> thisLines = new LinkedList<>();
        try {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = br.readLine()) != null) {
                    thisLines.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (!thisLines.isEmpty()) {
            if (thisLines.peek().startsWith("//")) {
                thisLines.remove();
            } else {
                lines.add(thisLines.remove());
                for (int i = 0; i < 3 && !thisLines.isEmpty(); i++) {
                    thisLines.remove();
                }
            }
        }
    }

    private static class Fail {
        String s;
        Exception e;

        public Fail(String s, Exception e) {
            this.s = s;
            this.e = e;
        }

        @Override
        public String toString() {
            return e.getClass().getSimpleName() + " at: " + s;
        }
    }
}
