package dev.namelessgroup.regexvisualizerintellijplatform.controller.parsers;

import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexParser;
import dev.namelessgroup.regexvisualizerintellijplatform.model.*;

import java.util.List;

public class JavaRegexParser extends RegexParser {

    private boolean inQuote;

    public JavaRegexParser(String regex) {
        super(regex, List.of('+', '*', '?'));
        inQuote = false;
    }

    @Override
    protected boolean handleCharacter(char character, int position) {
        if (inQuote) {
            if (character == '\\' && this.regex.charAt(position + 1) == 'E') {
                this.inQuote = false;
                return false;
            } else {
                this.lastContent += character;
                return true;
            }
        }

        return false;
    }

    @Override
    protected void handleEscapeCharacter(char character, int position) {
        if (character == 'Q') {
            this.inQuote = true;
        } else {
            this.lastContent += character;
        }
    }

    @Override
    protected void handleCharacterRange(String content, int startingPosition) {
        if (content.charAt(0) == '^') {
            this.addNode(new NoneOfNode(content.substring(1)));
        } else {
            this.addNode(new OneOfNode(content));
        }
    }

    @Override
    protected void handleGroup(String content, int startingPosition) {
        boolean isCapturingGroup = true;

        // Checking for weird thing's at the start of the group
        if (content.charAt(0) == '?') {
            content = content.substring(1);
            if (content.charAt(0) == ':') {
                content = content.substring(1);
                isCapturingGroup = false;
            } else {
                if (content.charAt(0) == '<') {
                    content = content.substring(1);
                }
                if (content.charAt(0) == '=' || content.charAt(0) == '!') {
                    content = content.substring(1);
                }
            }
        }

        // Parsing the group content
        List<Node> groupContent = new JavaRegexParser(content).buildRegexNodes();
        if (isCapturingGroup) {
            this.addNode(new GroupNode(groupContent));
        } else {
            this.addNode(new NonCapturingGroupNode(groupContent));
        }
    }

    @Override
    protected void handleGroupModifier(char modifier, int position) {
        if (this.isGroupModifier(this.regex.charAt(position - 1)) && this.regex.charAt(position - 2) != modifier &&
            (modifier == '+' || modifier == '?')) {
            // This is our second + or ? in a row, needed for special searches - we can safely ignore it
            return;
        }

        Node toUpdate = this.getLastNode();
        switch (modifier) {
            case '?':
                toUpdate.setLowerBound(0);
                break;
            case '*':
                toUpdate.setLowerBound(0);
                toUpdate.setUpperBound(-1);
                break;
            case '+':
                toUpdate.setLowerBound(1);
                toUpdate.setUpperBound(-1);
                break;
        }
    }
}