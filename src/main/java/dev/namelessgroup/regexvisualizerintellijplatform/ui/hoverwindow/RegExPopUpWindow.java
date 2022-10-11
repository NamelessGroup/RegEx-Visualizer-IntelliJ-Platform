package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.UIDisplayable;

import javax.swing.*;
import java.awt.*;

public class RegExPopUpWindow extends JBPanel<RegExPopUpWindow> implements UIDisplayable {


    public RegExPopUpWindow(Image img) {
        super();
        add(new JBScrollPane(new JBLabel(new ImageIcon(img))));
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }
}
