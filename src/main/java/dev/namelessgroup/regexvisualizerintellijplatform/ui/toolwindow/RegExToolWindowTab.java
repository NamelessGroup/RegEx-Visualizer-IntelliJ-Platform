package dev.namelessgroup.regexvisualizerintellijplatform.ui.toolwindow;

import com.intellij.ui.JBColor;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.*;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.JBUI;
import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexParser;
import dev.namelessgroup.regexvisualizerintellijplatform.controller.RegexUtilites;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.RegExImageFactory;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.settings.RegExSettingsState;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;

public class RegExToolWindowTab extends JBPanel<RegExToolWindowTab> {

    private static final JBInsets DEFAULT_INSETS = JBUI.insets(5);
    private final JBTextField regExInput;
    private final JTextPane testInput;
    private final JBCheckBox separateTestsCheckBox;

    private final Style testInputDefaultStyle;
    private final Style testInputMatchStyle;
    private final Style testInputNoMatchStyle;
    private final JBLabel imageHolder;

    private final String name;

    private final RegExSettingsState settingsState;

    public RegExToolWindowTab(String name) {
        super(new GridBagLayout());
        settingsState = RegExSettingsState.getInstance();
        this.name = name;

        regExInput = new JBTextField();
        testInput = new JTextPane();
        separateTestsCheckBox = new JBCheckBox("Separate tests at linebreak", false);
        imageHolder = new JBLabel();

        add(createRegExInputPane(), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));
        add(createTestInputPane(), new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));
        add(createVerticalSeparatorLine(), new GridBagConstraints(1, 0, 1, 2, 0, 1, GridBagConstraints.NORTH, GridBagConstraints.VERTICAL, DEFAULT_INSETS, 0, 0));
        add(createVisualizerDisplayPane(), new GridBagConstraints(2, 0, 1, 2, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));

        testInputDefaultStyle = testInput.addStyle("default", null);
        StyleConstants.setForeground(testInputDefaultStyle, testInput.getForeground());
        testInputMatchStyle = testInput.addStyle("match", null);
        StyleConstants.setForeground(testInputMatchStyle, JBColor.GREEN);
        testInputNoMatchStyle = testInput.addStyle("noMatch", null);
        StyleConstants.setForeground(testInputNoMatchStyle, JBColor.RED);
        updateImage();
    }

    public RegExToolWindowTab(String name, String regEx) {
        this(name);
        regExInput.setText(regEx);
    }

    private JComponent createRegExInputPane() {
        regExInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onRegExInputChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onRegExInputChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        JBPanel<JBPanel> regExInputPane = new JBPanel<>(new GridBagLayout());
        regExInputPane.add(new JLabel("Regex:"),
                new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        regExInputPane.add(regExInput,
                new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        return regExInputPane;
    }

    private JComponent createTestInputPane() {
        testInput.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateTestInputColoring());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                SwingUtilities.invokeLater(() -> updateTestInputColoring());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        separateTestsCheckBox.addActionListener(e -> updateTestInputColoring());

        JBPanel<JBPanel> regExTestPane = new JBPanel<>(new GridBagLayout());
        regExTestPane.add(new TitledSeparator("Test Input"),
                new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        regExTestPane.add(new JBScrollPane(testInput),
                new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));
        regExTestPane.add(separateTestsCheckBox, new GridBagConstraints(0, 2, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, DEFAULT_INSETS, 0, 0));
        return regExTestPane;
    }

    private JComponent createVerticalSeparatorLine() {
        return new JSeparator(SwingConstants.VERTICAL);
    }

    private JComponent createVisualizerDisplayPane() {
        imageHolder.setHorizontalAlignment(SwingConstants.CENTER);


        JBPanel<JBPanel> visualizerDisplayPane = new JBPanel<>(new GridBagLayout());
        visualizerDisplayPane.add(new JBScrollPane(imageHolder), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, DEFAULT_INSETS, 0, 0));

        return visualizerDisplayPane;
    }

    private void onRegExInputChanged() {
        updateImage();

        updateTestInputColoring();
    }

    private void updateTestInputColoring() {
        String regex = regExInput.getText();
        Document document = testInput.getDocument();
        StyledDocument styledDocument = testInput.getStyledDocument();
        if (!RegexUtilites.isRegex(regex)) {
            styledDocument.setCharacterAttributes(0, document.getLength(), testInputDefaultStyle, false);
            return;
        }

        if (separateTestsCheckBox.isSelected()) {
            String[] testCases = testInput.getText().split(System.lineSeparator());
            int offset = 0;
            for (String testCase : testCases) {
                styledDocument.setCharacterAttributes(offset, testCase.length(), RegexUtilites.matchesRegex(regex, testCase) ? testInputMatchStyle : testInputNoMatchStyle, false);
                offset += testCase.length() + 1;
            }
        } else {
            styledDocument.setCharacterAttributes(0, document.getLength(), RegexUtilites.matchesRegex(regex, testInput.getText()) ? testInputMatchStyle : testInputNoMatchStyle, false);
        }

        //styledDocument.setCharacterAttributes(0, document.getLength(), testInputDefaultStyle, false);

    }

    private void updateImage() {
        String regEx  = regExInput.getText();
        if (!RegexUtilites.isRegex(regEx)) {
            imageHolder.setIcon(null);
            return;
        }
        try {
            imageHolder.setIcon(new ImageIcon(RegExImageFactory.createImage(
                    new RegexParser(regExInput.getText()).buildRegexNodes(),
                    settingsState.getNodeColor(),
                    settingsState.getOptionNodeColor(),
                    settingsState.getGroupNodeColor(),
                    settingsState.getLineColor(),
                    settingsState.getTextColor(),
                    settingsState.getEndNodeColor(),
                    settingsState.getImageQualitySettings())));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getName() {
        return name;
    }

}
