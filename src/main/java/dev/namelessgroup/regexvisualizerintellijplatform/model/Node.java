package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class Node {
    private String content;
    private Node nextNode;
    private int lowerBound;
    private int upperBound;

    public String getContent() {
        return this.content;
    }

    public Node getNextNode() {
        return this.nextNode;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    public int getUpperBound() {
        return this.upperBound;
    }
}

