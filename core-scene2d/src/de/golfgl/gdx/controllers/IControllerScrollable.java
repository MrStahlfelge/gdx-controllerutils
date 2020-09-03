package de.golfgl.gdx.controllers;

/**
 * Groups that should be able to scroll via Controller buttons or keyboard keys can implement this interface.
 * {@link ControllerMenuStage} will then send callbacks to this Actor before setting the focus to an Actor outside.
 * <p>
 * See {@link ControllerScrollPane} for an example implementation. You can also use this for Lists or other custom
 * Actors.
 * <p>
 * Created by Benjamin Schulte on 05.02.2018.
 */

public interface IControllerScrollable {
    /**
     * Called when a key event was done and scroll should be performed
     * @param direction the direction to onControllerScroll to
     * @return if event was handled
     */
    boolean onControllerScroll(ControllerMenuStage.MoveFocusDirection direction);
}
