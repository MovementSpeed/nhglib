package io.github.movementspeed.nhglib.input.controllers;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class StickConfiguration {
    private boolean invertHorizontalAxis;
    private boolean invertVerticalAxis;
    private float deadZoneRadius;
    private float horizontalSensitivity;
    private float verticalSensitivity;

    public void setInvertHorizontalAxis(boolean invertHorizontalAxis) {
        this.invertHorizontalAxis = invertHorizontalAxis;
    }

    public void setInvertVerticalAxis(boolean invertVerticalAxis) {
        this.invertVerticalAxis = invertVerticalAxis;
    }

    public void setDeadZoneRadius(float deadZoneRadius) {
        this.deadZoneRadius = deadZoneRadius;
    }

    public void setHorizontalSensitivity(float horizontalSensitivity) {
        this.horizontalSensitivity = horizontalSensitivity;
    }

    public void setVerticalSensitivity(float verticalSensitivity) {
        this.verticalSensitivity = verticalSensitivity;
    }

    public boolean getInvertHorizontalAxis() {
        return invertHorizontalAxis;
    }

    public boolean getInvertVerticalAxis() {
        return invertVerticalAxis;
    }

    public float getDeadZoneRadius() {
        return deadZoneRadius;
    }

    public float getHorizontalSensitivity() {
        return horizontalSensitivity;
    }

    public float getVerticalSensitivity() {
        return verticalSensitivity;
    }
}