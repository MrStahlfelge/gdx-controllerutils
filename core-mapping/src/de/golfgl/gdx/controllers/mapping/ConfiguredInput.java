package de.golfgl.gdx.controllers.mapping;

/**
 * Input type definition
 */
public class ConfiguredInput {
    // configured by game
    public int inputId;
    public Type inputType;

    public ConfiguredInput(Type type, int inputId) {
        this.inputId = inputId;
        this.inputType = type;
    }

    public enum Type {
        /**
         * a button is a button on the controller that is pressed or not pressed
         */
        button,
        /**
         * an axis is an axis that can be an analog or a digital axis, so mapping can use buttons, analog sticks
         * or dpads for this. If an analog axis is used, this gives you its real value between -1.0 and +1.0,
         * if button or dpad is used this gives you values -1, 0 or 1
         */
        axis,
        /**
         * an analog axis is an axis on the controller that gives you values between -1.0 and +1.0. Mapping will
         * not accept dpad or buttons
         */
        axisAnalog,
        /**
         * a digital axis is an axis that gives you values -1, 0 or +1. Mapping can use
         * an analog axis, dpad or normal buttons for this. If an analog axis is used, a treshold value of default
         * 0.5 is 0 and of > 0.8 is 1. Between old value is used.
         */
        axisDigital
    }
}
