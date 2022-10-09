package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import com.intellij.ui.JBColor;
import com.intellij.util.xmlb.Converter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;

/**
 * Converts Colors to Strings and vice versa for storing them in a xml file
 */
public class ColorConverter extends Converter<Color> {
    @Override
    public @Nullable Color fromString(@NotNull String value) {
        String[] split = value.split("\\.");
        if (split.length != 4) {
            return JBColor.BLACK;
        }
        return new Color(Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2]), Integer.parseInt(split[3]));
    }

    @Override
    public @Nullable String toString(@NotNull Color value) {
        return value.getRed() + "." + value.getGreen() + "." + value.getBlue() + "." + value.getAlpha();
    }
}
