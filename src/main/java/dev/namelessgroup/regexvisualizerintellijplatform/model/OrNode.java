package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class OrNode extends Node {
    private Node[] children;

    public OrNode(String content) {
        super(content);
    }

    public OrNode(String content, int lowerBound, int upperBound) {
        super(content, lowerBound, upperBound);
    }
}
