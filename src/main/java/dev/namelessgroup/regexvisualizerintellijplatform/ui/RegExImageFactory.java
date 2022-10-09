package dev.namelessgroup.regexvisualizerintellijplatform.ui;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.ImageUtil;
import dev.namelessgroup.regexvisualizerintellijplatform.model.*;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.settings.RegExImageQualitySettings;
import org.apache.commons.lang.NotImplementedException;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for transforming a graph given by {@link org.intellij.lang.regexp.RegExpParser the parser} into an {@link Image}.
 * @version 1.0
 */
public final class RegExImageFactory {

    // Font data
    private final static Font DEFAULT_CONTENT_FONT = new Font("JetBrains Mono", Font.PLAIN, 18);
    private static Font CONTENT_FONT = new Font("JetBrains Mono", Font.PLAIN, 18);
    private static FontMetrics CONTENT_FONT_METRICS = getFontMetrics(CONTENT_FONT);
    private final static Font DEFAULT_INFO_FONT = new Font("JetBrains Mono", Font.PLAIN, 14);
    private static Font INFO_FONT = new Font("JetBrains Mono", Font.PLAIN, 14);
    private static FontMetrics INFO_FONT_METRICS = getFontMetrics(INFO_FONT);

    // Dimensions
    private static final int DEFAULT_LINE_BASE_LENGTH = 8;
    private static final int DEFAULT_TEXT_SIDE_ROOM = 7;
    private static final int DEFAULT_STROKE_WIDTH = 2;
    private static final int DEFAULT_GROUP_SIDE_SPACING = 8;
    private static final int DEFAULT_NODE_STACKING_SPACING = 4;
    private static final int DEFAULT_OUTSIDE_LINE_SPACING = 4;
    private static int LINE_BASE_LENGTH = 8;
    private static int TEXT_SIDE_ROOM = 7;
    private static int STROKE_WIDTH = 2;
    private static int GROUP_SIDE_SPACING = 8;
    private static int NODE_STACKING_SPACING = 4;
    private static int OUTSIDE_LINE_SPACING = 4;

    // Colors
    private static Color NODE_COLOR = makeTransparent(JBColor.ORANGE, 200);
    private static Color OPTION_NODE_COLOR = makeTransparent(JBColor.RED, 200);
    private static Color GROUP_NODE_COLOR = makeTransparent(JBColor.YELLOW, 40);
    private static Color LINE_COLOR = JBColor.GRAY;
    private static Color TEXT_COLOR = JBColor.BLACK;
    private static Color END_NODE_COLOR = JBColor.BLACK;
    private static RegExImageQualitySettings QUALITY = RegExImageQualitySettings.MEDIUM;

    /**
     * Stores the runtime variables for calculating the size and position of the nodes and image dimensions
     */
    private static class FactoryRuntimeData {
        private static int GROUP_COUNT = 1;
        private int TOTAL_WIDTH = 0;
        private int MAX_BELOW_LINE = 0;
        private int MAX_LINE_HEIGHT = 0;
        private int CURRENT_X = 0;
        private final List<ImageNode> nodes = new ArrayList<>();

        /**
         *
         * @param w width of added node
         * @param h height of added node
         * @param lH line height of added node
         */
        private void updateValues(int w, int h, int lH) {
            TOTAL_WIDTH += w;
            MAX_BELOW_LINE = Math.max(MAX_BELOW_LINE, h - lH);
            MAX_LINE_HEIGHT = Math.max(MAX_LINE_HEIGHT, lH);
            CURRENT_X += w;
        }

        public int getMaxHeight() {
            return MAX_LINE_HEIGHT + MAX_BELOW_LINE;
        }
    }

    /**
     * Creates an image of the given graph given by {@link org.intellij.lang.regexp.RegExpParser the parser}
     * @param nodes Graph to transform
     * @return Image of the graph
     */
    public static @NotNull Image createImage(List<Node> nodes) {
        applyScaling();
        // calculate data for drawing all nodes
        FactoryRuntimeData factoryRuntimeData = new FactoryRuntimeData();
        calcEndNodes(factoryRuntimeData);
        imageFromNodeStructure(nodes, factoryRuntimeData);

        BufferedImage image = ImageUtil.createImage(factoryRuntimeData.TOTAL_WIDTH, factoryRuntimeData.getMaxHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        applySettings(g);

        // draw nodes
        for (ImageNode node : factoryRuntimeData.nodes) {
            node.draw(g);
        }
        drawEndNodes(g, factoryRuntimeData);
        g.dispose();

        // scaling ensures the image is not as pixelated
        if (QUALITY.getScalingFactor() == 1) {
            return image;
        }
        return image.getScaledInstance(image.getWidth() / QUALITY.getScalingFactor(), image.getHeight() / QUALITY.getScalingFactor(), Image.SCALE_SMOOTH);
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
    public static @NotNull Image createImage(List<Node> nodes, Color nodeColor, Color optionNodeColor, Color groupNodeColor, Color lineColor, Color textColor, Color endNodeColor, RegExImageQualitySettings quality) {
        QUALITY = quality;
        NODE_COLOR = nodeColor;
        OPTION_NODE_COLOR = optionNodeColor;
        GROUP_NODE_COLOR = groupNodeColor;
        LINE_COLOR = lineColor;
        TEXT_COLOR = textColor;
        END_NODE_COLOR = endNodeColor;

        return createImage(nodes);
    }

    private static void applyScaling() {
        LINE_BASE_LENGTH = DEFAULT_LINE_BASE_LENGTH * QUALITY.getScalingFactor();
        TEXT_SIDE_ROOM = DEFAULT_TEXT_SIDE_ROOM * QUALITY.getScalingFactor();
        STROKE_WIDTH = DEFAULT_STROKE_WIDTH * QUALITY.getScalingFactor();
        GROUP_SIDE_SPACING = DEFAULT_GROUP_SIDE_SPACING * QUALITY.getScalingFactor();
        NODE_STACKING_SPACING = DEFAULT_NODE_STACKING_SPACING * QUALITY.getScalingFactor();
        OUTSIDE_LINE_SPACING = DEFAULT_OUTSIDE_LINE_SPACING * QUALITY.getScalingFactor();

        CONTENT_FONT = DEFAULT_CONTENT_FONT.deriveFont((float) (DEFAULT_CONTENT_FONT.getSize() * QUALITY.getScalingFactor()));
        CONTENT_FONT_METRICS = getFontMetrics(CONTENT_FONT);
        INFO_FONT = DEFAULT_INFO_FONT.deriveFont((float) (DEFAULT_INFO_FONT.getSize() * QUALITY.getScalingFactor()));
        INFO_FONT_METRICS = getFontMetrics(INFO_FONT);
    }

    private static void applySettings(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, QUALITY.getAntialiasing());
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, QUALITY.getTextAntialiasing());
        g.setRenderingHint(RenderingHints.KEY_RENDERING, QUALITY.getRendering());
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, QUALITY.getStrokeControl());
        g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, QUALITY.getFractionalMetrics());
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, QUALITY.getAlphaInterpolation());
    }

    /**
     * Creates {@link ImageNode ImageNodes} from the given List and stores them and their date in the given {@link FactoryRuntimeData}
     * @param nodes Nodes to transform
     * @param f Runtime data to store the data in
     */
    private static void imageFromNodeStructure(List<Node> nodes, FactoryRuntimeData f) {
        for (Node n : nodes) {
            new ImageNode(n, f);
        }
    }

    /**
     * Representation of a {@link Node}
     */
    private static class ImageNode {
        protected int lineHeight;

        protected Node node;
        protected int x;
        protected int h;
        protected int w;
        protected FactoryRuntimeData factoryRuntimeData;

        /**
         * Generates an image from the contents of the node
         * @param node Node to make image of
         * @param factoryRuntimeData Runtime data to store the data in
         */
        private ImageNode(Node node, FactoryRuntimeData factoryRuntimeData) {
            this.node = node;
            this.factoryRuntimeData = factoryRuntimeData;
            if (node instanceof GroupNode) {
                calcGroupNode((GroupNode) node, !(node instanceof NonCapturingGroupNode));
                return;
            } else if (node instanceof OrNode) {
                calcStackNodes((OrNode)node);
            } else if (node instanceof OneOfNode) {
                calcTextNode(getOptionNodeText());
                calcTextInfo("One of", true);
            } else if (node instanceof NoneOfNode) {
                calcTextNode(getOptionNodeText());
                calcTextInfo("None of", true);
            } else {
                calcTextNode(node.getContent());
            }

            // Extend lines if optional or repeating to get their lines in
            if (node.isOptional() || node.getUpperBound() != 1) {
                calcWidenImage(LINE_BASE_LENGTH);
            }

            if (node.isOptional()) {
                calcSkipLine();
            }
            if (node.getUpperBound() != 1) {
                calcRepeatLine(node.getLowerBound(), node.getUpperBound());
            }
        }

        /**
         * Constructor used for inheritance
         */
        private ImageNode() { }

        /**
         * Draws the node on the {@link Image} of the {@link Graphics2D}
         * @param g Graphic  to use for drawing
         */
        private void draw(Graphics2D g) {
            if (node instanceof GroupNode) {
                if (!(node instanceof NonCapturingGroupNode)) {
                    int thisGroup = FactoryRuntimeData.GROUP_COUNT++;
                    drawGroupNode(g, factoryRuntimeData, thisGroup);
                } else {
                    drawNonCapturingGroupNode(g, factoryRuntimeData);
                }
            } else if (node instanceof OrNode) {
                drawStackNodes(g, factoryRuntimeData);
            } else if (node instanceof OneOfNode) {
                drawTextNode(g, factoryRuntimeData, getOptionNodeText(), OPTION_NODE_COLOR);
                drawInfoText(g, factoryRuntimeData,"One of", true);
            } else if (node instanceof NoneOfNode) {
                drawTextNode(g, factoryRuntimeData, getOptionNodeText(), OPTION_NODE_COLOR);
                drawInfoText(g, factoryRuntimeData, "None of", true);
            } else {
                drawTextNode(g, factoryRuntimeData, node.getContent(), NODE_COLOR);
            }

            if (node.isOptional()) {
                drawSkipLine(g, factoryRuntimeData);
            }
            if (node.getUpperBound() != 1) {
                drawRepeatLine(g, factoryRuntimeData, node.getLowerBound(), node.getUpperBound());
            }
        }

        /**
         * Calculate values for a text node
         * @param content Text of node
         */
        private void calcTextNode(String content) {
            w = CONTENT_FONT_METRICS.stringWidth(content) + 2 * TEXT_SIDE_ROOM + 2 * LINE_BASE_LENGTH;
            h = CONTENT_FONT_METRICS.getHeight();
            lineHeight = h / 2;
            x = factoryRuntimeData.CURRENT_X;

            factoryRuntimeData.updateValues(w, h, lineHeight);
            factoryRuntimeData.nodes.add(this);
        }

        /**
         * Draws the text node onto the {@link Image} of the {@link Graphics2D}
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         * @param text Text to draw on node
         * @param nodeColor Color of node
         */
        private void drawTextNode(Graphics2D g2d, FactoryRuntimeData f, String text, Color nodeColor) {
            int y = f.MAX_LINE_HEIGHT - CONTENT_FONT_METRICS.getHeight() / 2;
            int thisW = CONTENT_FONT_METRICS.stringWidth(text) + 2 * TEXT_SIDE_ROOM;
            int thisX = x + getPositionForCenterPlacement(w, thisW);
            int thisArc = CONTENT_FONT_METRICS.getHeight() / 3;

            // draw rectangle
            g2d.setPaint(nodeColor);
            g2d.fillRoundRect(thisX, y, thisW, CONTENT_FONT_METRICS.getHeight(), thisArc, thisArc);
            // add text
            g2d.setPaint(TEXT_COLOR);
            g2d.setFont(CONTENT_FONT);
            g2d.drawString(text, thisX + TEXT_SIDE_ROOM, y + CONTENT_FONT_METRICS.getAscent());
            // draw lines
            drawEndLines(g2d, f, (w - thisW) / 2);
        }

        /**
         * Calculate values for a group node
         * @param node Node to calculate values for
         * @param capturing Whether the group is capturing or not
         */
        protected void calcGroupNode(GroupNode node, boolean capturing) {
            new GroupImageNode(node, factoryRuntimeData);
        }

        /**
         * Draws the group node onto the {@link Image} of the {@link Graphics2D}
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         * @param number Number of the group
         */
        protected void drawGroupNode(Graphics2D g2d, FactoryRuntimeData f, int number) {
            throw new NotImplementedException();
        }

        /**
         * Draws the non-capturing group node onto the {@link Image} of the {@link Graphics2D}
         * @param g Graphic to use for drawing
         * @param f Factory to get data from
         */
        protected void drawNonCapturingGroupNode(Graphics2D g, FactoryRuntimeData f) {
            throw new NotImplementedException();
        }

        /**
         * Calculate values for an OrNode
         * @param node Node to calculate values for
         */
        protected void calcStackNodes(OrNode node) {
            new StackImageNode(node, factoryRuntimeData);
        }

        /**
         * Draws the OrNode onto the {@link Image} of the {@link Graphics2D}
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         */
        protected void drawStackNodes(Graphics2D g2d, FactoryRuntimeData f) {
            throw new NotImplementedException();
        }

        /**
         * Calculate values for adding information text to a node
         * @param text Text to add
         * @param overImage Whether the text should be drawn over the image or under it
         */
        protected void calcTextInfo(String text, boolean overImage) {
            h += INFO_FONT_METRICS.getHeight();

            // If text wider than current image
            int dif = CONTENT_FONT_METRICS.stringWidth(text) + LINE_BASE_LENGTH - w;
            if (dif > 0) {
                w += dif;
            } else {
                dif = 0;
            }

            if (overImage) {
                lineHeight += INFO_FONT_METRICS.getHeight();
            }

            factoryRuntimeData.updateValues(dif, h, lineHeight);
        }

        /**
         * Adds a text either above or below the current content onto the {@link Image} of the {@link Graphics2D} <br>
         * The text will be centered in the image
         * @param text Text to display
         * @param overImage If true, the text will be displayed above the current image, below otherwise
         */
        protected void drawInfoText(Graphics2D g2d, FactoryRuntimeData f, String text, boolean overImage) {
            int y = overImage ?
                    f.MAX_LINE_HEIGHT - lineHeight + INFO_FONT_METRICS.getAscent()
                    : f.MAX_LINE_HEIGHT - lineHeight + h - INFO_FONT_METRICS.getDescent();
            // add text
            g2d.setPaint(TEXT_COLOR);
            g2d.setFont(INFO_FONT);
            g2d.drawString(text, x + getPositionForCenterPlacement(w, INFO_FONT_METRICS.stringWidth(text)), y);
        }

        /**
         * Calculate values for a skip line
         */
        protected void calcSkipLine() {
            h += STROKE_WIDTH + OUTSIDE_LINE_SPACING;
            lineHeight += STROKE_WIDTH + OUTSIDE_LINE_SPACING;

            factoryRuntimeData.updateValues(0, h, lineHeight);
        }

        /**
         * Draws a skip line onto the {@link Image} of the {@link Graphics2D}
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         */
        private void drawSkipLine(Graphics2D g2d, FactoryRuntimeData f) {
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            int arcH = (lineHeight - STROKE_WIDTH);
            int arcW = LINE_BASE_LENGTH;
            int y = f.MAX_LINE_HEIGHT - lineHeight;
            // draw middle line
            g2d.drawLine(x + 2 * arcW - STROKE_WIDTH, y + STROKE_WIDTH, x + w - 2 * arcW + STROKE_WIDTH, y + STROKE_WIDTH);
            // draw left arc
            g2d.drawArc(x, f.MAX_LINE_HEIGHT - arcH, arcW, arcH, 270, 90); // bottom
            g2d.drawArc(x + arcW, y + STROKE_WIDTH, arcW, arcH, 90, 90); // top

            // draw right arc
            g2d.drawArc(x + w - arcW - STROKE_WIDTH, f.MAX_LINE_HEIGHT - arcH, arcW, arcH, 180, 90); // bottom
            g2d.drawArc(x + w - 2 * arcW - STROKE_WIDTH, y + STROKE_WIDTH, arcW, arcH, 0, 90); // top
        }

        /**
         * Calculates values for a repeat line
         * @param minAmount Minimum amount of repetitions
         * @param maxAmount Maximum amount of repetitions
         */
        protected void calcRepeatLine(int minAmount, int maxAmount) {
            h += STROKE_WIDTH + OUTSIDE_LINE_SPACING;

            factoryRuntimeData.updateValues(0, h, lineHeight);
            String text =  getAmountText(minAmount, maxAmount);
            if (text != null) {
                calcTextInfo(text, false);
            }
        }

        /**
         * Draws a loop below the image going out from the line <br>
         * Add text if necessary
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         * @param minAmount Minimum amount of repetitions the node goes through
         * @param maxAmount Maximum amount of repetitions the node goes through, -1 means infinite
         */
        private void drawRepeatLine(Graphics2D g2d, FactoryRuntimeData f, int minAmount, int maxAmount) {
            String text = getAmountText(minAmount, maxAmount);
            int thisY = f.MAX_LINE_HEIGHT - lineHeight + h - STROKE_WIDTH - (text != null ? INFO_FONT_METRICS.getHeight() : 0);
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            // draw middle line
            g2d.drawLine(x + 2 * LINE_BASE_LENGTH - 2 * STROKE_WIDTH, thisY, x + w - 2 * LINE_BASE_LENGTH + 2 * STROKE_WIDTH, thisY);
            // draw left half ellipse
            g2d.drawArc(x + STROKE_WIDTH, f.MAX_LINE_HEIGHT,  2 * LINE_BASE_LENGTH, thisY - f.MAX_LINE_HEIGHT, 90, 180);
            // draw right half ellipse
            g2d.drawArc(x + w - STROKE_WIDTH - 2 * LINE_BASE_LENGTH, f.MAX_LINE_HEIGHT, 2 * LINE_BASE_LENGTH, thisY - f.MAX_LINE_HEIGHT, 270, 180);

            if (text != null) {
                drawInfoText(g2d, f, text, false);
            }
        }

        /**
         * Determines the info text for a repeat line
         * @param minAmount Minimum amount of repetitions
         * @param maxAmount Maximum amount of repetitions
         * @return Returns the text to display
         */
        private String getAmountText(int minAmount, int maxAmount) {
            if (minAmount <= 1 && maxAmount <= 1) {
                return null;
            }
            if (minAmount == maxAmount) {
                return minAmount + "x";
            }
            if (minAmount > 1) {
                if (maxAmount > 1) {
                    return minAmount + "-" + maxAmount;
                } else {
                    return minAmount + "+";
                }
            } else {
                return  "up to " + maxAmount;
            }
        }

        /**
         * Determines the text for a node of options
         * @return Returns the text to display
         */
        private String getOptionNodeText() {
            return String.join(" ", node.getContent().split(""));
        }

        protected void calcWidenImage(int width) {
            w += 2 * width;
            factoryRuntimeData.updateValues(2 * width, h, lineHeight);
        }

        /**
         * Draws lines at the given length from both horizontal ends of the image towards the middle
         * @param g2d Graphic to use for drawing
         * @param f Factory to get data from
         * @param lineLength Length of the lines
         */
        protected void drawEndLines(Graphics2D g2d, FactoryRuntimeData f, int lineLength) {
            drawSymmetricLines(g2d,0, lineLength, f.MAX_LINE_HEIGHT);
        }

        /**
         * Draws lines at the given length inset from both horizontal ends of the node
         * @param g2d Graphic to use for drawing
         * @param inset Pixels to take space from edge of image
         * @param lineLength Length of lines
         * @param height Height to place lines at
         */
        protected void drawSymmetricLines(Graphics2D g2d,int inset, int lineLength, int height) {
            g2d.setPaint(LINE_COLOR);
            g2d.setStroke(new BasicStroke(STROKE_WIDTH));
            // draw left line
            g2d.drawLine(x + inset, height, x + lineLength - 1 + inset, height);
            // draw right line
            g2d.drawLine(x + w - inset - lineLength + 1, height, x + w - inset, height);
        }

        /**
         * Calculates the position the draw methods of {@link Graphics2D} need to be given to center inside a parent container
         * @param containerWidth Width of parent container
         * @param elementWidth Width of the element to center
         * @return Position to center element in parent container
         */
        protected static int getPositionForCenterPlacement(int containerWidth, int elementWidth) {
            return (containerWidth - elementWidth) / 2;
        }

        protected void setFactory(FactoryRuntimeData f) {
            this.factoryRuntimeData = f;
        }

    }

    /**
     * Representation of a {@link GroupNode}
     */
    private static class GroupImageNode extends ImageNode {

        private FactoryRuntimeData contentFactoryRuntimeData;

        private GroupImageNode(GroupNode node, FactoryRuntimeData factoryRuntimeData) {
            this.node = node;
            this.factoryRuntimeData = factoryRuntimeData;
            calcGroupNode(node, !(node instanceof NonCapturingGroupNode));

            // Extend lines if optional or repeating to get their lines in
            if (node.isOptional() || node.getUpperBound() != 1) {
                calcWidenImage(LINE_BASE_LENGTH);
            }


            if (node.isOptional()) {
                calcSkipLine();
            }
            if (node.getUpperBound() != 1) {
                calcRepeatLine(node.getLowerBound(), node.getUpperBound());
            }
        }

        @Override
        protected void calcGroupNode(GroupNode node, boolean capturing) {
            contentFactoryRuntimeData = new FactoryRuntimeData();
            imageFromNodeStructure(node.getNodes(), contentFactoryRuntimeData);

            x = factoryRuntimeData.CURRENT_X;
            w = contentFactoryRuntimeData.TOTAL_WIDTH + (capturing ? 2 * GROUP_SIDE_SPACING + 2 * LINE_BASE_LENGTH : 0);
            h = contentFactoryRuntimeData.getMaxHeight() + (capturing ? 2 * GROUP_SIDE_SPACING : 0);
            lineHeight = contentFactoryRuntimeData.MAX_LINE_HEIGHT + (capturing ? GROUP_SIDE_SPACING : 0);
            factoryRuntimeData.updateValues(w, h, lineHeight);
            calcTextInfo("Group XXX", true);
            factoryRuntimeData.nodes.add(this);

            for(ImageNode n : contentFactoryRuntimeData.nodes) {
                n.setFactory(factoryRuntimeData);
            }
        }


        @Override
        protected void drawGroupNode(Graphics2D g2d, FactoryRuntimeData f, int number) {
            int thisW = contentFactoryRuntimeData.TOTAL_WIDTH + 2 * GROUP_SIDE_SPACING;
            int thisH = contentFactoryRuntimeData.getMaxHeight() + 2 * GROUP_SIDE_SPACING;
            int thisArc = thisH / 3;
            int thisX = x + getPositionForCenterPlacement(w, thisW);
            // draw rectangle
            g2d.setPaint(GROUP_NODE_COLOR);
            g2d.fillRoundRect(thisX,
                    f.MAX_LINE_HEIGHT - contentFactoryRuntimeData.MAX_LINE_HEIGHT - GROUP_SIDE_SPACING, thisW,
                    thisH,thisArc, thisArc);

            // draw children
            for (ImageNode n : contentFactoryRuntimeData.nodes) {
                n.x += thisX + GROUP_SIDE_SPACING;
                n.draw(g2d);
            }

            // draw lines
            drawEndLines(g2d, f, (w - thisW) / 2 + GROUP_SIDE_SPACING);
            drawInfoText(g2d, f, "Group " + number, true);
        }

        @Override
        protected void drawNonCapturingGroupNode(Graphics2D g2d, FactoryRuntimeData f) {
            // draw children
            for (ImageNode n : contentFactoryRuntimeData.nodes) {
                n.x += x + getPositionForCenterPlacement(w, contentFactoryRuntimeData.TOTAL_WIDTH);
                n.draw(g2d);
            }
            drawEndLines(g2d, f, LINE_BASE_LENGTH);
        }

        @Override
        protected void setFactory(FactoryRuntimeData f) {
            super.setFactory(f);
            for(ImageNode n : contentFactoryRuntimeData.nodes) {
                n.setFactory(f);
            }
        }
    }

    /**
     * Representation of a {@link OrNode}
     */
    private static class StackImageNode extends ImageNode {

        private final List<FactoryRuntimeData> children;

        public StackImageNode(OrNode node, FactoryRuntimeData factoryRuntimeData) {
            this.node = node;
            this.factoryRuntimeData = factoryRuntimeData;
            children = new ArrayList<>();
            calcStackNodes(node);
        }

        @Override
        protected void calcStackNodes(OrNode node) {
            int sideSpace = 3 * LINE_BASE_LENGTH;
            // determine necessary height and width
            w = 0;
            h = 0;
            for (List<Node> row : node.getChildren()) {
                FactoryRuntimeData f = new FactoryRuntimeData();
                imageFromNodeStructure(row, f);
                w = Math.max(w, f.TOTAL_WIDTH);
                h += f.getMaxHeight();
                children.add(f);
            }
            w +=  2 * sideSpace;
            h += (node.getChildren().size() - 1) * NODE_STACKING_SPACING;
            lineHeight = h / 2;
            x = factoryRuntimeData.CURRENT_X;

            factoryRuntimeData.updateValues(w, h, lineHeight);
            factoryRuntimeData.nodes.add(this);
        }

        @Override
        protected void drawStackNodes(Graphics2D g2d, FactoryRuntimeData f) {
            int sideSpace = 3 * LINE_BASE_LENGTH;
            int curY = factoryRuntimeData.MAX_LINE_HEIGHT - lineHeight;
            for (FactoryRuntimeData cF : children) {
                int curX = x + getPositionForCenterPlacement(w, cF.TOTAL_WIDTH);
                cF.MAX_LINE_HEIGHT = curY + cF.MAX_LINE_HEIGHT;
                for (ImageNode n : cF.nodes) {
                    n.x += curX;
                    n.draw(g2d);
                }

                drawSymmetricLines(g2d, sideSpace - STROKE_WIDTH * 2, (w - cF.TOTAL_WIDTH - 2 * sideSpace) / 2 + 2 * STROKE_WIDTH, cF.MAX_LINE_HEIGHT);
                int dif = cF.MAX_LINE_HEIGHT - factoryRuntimeData.MAX_LINE_HEIGHT;
                if (dif == 0) {
                    drawEndLines(g2d, f, sideSpace);
                } else if (dif < 0) {
                    // above middle
                    g2d.drawArc(x + sideSpace / 2, cF.MAX_LINE_HEIGHT, sideSpace / 2, sideSpace / 2, 90, 90); // left arc
                    g2d.drawArc(x + w - sideSpace, cF.MAX_LINE_HEIGHT, sideSpace / 2, sideSpace / 2, 0, 90); // right arc
                } else {
                    // below middle
                    g2d.drawArc(x + sideSpace / 2, cF.MAX_LINE_HEIGHT - sideSpace / 2, sideSpace / 2, sideSpace / 2, -90, -90); // left arc
                    g2d.drawArc(x + w - sideSpace, cF.MAX_LINE_HEIGHT - sideSpace / 2, sideSpace / 2, sideSpace / 2, 0, -90); // right arc
                }
                curY += cF.getMaxHeight() + NODE_STACKING_SPACING;
            }

            int firstLH = children.get(0).MAX_LINE_HEIGHT;
            int lastLH = children.get(children.size() - 1).MAX_LINE_HEIGHT;

            // left side
            g2d.drawLine(x + sideSpace / 2, firstLH + sideSpace / 4, x  + sideSpace / 2, factoryRuntimeData.MAX_LINE_HEIGHT - sideSpace / 2 + 2 * STROKE_WIDTH); // top line down
            g2d.drawLine(x + sideSpace / 2, lastLH - sideSpace / 4, x + sideSpace / 2, factoryRuntimeData.MAX_LINE_HEIGHT + sideSpace / 2 - 2 * STROKE_WIDTH); // bottom line up
            g2d.drawArc(x, factoryRuntimeData.MAX_LINE_HEIGHT - sideSpace / 2, sideSpace / 2,  sideSpace/ 2, -90, 90); // top arc
            g2d.drawArc(x, factoryRuntimeData.MAX_LINE_HEIGHT, sideSpace / 2,  sideSpace/ 2, 90, -90); // bottom arc

            // right side
            g2d.drawLine(x + w - sideSpace / 2, firstLH + sideSpace / 4, x + w - sideSpace / 2, factoryRuntimeData.MAX_LINE_HEIGHT - sideSpace / 2 + 2 * STROKE_WIDTH); // top line down
            g2d.drawLine(x + w - sideSpace / 2, lastLH - sideSpace / 4, x + w - sideSpace / 2, factoryRuntimeData.MAX_LINE_HEIGHT + sideSpace / 2 - 2 * STROKE_WIDTH); // bottom line down
            g2d.drawArc(x + w - sideSpace / 2, f.MAX_LINE_HEIGHT - sideSpace / 2, sideSpace / 2,  sideSpace/ 2, 180, 90); // top arc
            g2d.drawArc(x + w - sideSpace / 2, f.MAX_LINE_HEIGHT, sideSpace / 2,  sideSpace/ 2, 180, -90); // bottom arc
            drawEndLines(g2d, f, 2 * STROKE_WIDTH + 1);


        }
    }

    /**
     * Calculates the position and size of start and end node and adds them to the FactoryRuntimeData.
     * @param f Data to save into
     */
    private static void calcEndNodes(FactoryRuntimeData f) {
        int size = CONTENT_FONT_METRICS.getHeight();
        f.CURRENT_X += size + 2 * LINE_BASE_LENGTH;
        f.TOTAL_WIDTH += 2 * size + (int)(2.5 * LINE_BASE_LENGTH) + STROKE_WIDTH;
        f.MAX_LINE_HEIGHT = Math.max(f.MAX_LINE_HEIGHT, size / 2);
        f.MAX_BELOW_LINE = size / 2;
    }

    /**
     * Draws the start and end node onto the {@link Image} of the {@link Graphics2D}
     * @param g2d Graphics object for drawing
     * @param f Data to draw from
     */
    private static void drawEndNodes(Graphics2D g2d , FactoryRuntimeData f) {
        int imageLength = f.TOTAL_WIDTH - STROKE_WIDTH;
        int size = CONTENT_FONT_METRICS.getHeight();

        // start node
        // draw line through middle, used as both the outgoing line on the left and the shaft of the arrow
        g2d.setPaint(LINE_COLOR);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH));
        g2d.drawLine(0, f.MAX_LINE_HEIGHT, size + 2 * LINE_BASE_LENGTH, f.MAX_LINE_HEIGHT);

        // draw circle
        g2d.setPaint(END_NODE_COLOR);
        g2d.fillOval((int)(1.5 * LINE_BASE_LENGTH) + 2, f.MAX_LINE_HEIGHT - size/2 + 2, size - 4, size - 4);

        // draw arrow tip
        g2d.setPaint(LINE_COLOR);
        g2d.drawLine((int)(1.5 * LINE_BASE_LENGTH), f.MAX_LINE_HEIGHT, LINE_BASE_LENGTH, f.MAX_LINE_HEIGHT + LINE_BASE_LENGTH / 2);
        g2d.drawLine((int)(1.5 * LINE_BASE_LENGTH), f.MAX_LINE_HEIGHT, LINE_BASE_LENGTH, f.MAX_LINE_HEIGHT - LINE_BASE_LENGTH / 2);


        // end node
        int innerSize = size / 2;
        g2d.setPaint(LINE_COLOR);
        g2d.setStroke(new BasicStroke(STROKE_WIDTH));

        // draw connecting line to the left
        g2d.drawLine(imageLength - size, f.MAX_LINE_HEIGHT, imageLength - (int)(0.5 * LINE_BASE_LENGTH) - size, f.MAX_LINE_HEIGHT);

        // draw outer circle
        g2d.setPaint(END_NODE_COLOR);
        g2d.drawOval(imageLength - size + 2, f.MAX_LINE_HEIGHT - size/2 + 2, size - 4, size - 4);
        //draw inner circle
        g2d.fillOval(imageLength - size + (size-innerSize) / 2, f.MAX_LINE_HEIGHT - (size-innerSize) / 2, innerSize, innerSize);
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
     * @param alpha Alpha value
     * @return Transparent color
     */
    public static JBColor makeTransparent(JBColor color, int alpha) {
        return new JBColor(new Color(color.brighter().getRed(), color.brighter().getGreen(), color.brighter().getBlue(), alpha), new Color(color.darker().getRed(), color.darker().getGreen(), color.darker().getBlue(), alpha));
    }

    /**
     * Private constructor for Utility class
     */
    private RegExImageFactory() {}

}
