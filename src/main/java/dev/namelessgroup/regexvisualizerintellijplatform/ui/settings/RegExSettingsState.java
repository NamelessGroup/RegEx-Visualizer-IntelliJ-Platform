package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Stores the settings during the plugin running and stores them on the hard-drive <br>
 * Model component of the settings Model-View-Control implementation <br>
 * Uses the Singleton pattern
 */
@State(
        name = "dev.namelessgroup.regexvisualizerintellijplatform.RegExSettingsState",
        storages = @Storage("regexvisualizerintelijplatform.xml")
)
public class RegExSettingsState implements PersistentStateComponent<RegExSettingsState> {

    private boolean saveToolWindowTabs = true;
    private boolean showPopUpOnRegEx = true;
    private boolean showPopUpOnReference = true;
    private boolean showPopUpAboveCode = true;

    /**
     * Returns the instance of the settings according to the Singleton pattern
     * @return The instance of the settings
     */
    public static RegExSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(RegExSettingsState.class);
    }

    @Override
    public @Nullable RegExSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull RegExSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }


    public boolean isSaveToolWindowTabs() {
        return saveToolWindowTabs;
    }

    public void setSaveToolWindowTabs(boolean saveToolWindowTabs) {
        this.saveToolWindowTabs = saveToolWindowTabs;
    }

    public boolean isShowPopUpOnRegEx() {
        return showPopUpOnRegEx;
    }

    public void setShowPopUpOnRegEx(boolean showPopUpOnRegEx) {
        this.showPopUpOnRegEx = showPopUpOnRegEx;
    }

    public boolean isShowPopUpAboveCode() {
        return showPopUpAboveCode;
    }

    public void setShowPopUpAboveCode(boolean showPopUpAboveCode) {
        this.showPopUpAboveCode = showPopUpAboveCode;
    }

    public boolean isShowPopUpOnReference() {
        return showPopUpOnReference;
    }

    public void setShowPopUpOnReference(boolean showPopUpOnReference) {
        this.showPopUpOnReference = showPopUpOnReference;
    }
}
