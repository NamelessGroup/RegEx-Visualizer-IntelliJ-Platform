package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.OptionTag;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.RegExImageFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

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
    @OptionTag(converter = ColorConverter.class)
    private Color nodeColor = RegExImageFactory.makeTransparent(JBColor.ORANGE, 200);
    @OptionTag(converter = ColorConverter.class)
    private Color optionNodeColor = RegExImageFactory.makeTransparent(JBColor.RED, 200);
    @OptionTag(converter = ColorConverter.class)
    private Color groupNodeColor = RegExImageFactory.makeTransparent(JBColor.YELLOW, 40);
    @OptionTag(converter = ColorConverter.class)
    private Color lineColor = JBColor.GRAY;
    @OptionTag(converter = ColorConverter.class)
    private Color textColor = JBColor.BLACK;
    @OptionTag(converter = ColorConverter.class)
    private Color endNodeColor = JBColor.BLACK;
    private RegExImageQualitySettings imageQualitySettings = RegExImageQualitySettings.MEDIUM;

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

    public Color getOptionNodeColor() {
       return optionNodeColor;
    }

    public void setOptionNodeColor(Color optionNodeColor) {
        this.optionNodeColor = optionNodeColor;
    }

    public Color getGroupNodeColor() {
        return groupNodeColor;
    }

    public void setGroupNodeColor(Color groupNodeColor) {
        this.groupNodeColor = groupNodeColor;
    }

    public Color getLineColor() {
       return lineColor;
    }

    public void setLineColor(Color lineColor) {
        this.lineColor = lineColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getEndNodeColor() {
        return endNodeColor;
    }

    public void setEndNodeColor(Color endNodeColor) {
        this.endNodeColor = endNodeColor;
    }

    public Color getNodeColor() {
        return nodeColor;
    }

    public void setNodeColor(Color nodeColor) {
        this.nodeColor = nodeColor;
    }

    public RegExImageQualitySettings getImageQualitySettings() {
        return imageQualitySettings;
    }

    public void setImageQualitySettings(RegExImageQualitySettings imageQualitySettings) {
        this.imageQualitySettings = imageQualitySettings;
    }

}
