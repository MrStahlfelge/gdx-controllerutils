package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class FocusableTextField extends TextField {

    public FocusableTextField(String text, Skin skin) {
        super(text, skin);
    }

    public FocusableTextField(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableTextField(String text, TextFieldStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
