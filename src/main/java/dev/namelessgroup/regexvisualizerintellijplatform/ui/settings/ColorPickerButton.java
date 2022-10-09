package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.Disposable;
import com.intellij.ui.ColorChooserServiceImpl;
import com.intellij.ui.ColorPickerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class ColorPickerButton extends JButton {

    private final String parameterName;
    private Color color;

    public ColorPickerButton(String parameterName, Color color, @NotNull ColorPickerListener listener) {
        super(parameterName);
        this.parameterName = parameterName;
        this.color = color;

        setBackground(color);
        addActionListener(e -> {
            ColorChooserServiceImpl p = new ColorChooserServiceImpl();
            p.showDialog(this, parameterName, this.color, true, List.of(new ColorPickerListener() {
                @Override
                public void colorChanged(@Nullable Color newColor) {
                    setColorPrivate(newColor);
                }

                @Override
                public void closed(@Nullable Color newColor) {
                    setBackground(newColor);
                }
            }, listener), true);
        });
    }

    private void setColorPrivate(Color color) {
        this.color = color;
    }
    public void setColor(Color color) {
        this.color = color;
        setBackground(color);
    }

    public Color getColor() {
        return color;
    }
}
