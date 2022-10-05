package dev.namelessgroup.regexvisualizerintellijplatform.model;

import java.util.ArrayList;
import java.util.List;

public class NonCapturingGroupNode extends GroupNode {

    private List<Node> children;

    public NonCapturingGroupNode() {
        super();
        this.children = new ArrayList<>();
    }

    public NonCapturingGroupNode(int lowerBound, int upperBound) {
        super("", lowerBound, upperBound);
        this.children = new ArrayList<>();
    }

    public List<Node> getChildren() {
        return new ArrayList<>(this.children);
    }

    public void addChild(Node node) {
        this.children.add(node);
    }
}
