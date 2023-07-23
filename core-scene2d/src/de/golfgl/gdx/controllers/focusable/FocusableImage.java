package de.golfgl.gdx.controllers.focusable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

public class FocusableImage extends Image {

    public FocusableImage() {
    }

    public FocusableImage(NinePatch patch) {
        super(patch);
    }

    public FocusableImage(TextureRegion region) {
        super(region);
    }

    public FocusableImage(Texture texture) {
        super(texture);
    }

    public FocusableImage(Skin skin, String drawableName) {
        super(skin, drawableName);
    }

    public FocusableImage(Drawable drawable) {
        super(drawable);
    }

    public FocusableImage(Drawable drawable, Scaling scaling) {
        super(drawable, scaling);
    }

    public FocusableImage(Drawable drawable, Scaling scaling, int align) {
        super(drawable, scaling, align);
    }

    @Override
    protected void setStage(Stage stage) {
        super.setStage(stage);
        FocusableActorCompat.setFocusable(this, stage);
    }
}
