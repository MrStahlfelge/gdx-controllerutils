package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * ControllerList is extending {@link List} with scroll behaviour for controllers and key events.
 *
 * Created by Benjamin Schulte on 11.03.2018.
 */

public class ControllerList<T> extends List<T> implements IControllerActable, IControllerScrollable {
    public ControllerList(Skin skin) {
        super(skin);
        setup();
    }

    public ControllerList(Skin skin, String styleName) {
        super(skin, styleName);
        setup();
    }

    public ControllerList(ListStyle style) {
        super(style);
        setup();
    }

    protected void setup() {
        // add a listener that selects an item if none is selected when the list gains focus
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (getSelectedIndex() < 0 && getItems().size > 0)
                    setSelectedIndex(0);
            }
        });
    }

    @Override
    public boolean onControllerDefaultKeyDown() {
        // nothing should happen - just swallow the event
        return true;
    }

    @Override
    public boolean onControllerDefaultKeyUp() {
        // nothing should happen - just swallow the event
        return true;
    }

    @Override
    public boolean onControllerScroll(ControllerMenuStage.MoveFocusDirection direction) {
        if (!isTouchable())
            return false;
        switch (direction) {
            case north:
                if (getSelectedIndex() > 0) {
                    setSelectedIndex(getSelectedIndex() - 1);
                    return true;
                }
                return false;
            case south:
                if (getSelectedIndex() < getItems().size - 1) {
                    setSelectedIndex(getSelectedIndex() + 1);
                    return true;
                }
                return false;
            case east:
            case west:
                return false;
        }

        return false;
    }
}
