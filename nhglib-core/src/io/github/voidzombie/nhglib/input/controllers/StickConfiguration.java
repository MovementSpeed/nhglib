package io.github.voidzombie.nhglib.input.controllers;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class StickConfiguration {
    private Boolean invertHorizontalAxis;
    private Boolean invertVerticalAxis;
    private Float deadZoneRadius;
    private Float horizontalSensitivity;
    private Float verticalSensitivity;

    public void setInvertHorizontalAxis(Boolean invertHorizontalAxis) {
        this.invertHorizontalAxis = invertHorizontalAxis;
    }

    public void setInvertVerticalAxis(Boolean invertVerticalAxis) {
        this.invertVerticalAxis = invertVerticalAxis;
    }

    public void setDeadZoneRadius(Float deadZoneRadius) {
        this.deadZoneRadius = deadZoneRadius;
    }

    public void setHorizontalSensitivity(Float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(Float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public Boolean getInvertHorizontalAxis() {
        return invertHorizontalAxis;
    }

    public Boolean getInvertVerticalAxis() {
        return invertVerticalAxis;
    }

    public Float getDeadZoneRadius() {
        return deadZoneRadius;
    }

    public Float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public Float getVerticalSensitivity() {
        return verticalSensitivity;
    }
}