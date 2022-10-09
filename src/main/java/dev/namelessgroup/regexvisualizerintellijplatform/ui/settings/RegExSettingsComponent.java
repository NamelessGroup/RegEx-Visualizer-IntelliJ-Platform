package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.ColorPickerListener;
import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import dev.namelessgroup.regexvisualizerintellijplatform.model.GroupNode;
import dev.namelessgroup.regexvisualizerintellijplatform.model.Node;
import dev.namelessgroup.regexvisualizerintellijplatform.model.OneOfNode;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.RegExImageFactory;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.UIDisplayable;
import org.jetbrains.annotations.Nullable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Display component for settings <br>
 * View component of the settings Model-View-Control implementation
 */
public class RegExSettingsComponent extends JBPanel<RegExSettingsComponent> implements UIDisplayable, Disposable {

    private static final Insets DEFAULT_INSETS = JBUI.insets(5);
    private JBCheckBox saveToolWindowTabs;
    private JBCheckBox showPopUpOnRegEx;
    private JBCheckBox showPopUpOnReference;
    private ComboBox<PopUpLocation> popUpLocationComboBox;

    private ComboBox<RegExImageQualitySettings> regExImageQualityComboBox;
    private ColorPickerButton nodeColor;
    private ColorPickerButton optionNodeColor;
    private ColorPickerButton groupNodeColor;
    private ColorPickerButton textColor;
    private ColorPickerButton lineColor;
    private ColorPickerButton endNodeColor;
    private JBLabel exampleImageLabel;
    private JBLabel calcTime;
    private static final List<Node> EXAMPLE_GRAPH = List.of(new Node("Normal", 0, 2), new OneOfNode("Option"), new GroupNode(List.of(new Node("Group"))));

    /**
     * Creates a new RegExSettingsComponent, adding all panels and inputs
     */
    public RegExSettingsComponent() {
        super(new GridBagLayout());

        add(createHoverSettingsPanel(),
                new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
        add(createToolWindowSettingsPanel(),
                new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
        add (createVisualizerSettingsPanel(),
                new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
        add(new JPanel(),
                new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,DEFAULT_INSETS,0,0));
        add(createIssuesPanel(),
                new GridBagConstraints(0,3,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
    }

    private JComponent createHoverSettingsPanel() {
        showPopUpOnRegEx = new JBCheckBox("Show popup when hovering over a regex");
        showPopUpOnReference = new JBCheckBox("Show popup when hovering over a variable containing a determinable regex");
        popUpLocationComboBox = new ComboBox<>(PopUpLocation.values());

        JBLabel popupLocationLabel = new JBLabel("Popup location:");

        showPopUpOnRegEx.addChangeListener(e -> {
            showPopUpOnReference.setEnabled(showPopUpOnRegEx.isSelected());
            popupLocationLabel.setEnabled(showPopUpOnRegEx.isSelected());
            popUpLocationComboBox.setEnabled(showPopUpOnRegEx.isSelected());
        });


        JBPanel<JBPanel> hoverSettings = new JBPanel<>(new GridBagLayout());
        hoverSettings.add(new TitledSeparator("Hover Settings"),
                new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(showPopUpOnRegEx,
                new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(showPopUpOnReference,
                new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(popupLocationLabel,
                new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(popUpLocationComboBox,
                new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        return hoverSettings;
    }

    private JComponent createToolWindowSettingsPanel() {
        saveToolWindowTabs = new JBCheckBox("Save tool window tabs");

        JBPanel<JBPanel> toolWindowSettings = new JBPanel<>(new GridBagLayout());
        toolWindowSettings.add(new TitledSeparator("Tool Window Settings"),
                new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        toolWindowSettings.add(saveToolWindowTabs,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        return toolWindowSettings;
    }

    private JComponent createVisualizerSettingsPanel() {
        nodeColor = createColorPickerButton("Node Color", JBColor.ORANGE);
        optionNodeColor = createColorPickerButton("Option Node Color", JBColor.RED);
        groupNodeColor = createColorPickerButton("Group Node Color", JBColor.YELLOW);
        textColor = createColorPickerButton("Text Color", JBColor.BLACK);
        lineColor = createColorPickerButton("Line Color", JBColor.GRAY);
        endNodeColor = createColorPickerButton("Start/End Node Color", JBColor.BLACK);
        regExImageQualityComboBox = new ComboBox<>(RegExImageQualitySettings.values());
        exampleImageLabel = new JBLabel();
        calcTime = new JBLabel("");

        exampleImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        exampleImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        calcTime.setHorizontalAlignment(SwingConstants.CENTER);
        regExImageQualityComboBox.addActionListener(e -> updateImage());

        JBPanel<JBPanel> visualizerSettings = new JBPanel<>(new GridBagLayout());
        visualizerSettings.add(new TitledSeparator("Visualizer Settings"),
                new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        visualizerSettings.add(new JBLabel("Quality:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(regExImageQualityComboBox, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(new JBLabel("Colors:"), new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(nodeColor, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(optionNodeColor, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(groupNodeColor, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(lineColor, new GridBagConstraints(0, 7, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(textColor, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add(endNodeColor, new GridBagConstraints(0, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        visualizerSettings.add(new JBScrollPane(exampleImageLabel), new GridBagConstraints(1, 4, 1, 4, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        visualizerSettings.add (calcTime, new GridBagConstraints(1, 9, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));

        return visualizerSettings;
    }

    private JComponent createIssuesPanel() {
        JBLabel issuesLabel = new JBLabel("If you have any issues, please report them on GitHub.");
        issuesLabel.setForeground(JBColor.BLUE);
        issuesLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        Map underlineAttr = issuesLabel.getFont().getAttributes();
        underlineAttr.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
        Font underlineFont = issuesLabel.getFont().deriveFont(underlineAttr);
        Font standardFont = issuesLabel.getFont().deriveFont(Font.PLAIN);
        issuesLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/NamelessGroup/RegEx-Visualizer-IntelliJ-Platform/issues"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                issuesLabel.setFont(underlineFont);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                issuesLabel.setFont(standardFont);
            }
        });
        return issuesLabel;
    }

    private ColorPickerButton createColorPickerButton(String name, Color color) {
        return new ColorPickerButton(name, color, new ColorPickerListener() {
            @Override
            public void colorChanged(Color color) {
                updateImage();
            }

            @Override
            public void closed(@Nullable Color color) {
            }
        });
    }

    public void updateImage() {
        long start = System.nanoTime();
        Image img = RegExImageFactory.createImage(EXAMPLE_GRAPH,
                nodeColor.getColor(),
                optionNodeColor.getColor(),
                groupNodeColor.getColor(),
                lineColor.getColor(),
                textColor.getColor(),
                endNodeColor.getColor(),
                regExImageQualityComboBox.getItem());
        long end = System.nanoTime();
        calcTime.setText("Visualization generated in " + (end - start) / (float)1000000 + "ms");
        exampleImageLabel.setIcon(new ImageIcon(img));
    }

    public boolean isSaveToolWindowTabs() {
        return saveToolWindowTabs.isSelected();
    }

    public void setSaveToolWindowTabs(boolean saveToolWindowTabs) {
        this.saveToolWindowTabs.setSelected(saveToolWindowTabs);
    }

    public boolean isShowPopUpOnRegEx() {
        return showPopUpOnRegEx.isSelected();
    }

    public void setShowPopUpOnRegEx(boolean showPopUpOnRegEx) {
        this.showPopUpOnRegEx.setSelected(showPopUpOnRegEx);
    }

    public boolean isShowPopUpOnReference() {
        return showPopUpOnReference.isSelected();
    }

    public void setShowPopUpOnReference(boolean showPopUpOnReference) {
        this.showPopUpOnReference.setSelected(showPopUpOnReference);
    }

    public boolean isShowPopupAboveCode() {
        return popUpLocationComboBox.getItem() == PopUpLocation.ABOVE_CODE;
    }

    public void setShowPopupAboveCode(boolean showAboveCode) {
        popUpLocationComboBox.setSelectedItem(showAboveCode ? PopUpLocation.ABOVE_CODE : PopUpLocation.BELOW_CODE);
    }

    public Color getNodeColor() {
        return nodeColor.getColor();
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor.setColor(nodeColor);
    }

    public Color getOptionNodeColor() {
        return optionNodeColor.getColor();
    }

    public void setOptionNodeColor(Color optionNodeColor) {
        this.optionNodeColor.setColor(optionNodeColor);
    }

    public Color getGroupNodeColor() {
        return groupNodeColor.getColor();
    }

    public void setGroupNodeColor(Color groupNodeColor) {
        this.groupNodeColor.setColor(groupNodeColor);
    }

    public Color getLineColor() {
        return lineColor.getColor();
    }

    public void setLineColor(Color lineColor) {
        this.lineColor.setColor(lineColor);
    }

    public Color getTextColor() {
        return textColor.getColor();
    }

    public void setTextColor(Color textColor) {
        this.textColor.setColor(textColor);
    }

    public Color getEndNodeColor() {
        return endNodeColor.getColor();
    }

    public void setEndNodeColor(Color endNodeColor) {
        this.endNodeColor.setColor(endNodeColor);
    }

    public RegExImageQualitySettings getRegExImageQuality() {
        return regExImageQualityComboBox.getItem();
    }

    public void setRegExImageQuality(RegExImageQualitySettings regExImageQuality) {
        this.regExImageQualityComboBox.setSelectedItem(regExImageQuality);
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return showPopUpOnRegEx;
    }

    @Override
    public void dispose() {
        saveToolWindowTabs = null;
        showPopUpOnRegEx = null;
        showPopUpOnReference = null;
        popUpLocationComboBox = null;

        regExImageQualityComboBox = null;
        nodeColor = null;
        optionNodeColor = null;
        groupNodeColor = null;
        textColor = null;
        lineColor = null;
        endNodeColor = null;
        exampleImageLabel = null;
        calcTime = null;

        Disposer.dispose(this);
    }

    private enum PopUpLocation {
        ABOVE_CODE{
            @Override
            public String toString() {
                return "Above code";
            }
        },
        BELOW_CODE {
            @Override
            public String toString() {
                return "Below code";
            }
        }
    }
}
