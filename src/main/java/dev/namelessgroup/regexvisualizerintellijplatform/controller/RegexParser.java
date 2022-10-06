package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.model.*;

import java.util.ArrayList;
import java.util.List;

public class RegexParser {

    private static final List<Character> lazySearchable = List.of('?', '*', '+');

    private final String regex;
    private List<Node> nodes;
    private String lastContent;

    public RegexParser(String regex) {
        this.regex = regex;
        this.nodes = new ArrayList<>();
        this.lastContent = "";
    }

    public List<Node> buildRegexNodes() throws RuntimeException {
        boolean postGroup = false;
        boolean escapeCharacter = false;

        for (int i = 0; i < this.regex.length(); i++) {
            char currentCharacter = this.regex.charAt(i);

            if (escapeCharacter) {
                this.lastContent += escapeCharacter;
                escapeCharacter = false;
            }

            switch (currentCharacter) {
                case '|':
                    this.terminateLastContent();

                    if (this.nodes.get(this.nodes.size() - 1) instanceof OrNode) {
                        // We already have an or node, we need to start a new path
                        ((OrNode) this.nodes.get(this.nodes.size() - 1)).addNewPath();
                    } else {
                        // This is our first or node
                        OrNode rootOrNode = new OrNode(this.nodes);
                        this.nodes = new ArrayList<>();
                        this.nodes.add(rootOrNode);
                    }
                    postGroup = false;
                    break;
                case '?':
                    if (lazySearchable.contains(regex.charAt(i - 1)) && regex.charAt(i - 2) != '?') {
                        // This is our second ? in a row, needed for "lazy" search - we can safely ignore it
                        continue;
                    }

                    if (!postGroup) {
                        this.terminateLastCharacterOrThrow();
                    }
                    Node toMakeOptional = this.getLastNode();
                    toMakeOptional.setLowerBound(0);
                    postGroup = false;
                    break;
                case '*':
                    if (!postGroup) {
                        this.terminateLastCharacterOrThrow();
                    }
                    Node toMakeOptionallyRepeating = this.getLastNode();
                    toMakeOptionallyRepeating.setLowerBound(0);
                    toMakeOptionallyRepeating.setUpperBound(-1);
                    postGroup = false;
                    break;
                case '+':
                    if (!postGroup) {
                        this.terminateLastCharacterOrThrow();
                    }
                    Node toMakeRepeating = this.getLastNode();
                    toMakeRepeating.setLowerBound(1);
                    toMakeRepeating.setUpperBound(-1);
                    postGroup = false;
                    break;
                case '[':
                    this.terminateLastContent();

                    int j = i + 1;
                    if (regex.charAt(i + 1) == '^') {
                        j++;
                    }

                    // Figuring out how long the bracket is
                    int k = j;
                    while (k < this.regex.length()) {
                        if (this.regex.charAt(k) == ']' && this.regex.charAt(k - 1) != '\\') {
                            break;
                        }
                        k++;
                    }

                    if (this.regex.charAt(i + 1) == '^') {
                        this.addNode(new NoneOfNode(this.regex.substring(j, k)));
                    } else {
                        this.addNode(new OneOfNode(this.regex.substring(j, k)));
                    }
                    i = k;
                    postGroup = true;
                    break;
                case '(':
                    this.terminateLastContent();

                    int groupJ = i + 1;

                    // Figuring out how long the bracket is
                    int groupK = groupJ;
                    int amountOpenBrackets = 1;
                    while (groupK < this.regex.length()) {
                        if (this.regex.charAt(groupK) == ')' && this.regex.charAt(groupK - 1) != '\\') {
                            amountOpenBrackets--;
                        }
                        if (this.regex.charAt(groupK) == '(' && this.regex.charAt(groupK - 1) != '\\') {
                            amountOpenBrackets++;
                        }
                        if (amountOpenBrackets <= 0) {
                            break;
                        }
                        groupK++;
                    }

                    this.addNode(new GroupNode(new RegexParser(this.regex.substring(groupJ, groupK)).buildRegexNodes()));
                    i = groupK;
                    postGroup = true;
                    break;
                default:
                    if (currentCharacter == '\\') {
                        escapeCharacter = true;
                    } else {
                        this.lastContent += currentCharacter;
                    }
                    postGroup = false;
                    break;
            }
        }

        // Add the last group
        this.terminateLastContent();

        return nodes;
    }

    private void addNode(Node nodeToAdd) {
        if (this.nodes.size() > 0 && this.nodes.get(this.nodes.size() - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) this.nodes.get(this.nodes.size() - 1);
            lastOrNode.addNodeToLastPath(nodeToAdd);
        } else {
            this.nodes.add(nodeToAdd);
        }
    }

    private Node getLastNode() {
        if (this.nodes.size() <= 0) {
            return null;
        }
        if (this.nodes.get(this.nodes.size() - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) this.nodes.get(this.nodes.size() - 1);
            return lastOrNode.getLastNode();
        } else {
            return this.nodes.get(this.nodes.size() - 1);
        }
    }

    private void terminateLastContent() {
        if (this.lastContent.length() > 0) {
            this.addNode(new Node(this.lastContent));
            this.lastContent = "";
        }
    }

    private void terminateLastCharacterOrThrow() {
        if (this.lastContent.length() <= 0) {
            throw new RuntimeException();
        }
        if (this.lastContent.length() > 1) {
            this.addNode(new Node(this.lastContent.substring(0, lastContent.length() - 2)));
        }
        this.addNode(new Node(this.lastContent.substring(lastContent.length() - 1)));
        this.lastContent = "";
    }

}
