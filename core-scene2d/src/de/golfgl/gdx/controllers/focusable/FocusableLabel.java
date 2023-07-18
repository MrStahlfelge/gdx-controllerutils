package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FocusableLabel extends Label {

    public FocusableLabel(CharSequence text, Skin skin) {
        super(text, skin);
    }

    public FocusableLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
    }

    public FocusableLabel(CharSequence text, Skin skin, String fontName, String colorName) {
        super(text, skin, fontName, colorName);
    }

    public FocusableLabel(CharSequence text, LabelStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
