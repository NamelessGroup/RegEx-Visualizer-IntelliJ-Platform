package dev.namelessgroup.regexvisualizerintellijplatform.model;

import java.util.List;
import java.util.stream.Collectors;

public class NonCapturingGroupNode extends GroupNode {

    public NonCapturingGroupNode() {
        super();
    }

    public NonCapturingGroupNode(List<Node> children) {
        super(children);
    }

    @Override
    public String toString() {
        return "NonCapturingGroupNode(" + this.children.stream().map(Node::toString).collect(Collectors.joining(",")) + ")";
    }
}
