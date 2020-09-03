package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * You can implement this interface on an Actor if you have to adjust the way
 * {@link ControllerMenuStage} determines the next focused Actor.
 */
public interface IControllerManageFocus {

    /**
     * called when the next focusable actor in a direction needs to be determined
     *
     * @param direction
     * @return Actor that should be focused, or null for automatic determination
     */
    Actor getNextFocusableActor(ControllerMenuStage.MoveFocusDirection direction);

    /**
     * called when the next focusable actor in a direction needs to be determined
     *
     * @param next true if next Actor in list should be focused, or false if previos Actor should be focused
     * @return Actor that should be focused, or null for automatic determination
     */
    Actor getNextFocusableActor(boolean next);

}
