package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FocusableImageTextButton extends ImageTextButton {

    public FocusableImageTextButton(String text, Skin skin) {
        super(text, skin);
    }

    public FocusableImageTextButton(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableImageTextButton(String text, ImageTextButtonStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
