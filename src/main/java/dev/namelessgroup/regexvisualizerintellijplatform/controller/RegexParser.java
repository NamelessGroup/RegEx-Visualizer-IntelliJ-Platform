package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.model.Node;
import org.apache.commons.lang.NotImplementedException;

import java.util.ArrayList;
import java.util.List;

public class RegexParser {

    private static final List<Character> lazySearchable = List.of('?', '*', '+');

    public List<Node> buildRegexNodes(String regex) {
        // TODO

        // Warning: probably a lot of spaghetti-code ahead
        List<Node> nodes = new ArrayList<>();
        String lastContent = "";

        for (int i = 0; i < regex.length(); i++) {
            char currentCharacter = regex.charAt(i);

            switch (currentCharacter) {
                case '|':
                    // TODO: OrNode
                    break;
                case '?':
                    if (lazySearchable.contains(regex.charAt(i - 1)) && regex.charAt(i - 2) != '?') {
                        // This is our second ? in a row, needed for "lazy" search - we can safely ignore it
                        continue;
                    }
                    nodes.add(new Node(lastContent, 0, 1));
                    break;
                case '*':
                    nodes.add(new Node(lastContent, 0, -1));
                    break;
                case '+':
                    nodes.add(new Node(lastContent, 1, -1));
                    break;
                default:
                    lastContent += currentCharacter;
                    break;
            }
        }

        return nodes;
    }

}
