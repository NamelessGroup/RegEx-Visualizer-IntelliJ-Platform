package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.model.*;

import java.util.ArrayList;
import java.util.List;

public class RegexParser {

    private static final List<Character> lazySearchable = List.of('?', '*', '+');

    public List<Node> buildRegexNodes(String regex) throws RuntimeException {
        // TODO

        // Warning: probably a lot of spaghetti-code ahead
        List<Node> nodes = new ArrayList<>();
        String lastContent = "";
        boolean postGroup = false;

        for (int i = 0; i < regex.length(); i++) {
            char currentCharacter = regex.charAt(i);

            switch (currentCharacter) {
                case '|':
                    if (lastContent.length() > 0) {
                        // Terminate last content
                        this.addNode(nodes, new Node(lastContent));
                        lastContent = "";
                    }

                    if (nodes.get(nodes.size() - 1) instanceof OrNode) {
                        // We already have an or node, we need to start a new path
                        ((OrNode) nodes.get(nodes.size() - 1)).addNewPath();
                    } else {
                        // This is our first or node
                        OrNode rootOrNode = new OrNode(nodes);
                        nodes = new ArrayList<>();
                        nodes.add(rootOrNode);
                    }
                    break;
                case '?':
                    if (lazySearchable.contains(regex.charAt(i - 1)) && regex.charAt(i - 2) != '?') {
                        // This is our second ? in a row, needed for "lazy" search - we can safely ignore it
                        continue;
                    }

                    if (postGroup) {
                        // Since we're straight after one group, we'll make that one optional
                        Node toMakeOptional = this.getLastNode(nodes);
                        toMakeOptional.setLowerBound(0);
                        postGroup = false;
                    }

                    if (lastContent.equals("")) {
                        // throw some exception
                        throw new RuntimeException();
                    } else {
                        // This repeats the last content
                        String characterToRepeat = lastContent.substring(lastContent.length() - 1);
                        if (lastContent.length() > 1) {
                            this.addNode(nodes, new Node(lastContent.substring(0, lastContent.length() - 2)));
                        }
                        this.addNode(nodes, new Node(characterToRepeat, 0, 1));
                    }
                    lastContent = "";
                    break;
                case '*':
                    if (postGroup) {
                        // Since we're straight after one group, we'll make that one optional
                        Node toMakeOptionallyRepeating = this.getLastNode(nodes);
                        toMakeOptionallyRepeating.setLowerBound(0);
                        toMakeOptionallyRepeating.setUpperBound(-1);
                        postGroup = false;
                    }

                    if (lastContent.equals("")) {
                        // throw some exception
                        throw new RuntimeException();
                    } else {
                        // This repeats the last content
                        String characterToRepeat = lastContent.substring(lastContent.length() - 1);
                        if (lastContent.length() > 1) {
                            this.addNode(nodes, new Node(lastContent.substring(0, lastContent.length() - 2)));
                        }
                        this.addNode(nodes, new Node(characterToRepeat, 0, -1));
                    }
                    lastContent = "";
                    break;
                case '+':
                    if (postGroup) {
                        // Since we're straight after one group, we'll make that one optional
                        Node toMakeRepeating = this.getLastNode(nodes);
                        toMakeRepeating.setLowerBound(1);
                        toMakeRepeating.setUpperBound(-1);
                        postGroup = false;
                    }

                    if (lastContent.equals("")) {
                        // throw some exception
                        throw new RuntimeException();
                    } else {
                        // This repeats the last content
                        String characterToRepeat = lastContent.substring(lastContent.length() - 1);
                        if (lastContent.length() > 1) {
                            this.addNode(nodes, new Node(lastContent.substring(0, lastContent.length() - 2)));
                        }
                        this.addNode(nodes, new Node(characterToRepeat, 1, -1));
                    }
                    lastContent = "";
                    break;
                case '[':
                    if (lastContent.length() > 0) {
                        // Terminate last content
                        this.addNode(nodes, new Node(lastContent));
                        lastContent = "";
                    }

                    int j = i + 1;
                    if (regex.charAt(i + 1) == '^') {
                        j++;
                    }

                    // Figuring out how long the bracket is
                    int k = j;
                    while (k < regex.length()) {
                        if (regex.charAt(k) == ']' && regex.charAt(k - 1) != '\\') {
                            break;
                        }
                        k++;
                    }

                    if (regex.charAt(i + 1) == '^') {
                        this.addNode(nodes, new NoneOfNode(regex.substring(j, k)));
                    } else {
                        this.addNode(nodes, new OneOfNode(regex.substring(j, k)));
                    }
                    i += k;
                    postGroup = true;
                    break;
                case '(':
                    if (lastContent.length() > 0) {
                        // Terminate last content
                        this.addNode(nodes, new Node(lastContent));
                        lastContent = "";
                    }

                    int groupJ = i + 1;

                    // Figuring out how long the bracket is
                    int groupK = groupJ;
                    int amountOpenBrackets = 1;
                    while (groupK < regex.length()) {
                        if (regex.charAt(groupK) == ')' && regex.charAt(groupK - 1) != '\\') {
                            amountOpenBrackets--;
                        }
                        if (regex.charAt(groupK) == '(' && regex.charAt(groupK - 1) != '\\') {
                            amountOpenBrackets++;
                        }
                        if (amountOpenBrackets <= 0) {
                            break;
                        }
                        groupK++;
                    }

                    this.addNode(nodes, new GroupNode(this.buildRegexNodes(regex.substring(groupJ, groupK))));
                    i += groupK;
                    postGroup = true;
                    break;
                default:
                    lastContent += currentCharacter;
                    postGroup = false;
                    break;
            }
        }

        return nodes;
    }

    private void addNode(List<Node> nodes, Node nodeToAdd) {
        if (nodes.get(nodes.size() - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) nodes.get(nodes.size() - 1);
            lastOrNode.addNodeToLastPath(nodeToAdd);
        } else {
            nodes.add(nodeToAdd);
        }
    }

    private Node getLastNode(List<Node> nodes) {
        if (nodes.get(nodes.size() - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) nodes.get(nodes.size() - 1);
            return lastOrNode.getLastNode();
        } else {
            return nodes.get(nodes.size() - 1);
        }
    }

}
