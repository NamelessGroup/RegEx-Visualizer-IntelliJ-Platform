package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class NonCapturingGroupNode extends GroupNode {
    public NonCapturingGroupNode(String content) {
        super(content);
    }

    public NonCapturingGroupNode(String content, int lowerBound, int upperBound) {
        super(content, lowerBound, upperBound);
    }
}
