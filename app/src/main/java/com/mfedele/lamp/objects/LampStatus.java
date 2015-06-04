package com.mfedele.lamp.objects;

/**
 * Created by Marco Fedele on 04/06/15.
 */
public class LampStatus {

    private int brightness;
    private boolean isColorEnabled;
    private int color;
    private int colorBrightness;

    public LampStatus(int brightness, boolean isColorEnabled, int color, int colorBrightness) {

        this.brightness = brightness;
        this.isColorEnabled = isColorEnabled;
        this.color = color;
        this.colorBrightness = colorBrightness;
    }

    public void setIsColorEnabled(boolean isColorEnabled) {
        this.isColorEnabled = isColorEnabled;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public boolean isColorEnabled() {
        return isColorEnabled;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getColorBrightness() {
        return colorBrightness;
    }

    public void setColorBrightness(int colorBrightness) {
        this.colorBrightness = colorBrightness;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LampStatus)) return false;

        LampStatus that = (LampStatus) o;

        if (brightness != that.brightness) return false;
        if (isColorEnabled != that.isColorEnabled) return false;
        if (color != that.color) return false;
        return colorBrightness == that.colorBrightness;
    }

    @Override
    public int hashCode() {
        int result = brightness;
        result = 31 * result + (isColorEnabled ? 1 : 0);
        result = 31 * result + color;
        result = 31 * result + colorBrightness;
        return result;
    }

    @Override
    public String toString() {
        return "LampStatus {" +
                "brightness=" + brightness +
                ", isColorEnabled=" + isColorEnabled +
                ", color=" + color +
                ", colorBrightness=" + colorBrightness +
                '}';
    }
}
