package dev.namelessgroup.regexvisualizerintellijplatform.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrNode extends Node {
    private final List<List<Node>> children;

    public OrNode() {
        super("");
        this.children = new ArrayList<>();
    }

    public OrNode(List<Node> firstPath) {
        super("");
        this.children = new ArrayList<>();
        this.children.add(firstPath);
        this.addNewPath();
    }

    public List<List<Node>> getChildren() {
       return new ArrayList<>(this.children);
    }

    public Node getLastNode() {
        List<Node> lastGroup = this.children.get(this.children.size() - 1);
        return lastGroup.get(lastGroup.size() - 1);
    }

    public void addNewPath() {
        this.children.add(new ArrayList<>());
    }

    public void addNodeToLastPath(Node nodeToAdd) {
        this.children.get(this.children.size() - 1).add(nodeToAdd);
    }

    @Override
    public String toString() {
        return "OrNode(" + this.children.stream().map(e -> e.stream().map(Node::toString).collect(Collectors.joining(","))).collect(Collectors.joining(" | ")) + ")";
    }

}
