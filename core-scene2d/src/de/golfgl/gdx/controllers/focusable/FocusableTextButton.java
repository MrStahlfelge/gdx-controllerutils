package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class FocusableTextButton extends TextButton {

    public FocusableTextButton(String text, Skin skin) {
        super(text, skin);
    }

    public FocusableTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableTextButton(String text, TextButtonStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
