package dev.namelessgroup.regexvisualizerintellijplatform.model;

public class Node {
    private final String content;
    private int lowerBound;
    private int upperBound;

    /**
     * Constructs a new node with the given content
     * @param content Content of the node
     */
    public Node(String content) {
        this.content = content;
        this.lowerBound = 1;
        this.upperBound = 1;
    }

    /**
     * Constructs a new node with the given content, upper and lower bound
     * @param content Content of the node
     * @param lowerBound Lower bound of the node
     * @param upperBound Upper bound of the node
     */
    public Node(String content, int lowerBound, int upperBound) {
        this.content = content;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public String getContent() {
        return this.content;
    }

    public int getLowerBound() {
        return this.lowerBound;
    }

    public int getUpperBound() {
        return this.upperBound;
    }

    public void setLowerBound(int lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void setUpperBound(int upperBound) {
        this.upperBound = upperBound;
    }

    public boolean isOptional() {
        return this.lowerBound == 0;
    }

    @Override
    public String toString() {
        return "Node(" + this.content + ")";
    }
}

