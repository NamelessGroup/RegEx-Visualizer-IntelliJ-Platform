package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.ui.popup.ComponentPopupBuilderImpl;

/**
 * Builder class for the regex popup window
 */
public class RegExPopUpWindowBuilder extends ComponentPopupBuilderImpl {

    /**
     * Creates a new RegExPopUpWindowBuilder
     * @param popUp The popup window
     */
    public RegExPopUpWindowBuilder(RegExPopUpWindow popUp) {
        super(popUp, popUp.getPreferredFocusedComponent());
        setResizable(false);
        setMovable(false);
        setCancelOnClickOutside(true);
    }
}
