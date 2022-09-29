package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.UIDisplayable;

import javax.swing.*;
import java.awt.*;

/**
 * Display component for settings <br>
 * View component of the settings Model-View-Control implementation
 */
public class RegExSettingsComponent extends JBPanel<RegExSettingsComponent> implements UIDisplayable {

    private static final Insets DEFAULT_INSETS = JBUI.insets(5);
    private final JBCheckBox saveToolWindowTabs;
    private final JBCheckBox showPopUpOnRegEx;
    private final JBCheckBox showPopUpOnReference;
    private final ComboBox<PopUpLocation> popUpLocationComboBox;

    /**
     * Creates a new RegExSettingsComponent, adding all panels and inputs
     */
    public RegExSettingsComponent() {
        super(new GridBagLayout());

        showPopUpOnRegEx = new JBCheckBox("Show popup when hovering over a regex");
        showPopUpOnReference = new JBCheckBox("Show popup when hovering over a variable containing a determinable regex");
        saveToolWindowTabs = new JBCheckBox("Save tool window tabs");
        popUpLocationComboBox = new ComboBox<>(PopUpLocation.values());

        add(createHoverSettingsPanel(),
                new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
        add(createToolWindowSettingsPanel(),
                new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,DEFAULT_INSETS,0,0));
        add(new JPanel(),
                new GridBagConstraints(0,2,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,DEFAULT_INSETS,0,0));
    }

    private JComponent createHoverSettingsPanel() {
        JBLabel popupLocationLabel = new JBLabel("Popup location:");

        showPopUpOnRegEx.addChangeListener(e -> {
            showPopUpOnReference.setEnabled(showPopUpOnRegEx.isSelected());
            popupLocationLabel.setEnabled(showPopUpOnRegEx.isSelected());
            popUpLocationComboBox.setEnabled(showPopUpOnRegEx.isSelected());
        });


        JBPanel<JBPanel> hoverSettings = new JBPanel<>(new GridBagLayout());
        hoverSettings.add(new TitledSeparator("Hover Settings"),
                new GridBagConstraints(0, 0, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(showPopUpOnRegEx,
                new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(showPopUpOnReference,
                new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(popupLocationLabel,
                new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        hoverSettings.add(popUpLocationComboBox,
                new GridBagConstraints(1, 3, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        return hoverSettings;
    }

    private JComponent createToolWindowSettingsPanel() {
        JBPanel<JBPanel> toolWindowSettings = new JBPanel<>(new GridBagLayout());
        toolWindowSettings.add(new TitledSeparator("Tool Window Settings"),
                new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        toolWindowSettings.add(saveToolWindowTabs,
                new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, DEFAULT_INSETS, 0, 0));
        return toolWindowSettings;
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

    @Override
    public JComponent getPreferredFocusedComponent() {
        return showPopUpOnRegEx;
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
