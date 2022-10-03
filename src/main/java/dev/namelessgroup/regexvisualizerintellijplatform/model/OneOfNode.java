package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class OneOfNode extends Node {
    public OneOfNode(String content) {
        super(content);
    }

    public OneOfNode(String content, int lowerBound, int upperBound) {
        super(content, lowerBound, upperBound);
    }
}
