package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.utils.JsonValue;

import java.util.HashMap;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMappings {
    public static final String LOG_TAG = "CONTROLLERMAPPING";
    public float analogToDigitalTreshold = .5f;
    /**
     * some Gamepads report analog axis from .997 to 1.03...
     */
    public float maxAcceptedAnalogValue = 1.1f;
    /**
     * this holds all inputs defined by the game
     */
    private HashMap<Integer, ConfiguredInput> configuredInputs;
    /**
     * this holds defined input mappings for every Controller (String is its name)
     */
    private HashMap<String, MappedInputs> mappedInputs;
    private boolean initialized;
    private int waitingForReverseButtonAxisId = -1;
    private int waitingForReverseButtonFirstIdx = -1;

    public static int findHighAxisValue(Controller controller, float analogToDigitalTreshold,
                                        float maxAcceptedAnalogValue) {
        // Cycle through axis indexes to check if there is a high value
        float highestValue = 0;
        int axisWithHighestValue = -1;

        for (int i = 0; i <= 500; i++) {
            float abs = Math.abs(controller.getAxis(i));
            if (abs > highestValue && abs >= analogToDigitalTreshold && abs <= maxAcceptedAnalogValue) {
                highestValue = abs;
                axisWithHighestValue = i;
            }
        }

        return axisWithHighestValue;
    }

    public static int findPressedButton(Controller controller) {
        // Cycle through button indexes to check if a button is pressed
        // Some gamepads report buttons from 90 to 107, so we check up to index 500
        // this should be moved into controller implementation which knows it better
        for (int i = 0; i <= 500; i++)
            if (controller.getButton(i))
                return i;

        return -1;
    }

    /**
     * @return all mappings as a json value
     */
    public JsonValue toJson() {
        JsonValue json = new JsonValue(JsonValue.ValueType.array);
        if (mappedInputs != null)
            for (MappedInputs controllerMapping : mappedInputs.values())
                if (controllerMapping.isRecorded) {
                    JsonValue controllerJson = new JsonValue(JsonValue.ValueType.object);
                    controllerJson.addChild("name", new JsonValue(controllerMapping.getControllerName()));
                    controllerJson.addChild("mapping", controllerMapping.toJson());
                    json.addChild(controllerJson);
                }

        return json;
    }

    public boolean fillFromJson(JsonValue json) {
        // initialize mapping map and controller information if not already present
        if (mappedInputs == null)
            mappedInputs = new HashMap<>();

        for (JsonValue controllerJson = json.child; controllerJson != null; controllerJson = controllerJson.next) {
            String controllerName = controllerJson.getString("name");

            if (mappedInputs.containsKey(controllerName))
                mappedInputs.remove(controllerName);

            MappedInputs newMapping = new MappedInputs(controllerName);
            mappedInputs.put(controllerName, newMapping);

            newMapping.isRecorded = true;

            for (JsonValue mappingsJson = controllerJson.get("mapping").child; mappingsJson != null;
                 mappingsJson = mappingsJson.next) {

                int confId = mappingsJson.getInt("confId");

                if (mappingsJson.has("axis")) {
                    newMapping.putMapping(new MappedInput(confId, new ControllerAxis(mappingsJson.getInt("axis"))));
                } else if (mappingsJson.has("pov")) {
                    newMapping.putMapping(new MappedInput(confId, new ControllerPovButton(mappingsJson.getInt("pov"),
                            mappingsJson.getBoolean("vertical"))));
                } else if (mappingsJson.has("buttonR")) {
                    newMapping.putMapping(new MappedInput(confId, new ControllerButton(mappingsJson.getInt("button")),
                            new ControllerButton(mappingsJson.getInt("buttonR"))));
                } else if (mappingsJson.has("button")) {
                    newMapping.putMapping(new MappedInput(confId, new ControllerButton(mappingsJson.getInt("button"))));
                }
            }
        }

        return true;
    }

    protected MappedInputs getControllerMapping(Controller controller) {
        if (!initialized)
            throw new IllegalStateException("Call commitConfig() before creating Controller Listeners");

        MappedInputs retVal = null;

        // initialize mapping map and controller information if not already present
        if (mappedInputs == null)
            mappedInputs = new HashMap<>();


        retVal = mappedInputs.get(controller.getName());

        // in case the controller is not recorded or loaded already, initialize it
        if (retVal == null) {
            MappedInputs defaultMapping = new MappedInputs(controller);
            if (getDefaultMapping(defaultMapping)) {
                retVal = defaultMapping;
                mappedInputs.put(retVal.controllerName, retVal);
            }
        }

        return retVal;
    }

    /**
     * use this method to define a default mapping for your controllers. You can check for the controller's name
     * by calling {@link MappedInputs#getControllerName()}
     * <p>
     * The method is only called if mapping for the controller is needed, but not found
     *
     * @param defaultMapping Use {@link MappedInputs#putMapping(MappedInput)} on this to define default mappings
     * @return true if default mappings were defined and should be used
     */
    public boolean getDefaultMapping(MappedInputs defaultMapping) {
        //nothing - just override it for your desires
        return false;
    }

    public ControllerMappings addConfiguredInput(ConfiguredInput configuredInput) {
        if (initialized)
            throw new IllegalStateException("Changing config not allowed after commitConfig() is called");

        if (configuredInputs == null)
            configuredInputs = new HashMap<>();

        configuredInputs.put(configuredInput.inputId, configuredInput);

        return this;
    }

    /**
     * call this when configuration is done
     */
    public void commitConfig() {
        initialized = true;
    }

    /**
     * resets mapping for the given controller. Warning: already instantiated {@link MappedController} will still hold
     * a reference to the old mapping, so be sure to refresh such references with its
     * {@link MappedController#refreshMappingCache()}.
     * {@link MappedControllerAdapter} is not concerned.
     *
     * @param controller
     */
    public void resetMappings(Controller controller) {
        if (mappedInputs == null)
            return;

        mappedInputs.remove(controller.getName());

        waitingForReverseButtonFirstIdx = -1;
        waitingForReverseButtonAxisId = -1;
    }

    /**
     * Record a mapping. Don't call this in every render call, although it should make no problems users won't be so
     * fast.
     *
     * @param controller        controller to listen to
     * @param configuredInputId configured button or axis to record
     * @return {@link RecordResult#nothing_done} if nothing was done, {@link RecordResult#not_added} if buttons were
     * pressed but could not be added, {@link RecordResult#need_second_button} if axis was mapped to button and next
     * call must be for an axis is the reverse button, {@link RecordResult#not_added_need_button} if is waiting for
     * the second button but no valid was pressed
     * {@link RecordResult#recorded} if a button mapping was added
     */
    public RecordResult recordMapping(Controller controller, int configuredInputId) {
        if (!initialized)
            throw new IllegalStateException("Recording not allowed before commitConfig() is called");
        ConfiguredInput configuredInput = configuredInputs.get(configuredInputId);

        // initialize mapping map and controller information if not already present
        if (mappedInputs == null)
            mappedInputs = new HashMap<>();

        if (!mappedInputs.containsKey(controller.getName()))
            mappedInputs.put(controller.getName(), new MappedInputs(controller));

        MappedInputs mappedInput = getControllerMapping(controller);
        mappedInput.isRecorded = true;

        switch (configuredInput.inputType) {
            case button:
                int buttonIndex = findPressedButton(controller);

                if (buttonIndex >= 0) {
                    // we found our button, hopefully
                    boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                            new ControllerButton(buttonIndex)));

                    return (added ? RecordResult.recorded : RecordResult.not_added);
                } else
                    return RecordResult.nothing_done;
            case axis:
            case axisDigital:
                // check if a button is already there, then we need to set the reverse button
                int foundButtonIndex = findPressedButton(controller);
                if (foundButtonIndex >= 0) {
                    if (waitingForReverseButtonAxisId == configuredInputId) {
                        //this is the reverse button
                        boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                                new ControllerButton(waitingForReverseButtonFirstIdx),
                                new ControllerButton(foundButtonIndex)));
                        if (added) {
                            waitingForReverseButtonAxisId = -1;
                            waitingForReverseButtonFirstIdx = -1;
                            return RecordResult.recorded;
                        } else
                            return RecordResult.not_added_need_button;
                    } else if (mappedInput.isButtonInMapping(foundButtonIndex))
                        return RecordResult.not_added;
                    else {
                        // this is the first button, so remember state for next call
                        waitingForReverseButtonAxisId = configuredInputId;
                        waitingForReverseButtonFirstIdx = foundButtonIndex;
                        return RecordResult.need_second_button;
                    }
                } else if (waitingForReverseButtonAxisId == configuredInputId)
                    return RecordResult.not_added_need_button;

                // TODO support more than one pov
                switch (controller.getPov(0)) {
                    case east:
                    case west:
                        return (mappedInput.putMapping(new MappedInput(configuredInputId,
                                new ControllerPovButton(0, false))) ? RecordResult.recorded : RecordResult.not_added);
                    case north:
                    case south:
                        return (mappedInput.putMapping(new MappedInput(configuredInputId,
                                new ControllerPovButton(0, true))) ? RecordResult.recorded : RecordResult.not_added);
                    case northEast:
                    case northWest:
                    case southEast:
                    case southWest:
                        // two directions not supported
                        return RecordResult.not_added;
                }

                // no break here on purpose!
            case axisAnalog:
                int axisIndex = findHighAxisValue(controller, analogToDigitalTreshold, maxAcceptedAnalogValue);

                if (axisIndex >= 0) {
                    boolean added = mappedInput.putMapping(new MappedInput(configuredInputId,
                            new ControllerAxis(axisIndex)));

                    return (added ? RecordResult.recorded : RecordResult.not_added);
                } else
                    return RecordResult.nothing_done;

            default:
                return RecordResult.nothing_done;
        }

    }

    public enum RecordResult {recorded, nothing_done, not_added, need_second_button, not_added_need_button}

    public static abstract class ControllerInput {
        public static final char PREFIX_BUTTON = 'B';
        public static final char PREFIX_AXIS = 'A';
        public static final char PREFIX_POV = 'P';
    }

    public static class ControllerButton extends ControllerInput {
        public int buttonIndex;

        public ControllerButton(int buttonIndex) {
            this.buttonIndex = buttonIndex;
        }
    }

    public static class ControllerAxis extends ControllerInput {
        public int axisIndex;

        public ControllerAxis(int axisIndex) {
            this.axisIndex = axisIndex;
        }
    }

    public static class ControllerPovButton extends ControllerInput {
        public static final String VERTICAL = "V";
        public int povIndex;
        public boolean povDirectionVertical;

        public ControllerPovButton(int povIndex, boolean isVerticalDirection) {
            this.povDirectionVertical = isVerticalDirection;
            this.povIndex = povIndex;
        }

        public int getKey() {
            return povIndex * 10 + (povDirectionVertical ? 1 : 0);
        }
    }

    /**
     * A single input mapping definition for one ConfiguredInput and one Controller
     */
    protected class MappedInput {
        /**
         * the configured input this mapping is referring to
         */
        private int configuredInputId;
        private ControllerInput controllerInput;
        // if an axis is simulated by two buttons, second one is needed
        private ControllerButton secondButtonForAxis;

        public MappedInput(int configuredInputId, ControllerInput controllerInput) {
            this.configuredInputId = configuredInputId;
            this.controllerInput = controllerInput;
        }

        public MappedInput(int configuredInputId, ControllerButton controllerInput, ControllerButton
                reverseButton) {
            this.configuredInputId = configuredInputId;
            this.controllerInput = controllerInput;
            this.secondButtonForAxis = reverseButton;
        }

        /**
         * returns the real button index from a configured virtual button or axis
         *
         * @return the real button id, or -1 if this mapping is no button
         */
        public int getButtonIndex() {
            if (controllerInput instanceof ControllerButton)
                return ((ControllerButton) controllerInput).buttonIndex;

            return -1;
        }

        /**
         * returns the real axis from a configured axis.
         *
         * @return real axis id, or -1 if not available
         */
        public int getAxisIndex() {
            if (controllerInput instanceof ControllerAxis)
                return ((ControllerAxis) controllerInput).axisIndex;
            return -1;
        }

        /**
         * @return the pov index from a configured pov.
         */
        public int getPovIndex() {
            if (controllerInput instanceof ControllerPovButton)
                return ((ControllerPovButton) controllerInput).povIndex;
            return -1;
        }

        public boolean getPovVertical() {
            if (controllerInput instanceof ControllerPovButton)
                return ((ControllerPovButton) controllerInput).povDirectionVertical;
            return false;
        }

        public ConfiguredInput.Type getConfiguredInputType() {
            return configuredInputs.get(configuredInputId).inputType;
        }

        public int getReverseButtonIndex() {
            if (secondButtonForAxis != null)
                return secondButtonForAxis.buttonIndex;

            return -1;
        }
    }

    /**
     * Input mappings for a single controller. Class is protected and not for accessing from outside.
     * Mappings are constructed via {@link #recordMapping(Controller, int)}
     */
    protected class MappedInputs {
        public boolean isRecorded;
        private String controllerName;
        private HashMap<Integer, MappedInput> mappingsByConfigured;
        private HashMap<Integer, MappedInput> mappingsByButton;
        private HashMap<Integer, MappedInput> mappingsByAxis;
        private HashMap<Integer, MappedInput> mappingsByPov;

        private MappedInputs(Controller controller) {
            this(controller.getName());
        }

        private MappedInputs(String controllerName) {
            this.controllerName = controllerName;
            mappingsByConfigured = new HashMap<>(mappedInputs.size());
            mappingsByButton = new HashMap<>(mappedInputs.size());
            mappingsByAxis = new HashMap<>(mappedInputs.size());
            mappingsByPov = new HashMap<>(2);
        }

        public boolean checkCompleted() {
            boolean completed = true;
            for (Integer configureId : configuredInputs.keySet())
                completed = completed && mappingsByConfigured.containsKey(configureId);

            return completed;
        }

        public String getControllerName() {
            return controllerName;
        }

        /**
         * add a new mapping
         *
         * @param mapping
         * @return true if this was possible, false if mapping could not be added
         */
        public boolean putMapping(MappedInput mapping) {
            if (mappingsByConfigured.containsKey(mapping.configuredInputId))
                return false;

            if (mapping.controllerInput instanceof ControllerButton) {
                ControllerButton controllerButton = (ControllerButton) mapping.controllerInput;
                if (mappingsByButton.containsKey(controllerButton.buttonIndex))
                    return false;

                if (mapping.secondButtonForAxis != null &&
                        mappingsByButton.containsKey((mapping.secondButtonForAxis).buttonIndex))
                    return false;

                // just in case reverse and first button are the same...
                if (mapping.secondButtonForAxis != null &&
                        controllerButton.buttonIndex == mapping.secondButtonForAxis.buttonIndex)
                    return false;

                mappingsByButton.put(controllerButton.buttonIndex, mapping);
                if (mapping.secondButtonForAxis != null)
                    mappingsByButton.put(mapping.secondButtonForAxis.buttonIndex, mapping);

            } else if (mapping.controllerInput instanceof ControllerAxis) {
                ControllerAxis controllerAxis = (ControllerAxis) mapping.controllerInput;
                if (mappingsByAxis.containsKey(controllerAxis.axisIndex))
                    return false;

                mappingsByAxis.put(controllerAxis.axisIndex, mapping);

            } else if (mapping.controllerInput instanceof ControllerPovButton) {
                ControllerPovButton controllerPov = (ControllerPovButton) mapping.controllerInput;
                if (mappingsByPov.containsKey(controllerPov.getKey()))
                    return false;

                mappingsByPov.put(controllerPov.getKey(), mapping);

            } else
                return false;

            mappingsByConfigured.put(mapping.configuredInputId, mapping);

            return true;
        }

        /**
         * Save mapping for this controller instance
         *
         * @return
         */
        public JsonValue toJson() {
            JsonValue json = new JsonValue(JsonValue.ValueType.array);
            for (MappedInput mapping : mappingsByConfigured.values()) {
                JsonValue jsonmaping = new JsonValue(JsonValue.ValueType.object);
                jsonmaping.addChild("confId", new JsonValue(mapping.configuredInputId));
                if (mapping.controllerInput instanceof ControllerAxis)
                    jsonmaping.addChild("axis", new JsonValue(((ControllerAxis) mapping.controllerInput).axisIndex));
                else if (mapping.controllerInput instanceof ControllerButton) {
                    jsonmaping.addChild("button",
                            new JsonValue(((ControllerButton) mapping.controllerInput).buttonIndex));
                    if (mapping.secondButtonForAxis != null)
                        jsonmaping.addChild("buttonR", new JsonValue((mapping.secondButtonForAxis.buttonIndex)));
                } else if (mapping.controllerInput instanceof ControllerPovButton) {
                    jsonmaping.addChild("pov", new JsonValue(((ControllerPovButton) mapping.controllerInput).povIndex));
                    jsonmaping.addChild("vertical",
                            new JsonValue(((ControllerPovButton) mapping.controllerInput).povDirectionVertical));
                }
                json.addChild(jsonmaping);
            }
            return json;
        }

        /**
         * returns if a certain button is already in mapping
         *
         * @param buttonIndex
         * @return
         */
        protected boolean isButtonInMapping(int buttonIndex) {
            return mappingsByButton.containsKey(buttonIndex);
        }

        public ConfiguredInput getConfiguredFromButton(int buttonIndex) {
            MappedInput mappedInput = mappingsByButton.get(buttonIndex);

            // if hit, check if it is not the reverse button
            if (mappedInput != null && (mappedInput.secondButtonForAxis == null ||
                    mappedInput.secondButtonForAxis.buttonIndex != buttonIndex))
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }

        public ConfiguredInput getConfiguredFromReverseButton(int buttonIndex) {
            MappedInput mappedInput = mappingsByButton.get(buttonIndex);

            // if hit, check if it is the reverse button
            if (mappedInput != null && mappedInput.secondButtonForAxis != null &&
                    mappedInput.secondButtonForAxis.buttonIndex == buttonIndex)
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }

        /**
         * returns mapped input for a configuration id, if present
         *
         * @param configuredId configuration id
         * @return MappedInput
         * @throws Exception when no mapping set
         */
        public MappedInput getMappedInput(int configuredId) {
            return mappingsByConfigured.get(configuredId);
        }

        public ConfiguredInput getConfiguredFromAxis(int axisIndex) {
            MappedInput mappedInput = mappingsByAxis.get(axisIndex);

            if (mappedInput != null)
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }

        public ConfiguredInput getConfiguredFromPov(int povIndex, boolean vertical) {
            MappedInput mappedInput = mappingsByPov.get(povIndex * 10 + (vertical ? 1 : 0));

            if (mappedInput != null)
                return configuredInputs.get(mappedInput.configuredInputId);
            else
                return null;
        }
    }

    //TODO vordefinierte XBox und (S)NES Definitionen
}
