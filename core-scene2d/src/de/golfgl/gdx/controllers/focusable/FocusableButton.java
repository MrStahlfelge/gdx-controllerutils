package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class FocusableButton extends Button {

    public FocusableButton(Skin skin) {
        super(skin);
    }

    public FocusableButton(Skin skin, String styleName) {
        super(skin, styleName);
    }

    public FocusableButton(Actor child, Skin skin, String styleName) {
        super(child, skin, styleName);
    }

    public FocusableButton(Actor child, ButtonStyle style) {
        super(child, style);
    }

    public FocusableButton(ButtonStyle style) {
        super(style);
    }

    public FocusableButton() {
    }

    public FocusableButton(Drawable up) {
        super(up);
    }

    public FocusableButton(Drawable up, Drawable down) {
        super(up, down);
    }

    public FocusableButton(Drawable up, Drawable down, Drawable checked) {
        super(up, down, checked);
    }

    public FocusableButton(Actor child, Skin skin) {
        super(child, skin);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
