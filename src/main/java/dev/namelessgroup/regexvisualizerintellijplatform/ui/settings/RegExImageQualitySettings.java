package dev.namelessgroup.regexvisualizerintellijplatform.ui.settings;

import java.awt.RenderingHints;

public enum RegExImageQualitySettings {

    ULTRA_LOW("Ultra Low", 1, RenderingHints.VALUE_RENDER_SPEED, RenderingHints.VALUE_ANTIALIAS_OFF, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED, RenderingHints.VALUE_FRACTIONALMETRICS_OFF, RenderingHints.VALUE_STROKE_DEFAULT),
    LOW("Low", 2, RenderingHints.VALUE_RENDER_SPEED, RenderingHints.VALUE_ANTIALIAS_OFF, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED, RenderingHints.VALUE_FRACTIONALMETRICS_OFF, RenderingHints.VALUE_STROKE_NORMALIZE),
    MEDIUM("Medium", 2, RenderingHints.VALUE_RENDER_DEFAULT, RenderingHints.VALUE_ANTIALIAS_OFF, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_ALPHA_INTERPOLATION_DEFAULT, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT, RenderingHints.VALUE_STROKE_NORMALIZE),
    HIGH("High", 2, RenderingHints.VALUE_RENDER_DEFAULT, RenderingHints.VALUE_ANTIALIAS_ON, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY, RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT, RenderingHints.VALUE_STROKE_NORMALIZE),
    ULTRA_HIGH("Ultra High", 5, RenderingHints.VALUE_RENDER_QUALITY, RenderingHints.VALUE_ANTIALIAS_ON, RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY, RenderingHints.VALUE_FRACTIONALMETRICS_ON, RenderingHints.VALUE_STROKE_PURE);

    private final String name;
    private final int scalingFactor;
    /**
     * {@link RenderingHints#KEY_ANTIALIASING}
     */
    private final Object keyAntialiasing;
    /**
     * {@link RenderingHints#KEY_RENDERING}
     */
    private final Object keyRendering;
    /**
     * {@link RenderingHints#KEY_TEXT_ANTIALIASING}
     */
    private final Object keyTextAntialiasing;
    /**
     * {@link RenderingHints#KEY_ALPHA_INTERPOLATION}
     */
    private final Object keyAlphaInterpolation;
    /**
     * {@link RenderingHints#KEY_FRACTIONALMETRICS}
     */
    private final Object keyFractionalMetrics;
    /**
     * {@link RenderingHints#KEY_STROKE_CONTROL}
     */
    private final Object keyStrokeControl;

    RegExImageQualitySettings(String name, int scalingFactor,Object keyRendering,  Object keyAntialiasing, Object keyTextAntialiasing, Object keyAlphaInterpolation, Object keyFractionalMetrics, Object keyStrokeControl) {
        this.name = name;
        this.scalingFactor = scalingFactor;
        this.keyAntialiasing = keyAntialiasing;
        this.keyRendering = keyRendering;
        this.keyTextAntialiasing = keyTextAntialiasing;
        this.keyAlphaInterpolation = keyAlphaInterpolation;
        this.keyFractionalMetrics = keyFractionalMetrics;
        this.keyStrokeControl = keyStrokeControl;
    }

    public int getScalingFactor() {
        return scalingFactor;
    }

    /**
     * {@link RenderingHints#KEY_ANTIALIASING}
     */
    public Object getAntialiasing() {
        return keyAntialiasing;
    }

    /**
     * {@link RenderingHints#KEY_RENDERING}
     */
    public Object getRendering() {
        return keyRendering;
    }

    /**
     * {@link RenderingHints#KEY_TEXT_ANTIALIASING}
     */
    public Object getTextAntialiasing() {
        return keyTextAntialiasing;
    }

    /**
     * {@link RenderingHints#KEY_ALPHA_INTERPOLATION}
     */
    public Object getAlphaInterpolation() {
        return keyAlphaInterpolation;
    }

    /**
     * {@link RenderingHints#KEY_FRACTIONALMETRICS}
     */
    public Object getFractionalMetrics() {
        return keyFractionalMetrics;
    }

    /**
     * {@link RenderingHints#KEY_STROKE_CONTROL}
     */
    public Object getStrokeControl() {
        return keyStrokeControl;
    }

    @Override
    public String toString() {
        return name;
    }
}
