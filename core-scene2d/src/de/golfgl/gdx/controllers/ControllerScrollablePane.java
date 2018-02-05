package de.golfgl.gdx.controllers;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Normal {@link ScrollPane} implementing {@link IControllerScrollable}
 * <p>
 * Created by Benjamin Schulte on 05.02.2018.
 */

public class ControllerScrollablePane extends ScrollPane implements IControllerScrollable {
    public ControllerScrollablePane(Actor widget) {
        super(widget);
    }

    public ControllerScrollablePane(Actor widget, Skin skin) {
        super(widget, skin);
    }

    public ControllerScrollablePane(Actor widget, Skin skin, String styleName) {
        super(widget, skin, styleName);
    }

    public ControllerScrollablePane(Actor widget, ScrollPaneStyle style) {
        super(widget, style);
    }

    @Override
    public boolean scroll(ControllerMenuStage.MoveFocusDirection direction) {
        switch (direction) {
            case south:
                if (!isScrollY() || getScrollY() >= getMaxY())
                    return false;
                setScrollY(getScrollY() + getMouseWheelY() * getScrollAmount());
                return true;
            case north:
                if (!isScrollY() || getScrollY() <= 0)
                    return false;
                setScrollY(getScrollY() - getMouseWheelY() * getScrollAmount());
                return true;
            case west:
                if (!isScrollX() || getScrollX() <= 0)
                    return false;
                setScrollY(getScrollX() - getMouseWheelX() * getScrollAmount());
                return true;
            case east:
                if (!isScrollX() || getScrollX() >= getMaxX())
                    return false;
                setScrollY(getScrollX() + getMouseWheelX() * getScrollAmount());
                return true;
        }

        return false;
    }

    /**
     * override this to change the controller scroll speed
     *
     * @return
     */
    protected float getScrollAmount() {
        return 1;
    }
}
