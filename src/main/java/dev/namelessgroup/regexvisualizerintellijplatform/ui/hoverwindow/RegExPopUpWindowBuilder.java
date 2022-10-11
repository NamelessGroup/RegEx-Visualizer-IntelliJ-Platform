package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.ui.popup.ComponentPopupBuilderImpl;

public class RegExPopUpWindowBuilder extends ComponentPopupBuilderImpl {

    public RegExPopUpWindowBuilder(RegExPopUpWindow popUp) {
        super(popUp, popUp.getPreferredFocusedComponent());
        setResizable(false);
        setMovable(false);
        setCancelOnClickOutside(true);
    }
}
