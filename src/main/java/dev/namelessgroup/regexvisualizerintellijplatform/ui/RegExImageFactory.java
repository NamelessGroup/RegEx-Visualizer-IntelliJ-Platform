package dev.namelessgroup.regexvisualizerintellijplatform.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ImageUtil;
import dev.namelessgroup.regexvisualizerintellijplatform.model.*;
import org.jetbrains.annotations.NotNull;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Image;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for transforming a graph given by {@link org.intellij.lang.regexp.RegExpParser the parser} into an {@link Image}
 * @version 1.0
 */
public final class RegExImageFactory {

    // Font data
    private static final Font CONTENT_FONT = new Font("JetBrains Mono", Font.PLAIN, 36);
    private static final FontMetrics CONTENT_FONT_METRICS = getFontMetrics(CONTENT_FONT);
    private static final Font INFO_FONT = new Font("JetBrains Mono", Font.PLAIN, 28);
    private static final FontMetrics INFO_FONT_METRICS = getFontMetrics(INFO_FONT);


    // Dimensions
    private static final int LINE_BASE_LENGTH = 16;
    private static final int TEXT_SIDE_ROOM = 14;
    private static final int STROKE_WIDTH = 4;
    private static final int GROUP_SIDE_SPACING = 16;
    private static final int NODE_STACKING_SPACING = 8;
    private static final int OUTSIDE_LINE_SPACING = 8;


    // Colors
    private static Color NODE_COLOR = makeTransparent(JBColor.ORANGE, 200, 200);
    private static Color OPTION_NODE_COLOR = makeTransparent(JBColor.RED, 200, 200);
    private static Color GROUP_NODE_COLOR = makeTransparent(JBColor.YELLOW, 40, 40);
    private static Color LINE_COLOR = JBColor.GRAY;
    private static Color TEXT_COLOR = JBColor.BLACK;
    private static Color END_NODE_COLOR = JBColor.BLACK;

    // Runtime variables
    private static int GROUP_COUNT = 1;

    /**
     * Creates an image of the given graph given by {@link org.intellij.lang.regexp.RegExpParser the parser}
     * @param nodes Graph to transform
     * @return Image of the graph
     */
    public static @NotNull Image createImage(List<Node> nodes) {
        return createImage(nodes,
                makeTransparent(JBColor.ORANGE, 200, 200),
                makeTransparent(JBColor.RED, 200, 200),
                makeTransparent(JBColor.YELLOW, 40, 40),
                JBColor.GRAY, JBColor.BLACK, JBColor.BLACK);
    }

    /**
     * Creates an image of the given graph given by {@link org.intellij.lang.regexp.RegExpParser the parser}
     * @param nodes Graph to transform
     * @param nodeColor  Color of basic text nodes
     * @param optionNodeColor Color of nodes like "One of" and "None of"
     * @param groupNodeColor Color of groups
     * @param lineColor Color of lines between nodes
     * @param textColor Color of text
     * @param endNodeColor Color of start and end node
     * @return Image of the graph
     */
    public static @NotNull Image createImage(List<Node> nodes, Color nodeColor, Color optionNodeColor, Color groupNodeColor, Color lineColor, Color textColor, Color endNodeColor) {
        NODE_COLOR = nodeColor;
        OPTION_NODE_COLOR = optionNodeColor;
        GROUP_NODE_COLOR = groupNodeColor;
        LINE_COLOR = lineColor;
        TEXT_COLOR = textColor;
        END_NODE_COLOR = endNodeColor;

        GROUP_COUNT = 1;

        // form image from its three components: start-node, graph, end-node
        ImageNode start = drawStartingNode();
        start.appendImage(imageFromNodeStructure(nodes));
        start.appendImage(drawEndingNode());

        BufferedImage image = start.image;
        // scaling ensures the image is not as pixelated
        return image.getScaledInstance(image.getWidth() / 2, image.getHeight() / 2, Image.SCALE_SMOOTH);
    }

    /**
     * Creates an {@link ImageNode image} from a list of {@link Node nodes}
     * @param nodes List of nodes to transform
     * @return ImageNode of the list
     */
    private static @NotNull ImageNode imageFromNodeStructure(List<Node> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            return new ImageNode(ImageUtil.createImage(1,1,BufferedImage.TYPE_INT_ARGB),0);
        }

        // get image of first node
        ImageNode imageNode = new ImageNode(nodes.get(0));
        Iterator<Node> nodeIterator = nodes.iterator();
        nodeIterator.next();
        // append nodes that come behind it
        while (nodeIterator.hasNext()) {
            imageNode.appendImage(new ImageNode(nodeIterator.next()));
        }
        return imageNode;
    }

    /**
     * Representation of a {@link Node} as an image
     */
    private static class ImageNode {
        private BufferedImage image;
        private int lineHeight;

        /**
         * Generates an image from the contents of the node
         * @param node Node to make image of
         */
        private ImageNode(Node node) {
            if (node instanceof GroupNode) {
                int thisGroup = GROUP_COUNT;
                GROUP_COUNT++;
                // Generate image of content inside node
                ImageNode content = imageFromNodeStructure(((GroupNode) node).getNodes());

                // draw rectangle around it, if it is a capturing group, otherwise just set image of this class to the contents image
                if (node instanceof NonCapturingGroupNode) {
                    image = content.image;
                    lineHeight = content.lineHeight;
                } else {
                    createGroupNode(content, thisGroup);
                }
            } else if (node instanceof OrNode) {
                stackNodes(Arrays.stream(((OrNode) node).getChildren()).map(ImageNode::new).toArray(ImageNode[]::new));
            } else if (node instanceof OneOfNode) {
                createTextNode(String.join(" ", node.getContent().split("")), OPTION_NODE_COLOR);
                addInfoText("One of", true);
            } else if (node instanceof NoneOfNode) {
                createTextNode(String.join(" ", node.getContent().split("")), OPTION_NODE_COLOR);
                addInfoText("None of", true);
            } else {
                createTextNode(node.getContent(), NODE_COLOR);
            }

            // Extend lines if optional or repeating to get their lines in
            if (node.isOptional() || node.getUpperBound() != 1) {
                widenImageWithLines(LINE_BASE_LENGTH);
            }

            if (node.isOptional()) {
                drawSkipLine();
            }
            if (node.getUpperBound() != 1) {
                drawRepeatLine(node.getLowerBound(), node.getUpperBound());
            }
        }

        /**
         * Creates an ImageNode from the given data
         * @param image The image of this node
         * @param lineHeight The line height in the image
         */
        private ImageNode(BufferedImage image, int lineHeight) {
            this.image = image;
            this.lineHeight = lineHeight;
        }

        /**
         * Creates a rounded rectangle with the given text inside <br>
         * The line will be placed in the center <br>
         * At each end there will be {@link #LINE_BASE_LENGTH} pixels of line at each end
         * @param text Text to be displayed
         * @param nodeColor Color of the rectangle
         */
        private void createTextNode(String text, Color nodeColor) {
            int w = CONTENT_FONT_METRICS.stringWidth(text);
            int h = CONTENT_FONT_METRICS.getHeight();
            image = ImageUtil.createImage(w + 2 * LINE_BASE_LENGTH + 2 * TEXT_SIDE_ROOM, CONTENT_FONT_METRICS.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            // draw rectangle
            g2d.setPaint(nodeColor);
            g2d.fillRoundRect(LINE_BASE_LENGTH, 0, w + 2*TEXT_SIDE_ROOM, CONTENT_FONT_METRICS.getHeight(), h / 3, h / 3);
            // add text
            g2d.setPaint(TEXT_COLOR);
            g2d.setFont(CONTENT_FONT);
            g2d.drawString(text, LINE_BASE_LENGTH + TEXT_SIDE_ROOM, CONTENT_FONT_METRICS.getAscent());
            // draw lines
            lineHeight = h / 2;
            drawEndLines(LINE_BASE_LENGTH);
            g2d.dispose();
        }

        /**
         * Creates a rounded rectangle with the given node as an image inside <br>
         * The line will be placed in the center <br>
         * At each end there will be {@link #LINE_BASE_LENGTH} pixels of line at each end
         * @param content Node to be displayed inside the rectangle
         * @param thisGroupNumber Number to be displayed over the rectangle
         */
        private void createGroupNode(ImageNode content, int thisGroupNumber) {
            image = ImageUtil.createImage(content.image.getWidth() + 2 * LINE_BASE_LENGTH + 2 * GROUP_SIDE_SPACING, content.image.getHeight() + 2 *GROUP_SIDE_SPACING, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            // draw rectangle
            g2d.setPaint(GROUP_NODE_COLOR);
            g2d.fillRoundRect(LINE_BASE_LENGTH, 0, image.getWidth() - 2 * LINE_BASE_LENGTH, image.getHeight(), image.getHeight() / 3, image.getHeight() / 3);
            // add content
            g2d.drawImage(content.image, LINE_BASE_LENGTH + GROUP_SIDE_SPACING, GROUP_SIDE_SPACING, null);
            // draw lines
            lineHeight = content.lineHeight + GROUP_SIDE_SPACING;
            drawEndLines(LINE_BASE_LENGTH + GROUP_SIDE_SPACING);
            g2d.dispose();
            addInfoText("Group " + thisGroupNumber, true);
        }

        /**
         * Draws the given nodes on top of each other. Each will be centered in the image of this node. <br>
         * They will have a vertical space of {@link #NODE_STACKING_SPACING} pixels between them <br>
         * The outgoing line will be centered vertically in the image
         * @param nodes Nodes to stack
         */
        private void stackNodes(ImageNode... nodes) {
            int sideSpace = 3 * LINE_BASE_LENGTH;
            // determine necessary height and width
            int w = 0;
            int h = 0;
            for (ImageNode node : nodes) {
                w = Math.max(w, node.image.getWidth());
                h += node.image.getHeight();
            }
            int totalW = w +  2 * sideSpace;
            h += (nodes.length - 1) * NODE_STACKING_SPACING;

            image = ImageUtil.createImage(totalW , h, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            int y = 0;
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            lineHeight = h / 2;

            for (ImageNode node : nodes) {
                // draw the current node
                g2d.drawImage(node.image, getPositionForCenterPlacement(totalW, node.image.getWidth()), y, null);
                int lH = y + node.lineHeight;
                // extends its lines, if it wasn't the widest node
                drawSymmetricLines(sideSpace - STROKE_WIDTH * 2, (w - node.image.getWidth()) / 2 + 2 * STROKE_WIDTH, lH);
                // draw components for connection to out going lines on both end
                if (lH == lineHeight) {
                    drawEndLines(sideSpace);
                } else if (lH < lineHeight) {
                    // above middle
                    g2d.drawArc(sideSpace / 2, lH, sideSpace / 2, sideSpace / 2, 90, 90); // left arc
                    g2d.drawArc(totalW - sideSpace, lH, sideSpace / 2, sideSpace / 2, 0, 90); // right arc
                } else {
                    // below middle
                    g2d.drawArc(sideSpace / 2, lH - sideSpace / 2, sideSpace / 2, sideSpace / 2, -90, -90); // left arc
                    g2d.drawArc(totalW - sideSpace, lH - sideSpace / 2, sideSpace / 2, sideSpace / 2, 0, -90); // right arc

                }
                y += node.image.getHeight() + NODE_STACKING_SPACING;
            }
            int firstLH = nodes[0].lineHeight;
            int lastLH = y - nodes[nodes.length - 1].image.getHeight() - NODE_STACKING_SPACING + nodes[nodes.length - 1].lineHeight;
            // left side
            g2d.drawLine(sideSpace / 2, firstLH + sideSpace / 4, sideSpace / 2, lineHeight - sideSpace / 2 + 2 * STROKE_WIDTH); // top line down
            g2d.drawLine(sideSpace / 2, lastLH - sideSpace / 4, sideSpace / 2, lineHeight + sideSpace / 2 - 2 * STROKE_WIDTH); // bottom line up
            g2d.drawArc(0, lineHeight - sideSpace / 2, sideSpace / 2,  sideSpace/ 2, -90, 90); // top arc
            g2d.drawArc(0, lineHeight, sideSpace / 2,  sideSpace/ 2, 90, -90); // bottom arc

            // right side
            g2d.drawLine(totalW - sideSpace / 2, firstLH + sideSpace / 4, totalW - sideSpace / 2, lineHeight - sideSpace / 2 + 2 * STROKE_WIDTH); // top line down
            g2d.drawLine(totalW - sideSpace / 2, lastLH - sideSpace / 4, totalW - sideSpace / 2, lineHeight + sideSpace / 2 - 2 * STROKE_WIDTH); // bottom line down
            g2d.drawArc(totalW - sideSpace / 2, lineHeight - sideSpace / 2, sideSpace / 2,  sideSpace/ 2, 180, 90); // top arc
            g2d.drawArc(totalW - sideSpace / 2, lineHeight, sideSpace / 2,  sideSpace/ 2, 180, -90); // bottom arc

            // connect arc to end of image
            drawEndLines(2 * STROKE_WIDTH + 1);
            g2d.dispose();
        }

        /**
         * Adds a text either above or below the current image <br>
         * The text will be centered in the image
         * @param text Text to display
         * @param overImage If true, the text will be displayed above the current image, below otherwise
         */
        private void addInfoText(String text, boolean overImage) {
            // determine whether image is wide enough to display text, or if it needs to be wider
            boolean textWiderThanImage = CONTENT_FONT_METRICS.stringWidth(text) + LINE_BASE_LENGTH > image.getWidth();

            BufferedImage textImage = ImageUtil.createImage(textWiderThanImage ? CONTENT_FONT_METRICS.stringWidth(text) + 2 * LINE_BASE_LENGTH: image.getWidth(), image.getHeight() + INFO_FONT_METRICS.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = textImage.createGraphics();
            // draw image
            g2d.drawImage(image, getPositionForCenterPlacement(textImage.getWidth(), image.getWidth()), overImage ? INFO_FONT_METRICS.getHeight() : 0, null);

            // add text
            g2d.setPaint(TEXT_COLOR);
            g2d.setFont(INFO_FONT);
            g2d.drawString(text, getPositionForCenterPlacement(textImage.getWidth(), INFO_FONT_METRICS.stringWidth(text)), overImage ? INFO_FONT_METRICS.getAscent() : textImage.getHeight() - INFO_FONT_METRICS.getDescent());
            g2d.dispose();

            // extend lines if necessary, because image was widened
            lineHeight += (INFO_FONT_METRICS.getHeight()) * (overImage ? 1 : 0);
            int lineLength = (textImage.getWidth() - image.getWidth()) / 2;
            image = textImage;
            if (textWiderThanImage) {
                drawEndLines(lineLength);
            }
        }

        /**
         * Draws a line following the natural direction of the lines, going outside above the image
         */
        private void drawSkipLine() {
            BufferedImage newImage = ImageUtil.createImage(image.getWidth(), image.getHeight() + STROKE_WIDTH + OUTSIDE_LINE_SPACING, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();

            // add old image
            g2d.drawImage(image, 0, STROKE_WIDTH + OUTSIDE_LINE_SPACING, null);

            lineHeight += STROKE_WIDTH + OUTSIDE_LINE_SPACING;
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            int arcH = (lineHeight - STROKE_WIDTH);
            int arcW = LINE_BASE_LENGTH;
            // draw middle line
            g2d.drawLine(2 * arcW - STROKE_WIDTH, STROKE_WIDTH, newImage.getWidth() - 2 * arcW + STROKE_WIDTH, STROKE_WIDTH);
            // draw left arc
            g2d.drawArc(0, lineHeight - arcH, arcW, arcH, 270, 90); // bottom
            g2d.drawArc(arcW, STROKE_WIDTH, arcW, arcH, 90, 90); // top

            // draw right arc
            g2d.drawArc(newImage.getWidth() - arcW - STROKE_WIDTH, lineHeight - arcH, arcW, arcH, 180, 90); // bottom
            g2d.drawArc(newImage.getWidth() - 2 * arcW - STROKE_WIDTH, STROKE_WIDTH, arcW, arcH, 0, 90); // top

            g2d.dispose();
            image = newImage;
        }

        /**
         * Draws a loop below the image going out from the line <br>
         * Add text if necessary
         * @param minAmount Minimum amount of repetitions the node goes through
         * @param maxAmount Maximum amount of repetitions the node goes through, -1 means infinite
         */
        private void drawRepeatLine(int minAmount, int maxAmount) {
            boolean addTextBelow = minAmount > 1 || maxAmount > 1;
            BufferedImage newImage = ImageUtil.createImage(image.getWidth(), image.getHeight() + STROKE_WIDTH + OUTSIDE_LINE_SPACING, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();

            // add old image
            g2d.drawImage(image, 0, 0, null);

            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            // draw middle line
            g2d.drawLine(2 * LINE_BASE_LENGTH - 2 * STROKE_WIDTH, image.getHeight() + OUTSIDE_LINE_SPACING, image.getWidth() - 2 * LINE_BASE_LENGTH + 2 * STROKE_WIDTH, image.getHeight()+ OUTSIDE_LINE_SPACING);
            // draw left half ellipse
            g2d.drawArc(STROKE_WIDTH, lineHeight,  2 * LINE_BASE_LENGTH, image.getHeight() + OUTSIDE_LINE_SPACING - lineHeight, 90, 180);
            // draw right half ellipse
            g2d.drawArc(image.getWidth() - STROKE_WIDTH - 2 * LINE_BASE_LENGTH, lineHeight, 2 * LINE_BASE_LENGTH, image.getHeight() + OUTSIDE_LINE_SPACING - lineHeight, 270, 180);
            g2d.dispose();
            image = newImage;

            // add text below if necessary
            if (addTextBelow) {
                String text;
                if (minAmount > 1) {
                    if (maxAmount > 1) {
                        text = minAmount + "-" + maxAmount;
                    } else {
                        text = minAmount + "+";
                    }
                } else {
                    text = "up to " + maxAmount;
                }

                addInfoText(text, false);
            }
        }

        /**
         * Appends the image of another node at the back this nodes image <br>
         * It ensures that the lines will be connected
         * @param node Node to append
         */
        private void appendImage(ImageNode node) {
            int width = this.image.getWidth() + node.image.getWidth();

            // determine needed height
            int spaceAboveLine = Math.max(this.spaceAboveLine(), node.spaceAboveLine());
            int height = spaceAboveLine + Math.max(this.spaceBelowLine(), node.spaceBelowLine());

            BufferedImage mergedImage = ImageUtil.createImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = mergedImage.createGraphics();
            // add the two images
            g2d.drawImage(this.image, 0, spaceAboveLine - this.spaceAboveLine(), null);
            g2d.drawImage(node.image, this.image.getWidth(), spaceAboveLine - node.spaceAboveLine(), null);
            g2d.dispose();

            lineHeight = spaceAboveLine;
            image = mergedImage;
        }

        /**
         * Extends the image by the given length in both directions horizontally and draws the line to the end of the image
         * @param length Pixels to extend by
         */
        private void widenImageWithLines(int length) {
            // create wider image
            BufferedImage newImage = ImageUtil.createImage(image.getWidth() + 2 * length, image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = newImage.createGraphics();
            g2d.drawImage(image, length, 0, null);
            image = newImage;
            //draw lines
            drawEndLines(length);
        }

        /**
         * Draws lines at the given length from both horizontal ends of the image towards the middle
         * @param lineLength Length of the lines
         */
        private void drawEndLines(int lineLength) {
            drawSymmetricLines(0, lineLength, lineHeight);
        }

        /**
         * Draws lines at the given length inset from both horizontal ends by a given amount of the image towards the middle
         * @param inset Pixels to take space from edge of image
         * @param lineLength Length of lines
         * @param height Height to place lines at
         */
        private void drawSymmetricLines(int inset, int lineLength, int height) {
            Graphics2D g2d = image.createGraphics();
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            // draw left line
            g2d.drawLine(inset, height, lineLength - 1 + inset, height);
            // draw right line
            g2d.drawLine(image.getWidth() - inset - lineLength + 1, height, image.getWidth() - inset, height);
        }

        /**
         * Returns the amount of pixels above the line
         * @return Pixels above the line
         */
        private int spaceAboveLine() {
            return lineHeight;
        }

        /**
         * Returns the amount of pixels below the line
         * @return Pixels below the line
         */
        private int spaceBelowLine() {
            return image.getHeight() - lineHeight;
        }

        /**
         * Calculates the position the draw methods of {@link Graphics2D} need to be given to center inside a parent container
         * @param containerWidth Width of parent container
         * @param elementWidth Width of the element to center
         * @return Position to center element in parent container
         */
        private static int getPositionForCenterPlacement(int containerWidth, int elementWidth) {
            return (containerWidth - elementWidth) / 2;
        }

    }

    /**
     * Generates an image of the generic starting node of each graph
     * @return Node containing information for drawing starting node image
     */
    private static ImageNode drawStartingNode() {
        int size = CONTENT_FONT_METRICS.getHeight();
        BufferedImage image = ImageUtil.createImage(size + 2 * LINE_BASE_LENGTH, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // draw line through middle, used as both the outgoing line on the left and the shaft of the arrow
        g2d.setPaint(LINE_COLOR);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH));
        g2d.drawLine(0, size / 2, image.getWidth(), size / 2);

        // draw circle
        g2d.setPaint(END_NODE_COLOR);
        g2d.fillOval((int)(1.5 * LINE_BASE_LENGTH) + 2, 2, size - 4, size - 4);

        // draw arrow tip
        g2d.setPaint(LINE_COLOR);
        g2d.drawLine( (int)(1.5 * LINE_BASE_LENGTH), size / 2, LINE_BASE_LENGTH, size / 2 + LINE_BASE_LENGTH / 2);
        g2d.drawLine((int)(1.5 * LINE_BASE_LENGTH), size / 2, LINE_BASE_LENGTH, size / 2 - LINE_BASE_LENGTH / 2);
        g2d.dispose();

        return new ImageNode(image, size / 2);
    }

    /**
     * Generates an image of the generic end node of each graph
     * @return Node containing information for drawing end node image
     */
    private static ImageNode drawEndingNode() {
        int size = CONTENT_FONT_METRICS.getHeight();
        int innerSize = size / 2;
        BufferedImage image = ImageUtil.createImage(size + (int)(0.5 * LINE_BASE_LENGTH), size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // draw line going out to the right
        g2d.setPaint(LINE_COLOR);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH));
        g2d.drawLine(0, size / 2, (int)(0.5 * LINE_BASE_LENGTH), size / 2);

        // draw outer  circle
        g2d.setPaint(END_NODE_COLOR);
        g2d.drawOval((int)(0.5 * LINE_BASE_LENGTH) + 2, 2, size - 4, size - 4);
        //draw inner circle
        g2d.fillOval((int)(0.5 * LINE_BASE_LENGTH) + (size-innerSize) / 2, (size-innerSize) / 2, innerSize, innerSize);
        g2d.dispose();

        return new ImageNode(image, size / 2);
    }

    /**
     * Gets the {@link FontMetrics} for the given font, which are used to determine height and width of {@link String Strings}
     * @param font Font to get metric for
     * @return FontMetric of the Font
     */
    private static FontMetrics getFontMetrics(Font font) {
        Graphics2D g2d = ImageUtil.createImage(1,1, BufferedImage.TYPE_INT_ARGB).createGraphics();
        g2d.setFont(font);
        return g2d.getFontMetrics();
    }

    /**
     * Creates a new JBColor with the given alpha values for the light and dark theme
     * @param color Base color
     * @param lightAlpha Alpha-value for light theme
     * @param darkAlpha Alpha-value for dark theme
     * @return Transparent color
     */
    private static JBColor makeTransparent(JBColor color, int lightAlpha, int darkAlpha) {
        return new JBColor(new Color(color.brighter().getRed(), color.brighter().getGreen(), color.brighter().getBlue(), lightAlpha), new Color(color.darker().getRed(), color.darker().getGreen(), color.darker().getBlue(), darkAlpha));
    }

    /**
     * Private constructor for Utility class
     */
    private RegExImageFactory() {}

}
