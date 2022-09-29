package dev.namelessgroup.regexvisualizerintellijplatform.ui;

import javax.swing.JComponent;

/**
 * Interface to be implemented by UI components
 */
public interface UIDisplayable {

    /**
     * Returns the component to be focues when opening UI window
     *
     * @return Preferred focused component
     */
    JComponent getPreferredFocusedComponent();

}
