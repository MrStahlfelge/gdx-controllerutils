package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

public class FocusableImageButton extends ImageButton {

    public FocusableImageButton(Skin skin) {
        super(skin);
    }

    public FocusableImageButton(Skin skin, String styleName) {
        super(skin, styleName);
    }

    public FocusableImageButton(ImageButtonStyle style) {
        super(style);
    }

    public FocusableImageButton(Drawable imageUp) {
        super(imageUp);
    }

    public FocusableImageButton(Drawable imageUp, Drawable imageDown) {
        super(imageUp, imageDown);
    }

    public FocusableImageButton(Drawable imageUp, Drawable imageDown, Drawable imageChecked) {
        super(imageUp, imageDown, imageChecked);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
