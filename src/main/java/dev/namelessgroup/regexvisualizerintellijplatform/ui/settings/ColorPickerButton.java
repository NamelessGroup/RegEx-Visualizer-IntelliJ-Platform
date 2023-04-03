package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.openapi.Disposable;
import com.intellij.ui.ColorChooserServiceImpl;
import com.intellij.ui.ColorPickerListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;

import javax.swing.*;
import java.awt.*;

/**
 * Button that opens a ColorPicker
 */
public class ColorPickerButton extends JButton {

    private Color color;

    /**
     * Creates a new ColorPickerButton
     * @param parameterName The name of the parameter that is being changed
     * @param color The initial color
     * @param listener The listener that is called when the color is changed
     */
    public ColorPickerButton(String parameterName, Color color, @NotNull ColorPickerListener listener) {
        super(parameterName);
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

    /**
     * Sets the buttons color
     * @param color The new color
     */
    public void setColor(Color color) {
        this.color = color;
        setBackground(color);
    }

    /**
     * Gets the buttons color
     * @return The color
     */
    public Color getColor() {
        return color;
    }
}
