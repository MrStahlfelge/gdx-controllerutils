package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */

public class MappedController {
    private Controller controller;
    private ControllerMappings mappings;
    private ControllerMappings.MappedInputs controllerMapping;
    private float analogToDigitalTreshold;

    public MappedController(Controller controller, ControllerMappings mappings) {
        this.controller = controller;
        this.mappings = mappings;
        this.analogToDigitalTreshold = mappings.analogToDigitalTreshold;

        refreshMappingCache();
    }

    /**
     * refreshes the cached mapping from the ControllerMappings. Call this after you resetted mappings or filled from
     * json
     */
    public void refreshMappingCache() {
        controllerMapping = mappings.getControllerMapping(controller);

        if (controllerMapping == null || !controllerMapping.checkCompleted())
            Gdx.app.log(ControllerMappings.LOG_TAG, "MappedController created with incomplete configuration");
    }

    /**
     * returns whether a mapped button is pressed
     * <p>
     * It is not checked if your configuredId is a button.
     *
     * @param configuredId
     * @return true of button is pressed. false if button is not pressed, configureId is not set, not recorded or not
     * of type button
     */
    public boolean isButtonPressed(int configuredId) {
        if (controllerMapping == null)
            return false;

        ControllerMappings.MappedInput mappedInput = controllerMapping.getMappedInput(configuredId);

        // not configured or not recorded
        if (mappedInput == null)
            return false;

        // A virtual button is always a real button
        int buttonIndex = mappedInput.getButtonIndex();
        if (buttonIndex >= 0)
            return controller.getButton(buttonIndex);
        else
            return false;
    }

    /**
     * returns current value of virtual axis
     * <p>
     * It is not checked if your configuredId is an axis.
     *
     * @param configuredId
     * @return current value
     */
    public float getConfiguredAxisValue(int configuredId) {
        if (controllerMapping == null)
            return 0;

        ControllerMappings.MappedInput mappedInput = controllerMapping.getMappedInput(configuredId);

        // not configured or not recorded
        if (mappedInput == null)
            return 0;

        ConfiguredInput.Type configuredInputType = mappedInput.getConfiguredInputType();

        // first check if a real axis is mapped
        int axisId = mappedInput.getAxisIndex();

        if (axisId >= 0) {
            float value = controller.getAxis(axisId);
            if (configuredInputType == ConfiguredInput.Type.axisDigital)
                return (Math.abs(value) < analogToDigitalTreshold ? 0 : 1f * Math.signum(value));
            else
                return value;
        }

        // axisAnalog only accepts real axis, so if not found don't look any further
        if (configuredInputType == ConfiguredInput.Type.axisAnalog)
            return 0;

        // if not a real axis, it could be a set of buttons or pov
        int buttonIndex = mappedInput.getButtonIndex();
        if (buttonIndex >= 0 && controller.getButton(buttonIndex))
            return 1f;
        int reverseButtonIndex = mappedInput.getReverseButtonIndex();
        if (reverseButtonIndex >= 0 && controller.getButton(reverseButtonIndex))
            return -1f;

        int povIdx = mappedInput.getPovIndex();
        if (povIdx >= 0) {
            boolean isVertical = mappedInput.getPovVertical();
            switch (controller.getPov(povIdx)) {
                case center:
                    return 0;
                case east:
                    return (isVertical ? 0 : 1f);
                case west:
                    return (isVertical ? 0 : -1f);
                case north:
                    return (isVertical ? -1f : 0);
                case south:
                    return (isVertical ? 1f : 0);
                case southEast:
                    return 1f;
                case southWest:
                    return (isVertical ? 1f : -1f);
                case northEast:
                    return (isVertical ? -1f : 1f);
                case northWest:
                    return -1f;
            }
        }

        return 0;
    }

    public String getControllerName() {
        return controller.getName();
    }
}
