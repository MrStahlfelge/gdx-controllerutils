package de.golfgl.gdx.controllers;

/**
 * Actors that are able to scroll via Controller buttons or keyboard keys can implement this interface.
 * {@link ControllerMenuStage} will then scroll this Actor before setting the focus to an Actor outside.
 * <p>
 * See {@link ControllerScrollablePane} for an example implementation. You can also use this for Lists or other custom
 * Actors.
 * <p>
 * Created by Benjamin Schulte on 05.02.2018.
 */

public interface IControllerScrollable {
    /**
     * Performs a scroll
     * @param direction the direction to scroll to
     * @return if a scroll was performed
     */
    boolean scroll(ControllerMenuStage.MoveFocusDirection direction);
}
