package dev.namelessgroup.regexvisualizerintellijplatform.model;

import java.util.List;

public class GroupNode extends Node {
    private List<Node> children;

    public GroupNode(String content) {
        super(content);
    }

    public GroupNode(List<Node> children) {
        super("");
        this.children = children;
    }

    public GroupNode(String content, int lowerBound, int upperBound) {
        super(content, lowerBound, upperBound);
    }

    public List<Node> getNodes() {
        return children;
    }
}
