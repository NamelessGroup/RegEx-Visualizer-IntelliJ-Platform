package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Logic for settings <br>
 * Controller component of the settings Model-View-Control implementation
 */
public class RegExSettingsConfigurable implements Configurable {

    RegExSettingsComponent component;

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "RegEx Visualizer";
    }

    @Override
    public @Nullable JComponent createComponent() {
        component = new RegExSettingsComponent();
        return component;
    }

    @Override
    public boolean isModified() {
        RegExSettingsState settingsState = RegExSettingsState.getInstance();

        return settingsState.isSaveToolWindowTabs() != component.isSaveToolWindowTabs()
                || settingsState.isShowPopUpOnRegEx() != component.isShowPopUpOnRegEx()
                || settingsState.isShowPopUpOnReference() != component.isShowPopUpOnReference()
                || settingsState.isShowPopUpAboveCode() != component.isShowPopupAboveCode()
                || settingsState.getNodeColor().getRGB() != component.getNodeColor().getRGB()
                || settingsState.getOptionNodeColor().getRGB() != component.getOptionNodeColor().getRGB()
                || settingsState.getGroupNodeColor().getRGB() != component.getGroupNodeColor().getRGB()
                || settingsState.getLineColor().getRGB() != component.getLineColor().getRGB()
                || settingsState.getTextColor().getRGB() != component.getTextColor().getRGB()
                || settingsState.getEndNodeColor().getRGB() != component.getEndNodeColor().getRGB()
                || settingsState.getImageQualitySettings() != component.getRegExImageQuality();
    }

    @Override
    public void apply() throws ConfigurationException {
        RegExSettingsState settingsState = RegExSettingsState.getInstance();

        settingsState.setSaveToolWindowTabs(component.isSaveToolWindowTabs());
        settingsState.setShowPopUpOnRegEx(component.isShowPopUpOnRegEx());
        settingsState.setShowPopUpOnReference(component.isShowPopUpOnReference());
        settingsState.setShowPopUpAboveCode(component.isShowPopupAboveCode());
        settingsState.setImageQualitySettings(component.getRegExImageQuality());
        settingsState.setNodeColor(component.getNodeColor());
        settingsState.setOptionNodeColor(component.getOptionNodeColor());
        settingsState.setGroupNodeColor(component.getGroupNodeColor());
        settingsState.setLineColor(component.getLineColor());
        settingsState.setTextColor(component.getTextColor());
        settingsState.setEndNodeColor(component.getEndNodeColor());
    }

    @Override
    public void reset() {
        RegExSettingsState settingsState = RegExSettingsState.getInstance();

        component.setSaveToolWindowTabs(settingsState.isSaveToolWindowTabs());
        component.setShowPopUpOnRegEx(settingsState.isShowPopUpOnRegEx());
        component.setShowPopUpOnReference(settingsState.isShowPopUpOnReference());
        component.setShowPopupAboveCode(settingsState.isShowPopUpAboveCode());

        component.setRegExImageQuality(settingsState.getImageQualitySettings());
        component.setNodeColor(settingsState.getNodeColor());
        component.setOptionNodeColor(settingsState.getOptionNodeColor());
        component.setGroupNodeColor(settingsState.getGroupNodeColor());
        component.setLineColor(settingsState.getLineColor());
        component.setTextColor(settingsState.getTextColor());
        component.setEndNodeColor(settingsState.getEndNodeColor());
        component.updateImage();
    }
}
