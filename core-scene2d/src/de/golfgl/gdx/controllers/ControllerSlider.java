package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;

/**
 * Created by Benjamin Schulte on 08.02.2018.
 */

public class ControllerSlider extends Slider implements IControllerActable, IControllerScrollable {
    // can someone explain me while subclasses can't access important members of Scene2d Actors?
    protected boolean isVertical;

    public ControllerSlider(float min, float max, float stepSize, boolean vertical, Skin skin) {
        super(min, max, stepSize, vertical, skin);
        isVertical = vertical;
    }

    public ControllerSlider(float min, float max, float stepSize, boolean vertical, Skin skin, String styleName) {
        super(min, max, stepSize, vertical, skin, styleName);
        isVertical = vertical;
    }

    public ControllerSlider(float min, float max, float stepSize, boolean vertical, SliderStyle style) {
        super(min, max, stepSize, vertical, style);
        isVertical = vertical;
    }

    @Override
    public boolean onControllerDefaultKeyDown() {
        // nothing should happen - just swallow the event
        return true;
    }

    @Override
    public boolean onControllerScroll(ControllerMenuStage.MoveFocusDirection direction) {
        if (isDisabled())
            return false;
        switch (direction) {
            case north:
                if (!isVertical)
                    return false;
                return setValue(getValue() - getStepSize());
            case south:
                if (!isVertical)
                    return false;
                return setValue(getValue() + getStepSize());
            case east:
                if (isVertical)
                    return false;
                return setValue(getValue() + getStepSize());
            case west:
                if (isVertical)
                    return false;
                return setValue(getValue() - getStepSize());
        }

        return false;
    }

    @Override
    public boolean onControllerDefaultKeyUp() {
        // nothing should happen - just swallow the event
        return true;
    }

}
