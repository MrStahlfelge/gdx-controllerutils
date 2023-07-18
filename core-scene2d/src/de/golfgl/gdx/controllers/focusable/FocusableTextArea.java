package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;

public class FocusableTextArea extends TextArea {

    public FocusableTextArea(String text, Skin skin) {
        super(text, skin);
    }

    public FocusableTextArea(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableTextArea(String text, TextFieldStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
