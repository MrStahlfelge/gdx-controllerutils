package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

/**
 * This class can be used for Actors where the controller default button should trigger an other action than firing
 * a touch down or touch up event. See {@link ControllerMenuStage#fireEventOnActor(Actor, InputEvent.Type, int, Actor)}
 * and {@link ControllerSlider} for an example
 * <p>
 * Created by Benjamin Schulte on 08.02.2018.
 */

public interface IControllerActable {
    /**
     * called when the controller/keyboard default key was pressed down
     *
     * @return if event was handled
     */
    boolean onControllerDefaultKeyDown();

    /**
     * called when the controller/keyboard default key went up
     *
     * @return if event was handled
     */
    boolean onControllerDefaultKeyUp();
}
