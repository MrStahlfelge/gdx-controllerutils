package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;

import java.util.HashMap;

/**
 * This class adapts mapped controller keys to key events and sends to an targetInput processor.
 * <p>
 * It is a controller-input-to-keyboard mapping after the mapping.
 * <p>
 * Use it when your game does not need analog input, but is just controlled via keyboard keys/dpad
 * to enable controller support with nearly zero effort.
 * <pre>
 *     controllerToInputAdapter = new ControllerToInputAdapter(controllerMappings);
 *     controllerToInputAdapter.addButtonMapping(BUTTON_JUMP, Input.Keys.SPACE);
 *     controllerToInputAdapter.addAxisMapping(AXIS_HORIZONTAL, Input.Keys.LEFT, Input.Keys.RIGHT);
 *     ...
 *     controllerToInputAdapter.setInputProcessor(yourKeyInputProcessor);
 * </pre>
 * <p>
 * Created by Benjamin Schulte on 06.11.2017.
 */

public class ControllerToInputAdapter extends MappedControllerAdapter {
    public float analogToDigitalTreshold;
    private InputProcessor targetInput;
    private HashMap<Integer, Integer> buttonMappings;
    private HashMap<Integer, AxisMapping> axisMappings;

    public ControllerToInputAdapter(ControllerMappings mappings) {
        super(mappings);

        analogToDigitalTreshold = mappings.analogToDigitalTreshold;
        buttonMappings = new HashMap<>();
        axisMappings = new HashMap<>();
    }

    public InputProcessor getInputProcessor() {
        return targetInput;
    }

    public void setInputProcessor(InputProcessor targetInput) {
        this.targetInput = targetInput;
    }

    @Override
    public boolean configuredAxisMoved(Controller controller, int axisId, float value) {
        if (targetInput == null || !axisMappings.containsKey(axisId))
            return false;

        AxisMapping thisMapping = axisMappings.get(axisId);
        boolean negativePressed;
        boolean positivePressed;

        if (value >= analogToDigitalTreshold) {
            negativePressed = false;
            positivePressed = true;
        } else if (value <= -analogToDigitalTreshold) {
            negativePressed = true;
            positivePressed = false;
        } else {
            negativePressed = false;
            positivePressed = false;
        }

        if (thisMapping.negativeKeyPressed != negativePressed) {
            if (negativePressed)
                sendKeyDownToTarget(thisMapping.keyCodeNegative, controller);
            else
                sendKeyUpToTarget(thisMapping.keyCodeNegative, controller);

            thisMapping.negativeKeyPressed = negativePressed;
        }

        if (thisMapping.positiveKeyPressed != positivePressed) {
            if (positivePressed)
                sendKeyDownToTarget(thisMapping.keyCodePositive, controller);
            else
                sendKeyUpToTarget(thisMapping.keyCodePositive, controller);

            thisMapping.positiveKeyPressed = positivePressed;
        }

        return true;
    }

    @Override
    public boolean configuredButtonDown(Controller controller, int buttonId) {
        if (targetInput == null || !buttonMappings.containsKey(buttonId))
            return false;

        return sendKeyDownToTarget(buttonMappings.get(buttonId), controller);
    }

    @Override
    public boolean configuredButtonUp(Controller controller, int buttonId) {
        if (targetInput == null || !buttonMappings.containsKey(buttonId))
            return false;

        return sendKeyUpToTarget(buttonMappings.get(buttonId), controller);
    }

    protected boolean sendKeyDownToTarget(int keycode, Controller inputSourceController) {
        return targetInput.keyDown(keycode);
    }

    protected boolean sendKeyUpToTarget(int keycode, Controller inputSourceController) {
        return targetInput.keyUp(keycode);
    }

    /**
     * adds a controller button to keycode mapping
     *
     * @param configuredButtonId your configured button id as given to
     *                           {@link ControllerMappings#addConfiguredInput(ConfiguredInput)}
     * @param targetKeyCode      keyCode for {@link InputProcessor}
     */
    public ControllerToInputAdapter addButtonMapping(int configuredButtonId, int targetKeyCode) {
        buttonMappings.put(configuredButtonId, targetKeyCode);
        return this;
    }

    public ControllerToInputAdapter addAxisMapping(int configuredAxisId, int targetKeyCodeNegative,
                                                   int targetKeyCodePositive) {
        AxisMapping axisMapping = new AxisMapping();
        axisMapping.keyCodeNegative = targetKeyCodeNegative;
        axisMapping.keyCodePositive = targetKeyCodePositive;

        axisMappings.put(configuredAxisId, axisMapping);

        return this;
    }

    private static class AxisMapping {
        public int keyCodeNegative;
        public int keyCodePositive;
        public boolean negativeKeyPressed;
        public boolean positiveKeyPressed;
    }
}
