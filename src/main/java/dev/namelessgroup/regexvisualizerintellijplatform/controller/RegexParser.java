package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public abstract class RegexParser {

    protected final String regex;
    protected List<Node> nodes;
    protected String lastContent;
    protected boolean postGroup;
    protected List<Character> groupModifiers;

    protected RegexParser(String regex) {
        this.regex = regex;
        this.nodes = new ArrayList<>();
        this.lastContent = "";
        this.postGroup = false;
        this.groupModifiers = List.of('+', '?', '*');
    }

    protected RegexParser(String regex, List<Character> groupModifiers) {
        this.regex = regex;
        this.nodes = new ArrayList<>();
        this.lastContent = "";
        this.postGroup = false;
        this.groupModifiers = groupModifiers;
    }

    /**
     * Building the regex tree
     */
    public List<Node> buildRegexNodes() throws PatternSyntaxException {
        for (int i = 0; i < this.regex.length(); i++) {
            char currentCharacter = this.regex.charAt(i);

            if (this.handleCharacter(currentCharacter, i)) {
                this.postGroup = false;
                continue;
            }

            if (this.isGroupModifier(currentCharacter)) {
                if (!this.postGroup) {
                    this.terminateLastCharacterOrThrow();
                }
                this.handleGroupModifier(currentCharacter, i);
                this.postGroup = false;
                continue;
            }

            switch (currentCharacter) {
                case '|':
                    this.terminateLastContent();

                    this.handleOr(i);
                    this.postGroup = false;
                    break;
                    case '\\':
                        char escapedCharacter = this.regex.charAt(i + 1);
                        this.handleEscapeCharacter(escapedCharacter, i + 1);
                        i++;
                        this.postGroup = false;
                        break;
                        case '[':
                            this.terminateLastContent();

                            int openingRangeBracket = i;
                            int closingRangeBracket = this.findMatchingRangeBracket(i);
                            String rangeContent = this.regex.substring(openingRangeBracket + 1, closingRangeBracket);

                            this.handleCharacterRange(rangeContent, openingRangeBracket + 1);
                            i = closingRangeBracket;
                            this.postGroup = true;
                            break;
                            case '(':
                                this.terminateLastContent();

                                int openingGroupBracket = i;
                                int closingGroupBracket = this.findMatchingGroupBracket(i);
                                String groupContent = this.regex.substring(openingGroupBracket + 1, closingGroupBracket);

                                this.handleGroup(groupContent, openingGroupBracket + 1);
                                i = closingGroupBracket;
                                this.postGroup = true;
                                break;
                                default:
                                    this.lastContent += currentCharacter;
                                    this.postGroup = false;
                                    break;
            }
        }

        // Add the last group
        this.terminateLastContent();

        return this.nodes;
    }

    /**
    * Handling individual characters. Might be useful for escaping, etc.
    * To be overridden by subclasses.
    */
    protected boolean handleCharacter(char character, int position) {
        return false;
    }

    protected abstract void handleEscapeCharacter(char character, int position);
    protected abstract void handleCharacterRange(String content, int startingPosition);
    protected abstract void handleGroup(String content, int startingPosition);
    protected abstract void handleGroupModifier(char modifier, int position);

    protected void handleOr(int position) {
        if (this.nodes.size() > 0 && this.nodes.get(this.nodes.size() - 1) instanceof OrNode) {
            // We already have an OrNode, we need to add a new path
            ((OrNode) this.nodes.get(this.nodes.size() - 1)).addNewPath();
        } else {
            // This is our first OrNode
            OrNode rootOrNode = new OrNode(this.nodes);
            this.nodes = new ArrayList<>();
            this.nodes.add(rootOrNode);
        }
    }

    protected boolean isGroupModifier(char character) {
        return this.groupModifiers.contains(character);
    }

    protected void addNode(Node node) {
        int amountNodes = this.nodes.size();
        if (amountNodes > 0 && this.nodes.get(amountNodes - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) this.nodes.get(amountNodes - 1);
            lastOrNode.addNodeToLastPath(node);
        } else {
            this.nodes.add(node);
        }
    }

    protected Node getLastNode() {
        if (this.nodes.size() == 0) {
            return null;
        }
        if (this.nodes.get(this.nodes.size() - 1) instanceof OrNode) {
            OrNode lastOrNode = (OrNode) this.nodes.get(this.nodes.size() - 1);
            return lastOrNode.getLastNode();
        }
        return this.nodes.get(this.nodes.size() - 1);
    }

    protected void terminateLastContent() {
        if (this.lastContent.length() > 0) {
            this.addNode(new Node(this.lastContent));
            this.lastContent = "";
        }
    }

    protected void terminateLastCharacterOrThrow() throws PatternSyntaxException {
        if (this.lastContent.length() <= 0) {
            throw new PatternSyntaxException("Nothing to repeat", this.regex, -1);
        }
        if (this.lastContent.length() > 1) {
            String preContent = this.lastContent.substring(0, lastContent.length() - 2);
            this.addNode(new Node(preContent));
        }
        String content = this.lastContent.substring(lastContent.length() - 1);
        this.addNode(new Node(content));
        this.lastContent = "";
    }

    protected int findMatchingRangeBracket(int startingBracket) throws PatternSyntaxException {
        int i = startingBracket + 1;
        while (i < this.regex.length()) {
            if (this.regex.charAt(i) == ']' && this.regex.charAt(i - 1) != '\\') {
                return i;
            }

            i++;
        }
        throw new PatternSyntaxException("Missing closing bracket", this.regex, startingBracket);
    }

    protected int findMatchingGroupBracket(int startingBracket) throws PatternSyntaxException {
        int i = startingBracket + 1;
        int amountOpenBrackets = 1;
        while (i < this.regex.length()) {
            if (this.regex.charAt(i) == ')' && this.regex.charAt(i - 1) != '\\') {
                amountOpenBrackets--;
            } else if (this.regex.charAt(i) == '(' && this.regex.charAt(i - 1) != '\\') {
                amountOpenBrackets++;
            }

            if (amountOpenBrackets == 0) {
                return i;
            }
            i++;
        }
        throw new PatternSyntaxException("Missing closing bracket", this.regex, startingBracket);
    }

}