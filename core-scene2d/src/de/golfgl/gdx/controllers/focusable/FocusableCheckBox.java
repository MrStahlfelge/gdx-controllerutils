package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class FocusableCheckBox extends CheckBox {
    public FocusableCheckBox(String text, Skin skin) {
        super(text, skin);
    }

    public FocusableCheckBox(String text, Skin skin, String styleName) {
        super(text, skin, styleName);
    }

    public FocusableCheckBox(String text, CheckBoxStyle style) {
        super(text, style);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
