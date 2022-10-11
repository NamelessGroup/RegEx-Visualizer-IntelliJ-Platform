package dev.namelessgroup.regexvisualizerintellijplatform.ui.hoverwindow;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.UIDisplayable;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.toolwindow.RegExToolWindowFactory;
import dev.namelessgroup.regexvisualizerintellijplatform.ui.toolwindow.RegExToolWindowTab;

import javax.swing.*;
import java.awt.*;

public class RegExPopUpWindow extends JBPanel<RegExPopUpWindow> implements UIDisplayable {


    public RegExPopUpWindow(Image img, String regex) {
        super(new GridBagLayout());
        add(new JBScrollPane(new JBLabel(new ImageIcon(img))), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH, GridBagConstraints.BOTH, JBUI.emptyInsets(), 0, 0));
        JButton btn = new JButton("Open in new tab");
        btn.addActionListener(e -> RegExToolWindowFactory.getInstance().insertTab(new RegExToolWindowTab("Code", regex)));
        add(btn, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, JBUI.emptyInsets(), 0, 0));
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return null;
    }
}
