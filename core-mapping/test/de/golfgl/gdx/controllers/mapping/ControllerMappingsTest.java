package de.golfgl.gdx.controllers.mapping;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Benjamin Schulte on 05.11.2017.
 */
public class ControllerMappingsTest {

    @BeforeClass
    public static void init() {
        // Note that we don't need to implement any of the listener's methods
        Gdx.app = new HeadlessApplication(new ApplicationListener() {
            @Override
            public void create() {
            }

            @Override
            public void resize(int width, int height) {
            }

            @Override
            public void render() {
            }

            @Override
            public void pause() {
            }

            @Override
            public void resume() {
            }

            @Override
            public void dispose() {
            }
        });

        // Use Mockito to mock the OpenGL methods since we are running headlessly
        Gdx.gl20 = Mockito.mock(GL20.class);
        Gdx.gl = Gdx.gl20;
    }

    @Test
    public void testButtonMapping() {
        ControllerMappings mappings = new ControllerMappings();

        // We test 4 buttons
        ConfiguredInput button1 = new ConfiguredInput(ConfiguredInput.Type.button, 1);
        ConfiguredInput button2 = new ConfiguredInput(ConfiguredInput.Type.button, 2);
        ConfiguredInput button3 = new ConfiguredInput(ConfiguredInput.Type.button, 3);
        ConfiguredInput button4 = new ConfiguredInput(ConfiguredInput.Type.button, 4);

        mappings.addConfiguredInput(button1);
        mappings.addConfiguredInput(button2);
        mappings.addConfiguredInput(button3);
        mappings.addConfiguredInput(button4);

        // ok, configuration done...
        mappings.commitConfig();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.pressedButton = 107;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 1));
        //next call too fast
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 2));
        controller.pressedButton = 108;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 2));
        controller.pressedButton = 1;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 3));
        //TODO add assertion for check if record is complete
        assertFalse(mappings.getControllerMapping(controller).checkCompleted());
        controller.pressedButton = 4;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 4));
        controller.pressedButton = -1;
        assertTrue(mappings.getControllerMapping(controller).checkCompleted());

        System.out.println(mappings.toJson().toJson(JsonWriter.OutputType.json));

        // now check
        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);

        assertTrue(controllerAdapter.buttonDown(controller, 108));
        assertEquals(2, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.buttonDown(controller, 4));
        assertEquals(4, controllerAdapter.lastEventId);
        assertFalse(controllerAdapter.buttonDown(controller, 2));
        assertEquals(4, controllerAdapter.lastEventId);

        MappedController mappedController = new MappedController(controller, mappings);
        controller.pressedButton = 4;
        assertTrue(mappedController.isButtonPressed(4));
        controller.pressedButton = 108;
        assertTrue(mappedController.isButtonPressed(2));
        controller.pressedButton = 99;
        assertFalse(mappedController.isButtonPressed(2));
        assertFalse(mappedController.isButtonPressed(5));

    }

    @Test
    public void testAxisToAxisMapping() {
        ControllerMappings mappings = new ControllerMappings();

        // We test 3 axis
        ConfiguredInput axis1 = new ConfiguredInput(ConfiguredInput.Type.axis, 5);
        ConfiguredInput axis2 = new ConfiguredInput(ConfiguredInput.Type.axisAnalog, 6);
        ConfiguredInput axis3 = new ConfiguredInput(ConfiguredInput.Type.axisDigital, 7);

        mappings.addConfiguredInput(axis1);
        mappings.addConfiguredInput(axis2);
        mappings.addConfiguredInput(axis3);

        // ok, configuration done...
        mappings.commitConfig();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.axisValues = new float[3];
        controller.povDirection = PovDirection.center;
        controller.axisValues[0] = .2f;
        controller.axisValues[1] = .6f;
        controller.axisValues[2] = -.2f;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 5));
        //next call too fast
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 6));

        controller.axisValues[0] = .2f;
        controller.axisValues[1] = .1f;
        controller.axisValues[2] = 0;
        assertEquals(ControllerMappings.RecordResult.nothing_done, mappings.recordMapping(controller, 6));

        controller.axisValues[0] = .6f;
        controller.axisValues[1] = .5f;
        controller.axisValues[2] = 0;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 6));

        controller.axisValues[0] = .6f;
        controller.axisValues[1] = .5f;
        controller.axisValues[2] = -1;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 7));

        assertTrue(mappings.getControllerMapping(controller).checkCompleted());

        System.out.println(mappings.toJson().toJson(JsonWriter.OutputType.json));

        // now check
        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);

        // the digital
        assertTrue(controllerAdapter.axisMoved(controller, 2, .8f));
        assertEquals(7, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.axisMoved(controller, 2, -.8f));
        assertEquals(7, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.axisMoved(controller, 2, -.2f));
        assertEquals(7, controllerAdapter.lastEventId);

        assertTrue(controllerAdapter.axisMoved(controller, 1, .8f));
        assertEquals(5, controllerAdapter.lastEventId);

        assertTrue(controllerAdapter.axisMoved(controller, 0, -.9f));
        assertEquals(6, controllerAdapter.lastEventId);

        MappedController mappedController = new MappedController(controller, mappings);
        controller.axisValues[0] = .6f;
        controller.axisValues[1] = .5f;
        controller.axisValues[2] = -.7f;
        assertEquals(.6f, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(.5f, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(-1f, mappedController.getConfiguredAxisValue(7), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(3), .01f);
    }

    @Test
    public void testButtonToAxisMapping() {
        ControllerMappings mappings = new ControllerMappings();

        // We test 3 axis
        ConfiguredInput axis1 = new ConfiguredInput(ConfiguredInput.Type.axis, 5);
        ConfiguredInput axis2 = new ConfiguredInput(ConfiguredInput.Type.axisAnalog, 6);
        ConfiguredInput axis3 = new ConfiguredInput(ConfiguredInput.Type.axisDigital, 7);

        mappings.addConfiguredInput(axis1);
        mappings.addConfiguredInput(axis2);
        mappings.addConfiguredInput(axis3);

        // ok, configuration done...
        mappings.commitConfig();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.pressedButton = 0;
        controller.axisValues = new float[3];

        assertEquals(ControllerMappings.RecordResult.need_second_button, mappings.recordMapping(controller, 5));
        //next call too fast
        assertEquals(ControllerMappings.RecordResult.not_added_need_button, mappings.recordMapping(controller, 5));
        //now an axis is moved
        controller.axisValues[1] = .5f;
        controller.pressedButton = -1;
        assertEquals(ControllerMappings.RecordResult.not_added_need_button, mappings.recordMapping(controller, 5));
        controller.pressedButton = 1;
        controller.axisValues[1] = 0f;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 5));

        // analog do not accept buttons
        controller.pressedButton = 2;
        assertEquals(ControllerMappings.RecordResult.nothing_done, mappings.recordMapping(controller, 6));

        controller.pressedButton = 1;
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 7));
        controller.pressedButton = 3;
        assertEquals(ControllerMappings.RecordResult.need_second_button, mappings.recordMapping(controller, 7));
        controller.pressedButton = 4;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 7));

        assertFalse(mappings.getControllerMapping(controller).checkCompleted());

        System.out.println(mappings.toJson().toJson(JsonWriter.OutputType.json));

        // now check
        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);

        // the digital
        assertTrue(controllerAdapter.buttonDown(controller, 0));
        assertEquals(5, controllerAdapter.lastEventId);

        assertTrue(controllerAdapter.buttonDown(controller, 1));
        assertEquals(5, controllerAdapter.lastEventId);

        assertTrue(controllerAdapter.buttonDown(controller, 3));
        assertEquals(7, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.buttonUp(controller, 1));
        assertEquals(5, controllerAdapter.lastEventId);
        assertFalse(controllerAdapter.buttonUp(controller, 2));
        assertEquals(5, controllerAdapter.lastEventId);

        MappedController mappedController = new MappedController(controller, mappings);
        controller.pressedButton = 5;
        assertEquals(0, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(7), .01f);

        controller.pressedButton = 3;
        assertEquals(0, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(1, mappedController.getConfiguredAxisValue(7), .01f);

        controller.pressedButton = 4;
        assertEquals(0, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(-1, mappedController.getConfiguredAxisValue(7), .01f);
    }

    @Test
    public void testPovToAxisMapping() {
        ControllerMappings mappings = new ControllerMappings();

        // We test 3 axis
        ConfiguredInput axis1 = new ConfiguredInput(ConfiguredInput.Type.axis, 5);
        ConfiguredInput axis2 = new ConfiguredInput(ConfiguredInput.Type.axisAnalog, 6);
        ConfiguredInput axis3 = new ConfiguredInput(ConfiguredInput.Type.axisDigital, 7);

        mappings.addConfiguredInput(axis1);
        mappings.addConfiguredInput(axis2);
        mappings.addConfiguredInput(axis3);

        // ok, configuration done...
        mappings.commitConfig();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.axisValues = new float[3];
        controller.povDirection = PovDirection.east;

        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 5));
        //next call too fast
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 7));
        //not two directions
        controller.povDirection = PovDirection.northWest;
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 7));

        //not same direction on different configurations
        controller.povDirection = PovDirection.west;
        assertEquals(ControllerMappings.RecordResult.not_added, mappings.recordMapping(controller, 7));

        //not for analog axis
        controller.povDirection = PovDirection.north;
        assertEquals(ControllerMappings.RecordResult.nothing_done, mappings.recordMapping(controller, 6));

        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 7));

        assertFalse(mappings.getControllerMapping(controller).checkCompleted());

        System.out.println(mappings.toJson().toJson(JsonWriter.OutputType.json));

        // now check
        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);

        assertFalse(controllerAdapter.povMoved(controller, 1, PovDirection.center));

        // the digital
        assertTrue(controllerAdapter.povMoved(controller, 0, PovDirection.center));
        assertTrue(controllerAdapter.lastEventId == 5 || controllerAdapter.lastEventId == 7);

        assertTrue(controllerAdapter.povMoved(controller, 0, PovDirection.east));
        assertTrue(controllerAdapter.lastEventId == 5 || controllerAdapter.lastEventId == 7);

        assertTrue(controllerAdapter.povMoved(controller, 0, PovDirection.southEast));
        assertTrue(controllerAdapter.lastEventId == 5 || controllerAdapter.lastEventId == 7);

        assertTrue(controllerAdapter.povMoved(controller, 0, PovDirection.west));
        assertTrue(controllerAdapter.lastEventId == 5 || controllerAdapter.lastEventId == 7);

        MappedController mappedController = new MappedController(controller, mappings);
        controller.povDirection = PovDirection.center;
        assertEquals(0, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(7), .01f);

        controller.povDirection = PovDirection.north;
        assertEquals(0, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(-1, mappedController.getConfiguredAxisValue(7), .01f);

        controller.povDirection = PovDirection.east;
        assertEquals(1, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(7), .01f);

        controller.povDirection = PovDirection.southWest;
        assertEquals(-1, mappedController.getConfiguredAxisValue(5), .01f);
        assertEquals(0, mappedController.getConfiguredAxisValue(6), .01f);
        assertEquals(1, mappedController.getConfiguredAxisValue(7), .01f);
    }

    @Test
    public void testDefaultMapping() {
        ControllerMappings mappings = new ControllerMappings() {
            @Override
            public boolean getDefaultMapping(MappedInputs defaultMapping) {
                defaultMapping.putMapping(new MappedInput(0, new ControllerAxis(5)));
                defaultMapping.putMapping(new MappedInput(1, new ControllerButton(2)));
                return true;
            }
        };

        ConfiguredInput axis = new ConfiguredInput(ConfiguredInput.Type.axis, 0);
        ConfiguredInput button = new ConfiguredInput(ConfiguredInput.Type.button, 1);

        mappings.addConfiguredInput(axis);
        mappings.addConfiguredInput(button);

        // ok, configuration done...
        mappings.commitConfig();

        // now check
        MockedController controller = new MockedController();


        MappedController mappedController = new MappedController(controller, mappings);
        controller.pressedButton = 2;
        assertTrue(mappedController.isButtonPressed(1));
        controller.pressedButton = 1;
        assertFalse(mappedController.isButtonPressed(1));

        TestControllerAdapter controllerAdapter = new TestControllerAdapter(mappings);
        assertTrue(controllerAdapter.buttonDown(controller, 2));
        assertEquals(1, controllerAdapter.lastEventId);
        assertTrue(controllerAdapter.axisMoved(controller, 5, .5f));
        assertEquals(0, controllerAdapter.lastEventId);

        // serialize gives only recorded mappings
        assertEquals("[]", mappings.toJson().toJson(JsonWriter.OutputType.json));
    }

    @Test
    public void testJsonSaveLoad() {
        ControllerMappings mappings = new ControllerMappings();

        mappings.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.button, 1));
        mappings.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, 2));
        mappings.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, 3));
        mappings.addConfiguredInput(new ConfiguredInput(ConfiguredInput.Type.axis, 4));

        mappings.commitConfig();

        // now we connect a Controller... and map
        MockedController controller = new MockedController();
        controller.axisValues = new float[3];
        controller.povDirection = PovDirection.center;
        controller.axisValues[0] = 1f;
        controller.pressedButton = -1;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 2));
        controller.pressedButton = 2;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 1));
        controller.pressedButton = 1;
        assertEquals(ControllerMappings.RecordResult.need_second_button, mappings.recordMapping(controller, 3));
        controller.pressedButton = 0;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 3));
        controller.pressedButton = -1;
        controller.povDirection = PovDirection.east;
        assertEquals(ControllerMappings.RecordResult.recorded, mappings.recordMapping(controller, 4));

        JsonValue json = mappings.toJson();
        System.out.println(json.toJson(JsonWriter.OutputType.json));
        MappedController mappedController = new MappedController(controller, mappings);
        mappings.resetMappings(controller);

        mappedController.refreshMappingCache();
        assertEquals(0, mappedController.getConfiguredAxisValue(4), 0.1f);

        mappings.fillFromJson(json);
        mappedController.refreshMappingCache();

        assertEquals(1, mappedController.getConfiguredAxisValue(4), 0.1f);
        controller.pressedButton = 2;
        assertTrue(mappedController.isButtonPressed(1));
        controller.pressedButton = 1;
        assertEquals(1, mappedController.getConfiguredAxisValue(3), 0.1f);
    }

    public class TestControllerAdapter extends MappedControllerAdapter {
        public int lastEventId = -1;

        public TestControllerAdapter(ControllerMappings mappings) {
            super(mappings);
        }

        @Override
        public boolean configuredButtonDown(Controller controller, int buttonId) {
            lastEventId = buttonId;
            System.out.println("Button down: " + controller.getName() + ":" + buttonId);
            return true;
        }

        @Override
        public boolean configuredAxisMoved(Controller controller, int axisId, float value) {
            System.out.println("Axis moved: " + controller.getName() + ":" + axisId + " " + String.valueOf(value));
            lastEventId = axisId;
            return true;
        }
    }

    public class MockedController implements Controller {

        public int pressedButton = -1;
        public float[] axisValues;
        public PovDirection povDirection;

        @Override
        public boolean getButton(int buttonCode) {
            return (pressedButton == buttonCode);
        }

        @Override
        public float getAxis(int axisCode) {
            if (axisCode >= 0 && axisCode < axisValues.length)
                return axisValues[axisCode];
            return 0;
        }

        @Override
        public PovDirection getPov(int povCode) {
            return (povCode != 0 ? PovDirection.center : povDirection);
        }

        @Override
        public boolean getSliderX(int sliderCode) {
            return false;
        }

        @Override
        public boolean getSliderY(int sliderCode) {
            return false;
        }

        @Override
        public Vector3 getAccelerometer(int accelerometerCode) {
            return null;
        }

        @Override
        public void setAccelerometerSensitivity(float sensitivity) {

        }

        @Override
        public String getName() {
            return "TEST";
        }

        @Override
        public void addListener(ControllerListener listener) {

        }

        @Override
        public void removeListener(ControllerListener listener) {

        }
    }
}