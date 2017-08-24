package io.github.movementspeed.nhglib.input.controllers;

/**
 * Created by Fausto Napoli on 24/01/2017.
 */
public class ControllerConfiguration {
    private int id;
    private StickConfiguration leftStick;
    private StickConfiguration rightStick;

    public void setId(int id) {
        this.id = id;
    }

    public void setLeftStick(StickConfiguration leftStick) {
        this.leftStick = leftStick;
    }

    public void setRightStick(StickConfiguration rightStick) {
        this.rightStick = rightStick;
    }

    public int getId() {
        return id;
    }

    public StickConfiguration getLeftStick() {
        return leftStick;
    }

    public StickConfiguration getRightStick() {
        return rightStick;
    }
}
