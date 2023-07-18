package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.golfgl.gdx.controllers.ControllerMenuStage;

/**
 * Convenience wrapper for setting a focusable actor when the stage is typed {@link Stage}
 */
public class FocusableActorCompat {

    private FocusableActorCompat() {
    }

    /**
     * Usable in {@link Actor#setStage(Stage)} to set an actor as focusable without casting every time.
     */
    public static void setFocusable(Actor actor, Stage stage) {
        if (stage instanceof ControllerMenuStage) {
            ((ControllerMenuStage) stage).addFocusableActor(actor);
        }
    }
}
