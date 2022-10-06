package dev.namelessgroup.regexvisualizerintellijplatform.controller;

import dev.namelessgroup.regexvisualizerintellijplatform.model.Node;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.RegExImageFactory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RegexParserTests {

    @Test
    @DisplayName("Basic triple OR regex")
    public void regexOne() {
        RegexParser parser = new RegexParser("a(b)|s[9sd]|123");
        List<Node> nodes = parser.buildRegexNodes();
        assertEquals("[OrNode(Node(a),GroupNode(Node(b)) | Node(s),OneOfNode(9sd) | Node(123))]", nodes.toString());
    }

    @Test
    @DisplayName("Visual basic triple OR regex")
    @Disabled
    public void visualRegexOne() {
        JFrame frame = new JFrame();
        frame.setSize(800, 400);
        JLabel l = new JLabel();
        frame.add(new JScrollPane(l));

        long start = System.currentTimeMillis();
        List<Node> graph = new RegexParser("a(b)|s[9sd]|123").buildRegexNodes();
        long parse = System.currentTimeMillis();
        Image image = RegExImageFactory.createImage(graph);
        long imageGen = System.currentTimeMillis();
        l.setIcon(new ImageIcon(image));
        System.out.println("Parse: " + (parse - start) + "ms");
        System.out.println("Image: " + (imageGen - parse) + "ms");
        frame.setVisible(true);
        frame.requestFocus();
        while (frame.isFocused()) {};
    }

}
