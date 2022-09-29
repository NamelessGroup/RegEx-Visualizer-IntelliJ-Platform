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
                || settingsState.isShowPopUpAboveCode() != component.isShowPopupAboveCode();
    }

    @Override
    public void apply() throws ConfigurationException {
        RegExSettingsState settingsState = RegExSettingsState.getInstance();

        settingsState.setSaveToolWindowTabs(component.isSaveToolWindowTabs());
        settingsState.setShowPopUpOnRegEx(component.isShowPopUpOnRegEx());
        settingsState.setShowPopUpOnReference(component.isShowPopUpOnReference());
        settingsState.setShowPopUpAboveCode(component.isShowPopupAboveCode());
    }

    @Override
    public void reset() {
        RegExSettingsState settingsState = RegExSettingsState.getInstance();

        component.setSaveToolWindowTabs(settingsState.isSaveToolWindowTabs());
        component.setShowPopUpOnRegEx(settingsState.isShowPopUpOnRegEx());
        component.setShowPopUpOnReference(settingsState.isShowPopUpOnReference());
        component.setShowPopupAboveCode(settingsState.isShowPopUpAboveCode());
    }
}
