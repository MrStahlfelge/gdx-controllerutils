package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMenuDialog extends Dialog {
    protected Array<Button> buttonsToAdd = new Array<>();
    protected Actor previousFocussedActor;
    protected Actor previousEscapeActor;

    public ControllerMenuDialog(String title, Skin skin) {
        super(title, skin);
    }

    public ControllerMenuDialog(String title, Skin skin, String windowStyleName) {
        super(title, skin, windowStyleName);
    }

    public ControllerMenuDialog(String title, WindowStyle windowStyle) {
        super(title, windowStyle);
    }

    @Override
    public Dialog button(Button button, Object object) {
        buttonsToAdd.add(button);
        if (getStage() != null && getStage() instanceof ControllerMenuStage)
            ((ControllerMenuStage) getStage()).addFocussableActor(button);
        return super.button(button, object);
    }

    @Override
    protected void setStage(Stage stage) {
        if (stage == null && getStage() != null && getStage() instanceof ControllerMenuStage) {
            for (int i = 0; i < buttonsToAdd.size; i++)
                ((ControllerMenuStage) getStage()).removeFocussableActor(buttonsToAdd.get(i));
        }

        super.setStage(stage);

        if (stage != null && stage instanceof ControllerMenuStage) {
            for (int i = 0; i < buttonsToAdd.size; i++)
                ((ControllerMenuStage) stage).addFocussableActor(buttonsToAdd.get(i));
        }
    }

    @Override
    public Dialog show(Stage stage, Action action) {
        previousFocussedActor = null;
        previousEscapeActor = null;

        super.show(stage, action);

        if (stage instanceof ControllerMenuStage) {
            previousFocussedActor = ((ControllerMenuStage) stage).getFocussedActor();
            previousEscapeActor = ((ControllerMenuStage) stage).getEscapeActor();

            if (buttonsToAdd.size >= 1)
                ((ControllerMenuStage) stage).setFocussedActor(buttonsToAdd.get(0));

            if (buttonsToAdd.size == 1)
                ((ControllerMenuStage) stage).setEscapeActor(buttonsToAdd.get(0));
        }

        return this;

    }

    @Override
    public void hide(Action action) {
        if (getStage() != null && getStage() instanceof ControllerMenuStage) {
            Actor currentFocussedActor = ((ControllerMenuStage) getStage()).getFocussedActor();
            if (previousFocussedActor != null && previousFocussedActor.getStage() == getStage()
                    && (currentFocussedActor == null || currentFocussedActor.isDescendantOf(this)))
                ((ControllerMenuStage) getStage()).setFocussedActor(previousFocussedActor);
            Actor currentEscapeActor = ((ControllerMenuStage) getStage()).getEscapeActor();
            if (previousEscapeActor != null && previousEscapeActor.getStage() == getStage()
                    && (currentEscapeActor == null || currentEscapeActor.isDescendantOf(this)))
                ((ControllerMenuStage) getStage()).setEscapeActor(previousEscapeActor);
        }

        super.hide(action);
    }
}
