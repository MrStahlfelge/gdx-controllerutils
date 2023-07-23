package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

public class FocusableWidgetGroup extends WidgetGroup {

    public FocusableWidgetGroup() {
    }

    public FocusableWidgetGroup(Actor... actors) {
        super(actors);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
