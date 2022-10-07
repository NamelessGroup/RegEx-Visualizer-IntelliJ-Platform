package dev.namelessgroup.regexvisualizerintellijplatform.model;

import java.util.List;
import java.util.stream.Collectors;

public class GroupNode extends Node {
    protected List<Node> children;

    public GroupNode() {
        super("");
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

    @Override
    public String toString() {
        return "GroupNode(" + this.children.stream().map(Node::toString).collect(Collectors.joining(",")) + ")";
    }
}
