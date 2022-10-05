package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class NoneOfNode extends Node {
    public NoneOfNode(String content) {
        super(content);
    }

    public NoneOfNode(String content, int lowerBound, int upperBound) {
        super(content, lowerBound, upperBound);
    }

    @Override
    public String toString() {
        return "NoneOfNode(" + this.getContent() + ")";
    }
}
