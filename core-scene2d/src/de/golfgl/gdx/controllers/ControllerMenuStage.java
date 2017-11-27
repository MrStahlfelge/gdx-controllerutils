package de.golfgl.gdx.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMenuStage extends Stage {
    private Array<Actor> focussableActors = new Array<>();
    private boolean isPressed;
    private Actor focussedActor;
    private Actor escapeActor;

    public ControllerMenuStage(Viewport viewport) {
        super(viewport);
    }

    public Actor getEscapeActor() {
        return escapeActor;
    }

    public void setEscapeActor(Actor escapeActor) {
        this.escapeActor = escapeActor;
    }

    public void addFocussableActor(Actor actor) {
        focussableActors.add(actor);
    }

    public void clearFocussableActors() {
        setFocussedActor(null);
        focussableActors.clear();
    }

    public void removeFocussableActor(Actor actor) {
        focussableActors.removeValue(actor, true);
    }

    public Array<Actor> getFocussableActors() {
        return focussableActors;
    }

    public boolean setFocussedActor(Actor actor) {
        if (focussedActor == actor)
            return true;

        if (actor != null && !isActorFocussable(actor))
            return false;

        if (focussedActor != null) {
            Actor oldFocussed = focussedActor;
            focussedActor = null;
            onFocusLost(oldFocussed);
        }

        // focussedActor kann durch onFocusLost->touchCancel verändert worden sein, dann nicht neu setzen
        if (focussedActor == null) {
            focussedActor = actor;
            if (focussedActor != null)
                onFocusGained(focussedActor);
        }

        return true;
    }

    public Actor getFocussedActor() {
        return focussedActor;
    }

    /**
     * fired when focus was set, override for your own special actions
     *
     * @param focussedActor the actor that gained focus
     */
    protected void onFocusGained(Actor focussedActor) {
        fireEventOnActor(focussedActor, InputEvent.Type.enter);
        setKeyboardFocus(focussedActor);
        setScrollFocus(focussedActor);
    }

    /**
     * fired when focus was lost, override for your own special actions
     *
     * @param focussedActor the actor that lost focus
     */
    protected void onFocusLost(Actor focussedActor) {
        if (isPressed) {
            cancelTouchFocus();
            isPressed = false;
        }

        fireEventOnActor(focussedActor, InputEvent.Type.exit);
    }

    protected boolean isActorFocussable(Actor actor) {
        if (!focussableActors.contains(actor, true))
            return false;

        if (!actor.isVisible())
            return false;

        if (!actor.isTouchable())
            return false;

        if (actor.getStage() != this)
            return false;

        return true;
    }

    protected boolean isActorHittable(Actor actor) {
        // Prüfung auf hit
        Vector2 center = actor.localToStageCoordinates(new Vector2(actor.getWidth() / 2, actor.getHeight() / 2));
        Actor hitActor = hit(center.x, center.y, true);
        return hitActor != null && (hitActor.isDescendantOf(actor));
    }

    protected boolean isActorInViewportArea(Actor actor) {
        Vector2 leftBottom = actor.localToStageCoordinates(new Vector2(0, 0));
        Vector2 rightTop = actor.localToStageCoordinates(new Vector2(actor.getWidth(), actor.getHeight()));

        return !(leftBottom.x > getWidth() || leftBottom.y > getHeight() || rightTop.x < 0 || rightTop.y < 0);
    }

    @Override
    public boolean keyDown(int keyCode) {
        boolean handled;

        if (isGoDownKeyCode(keyCode))
            handled = moveFocusByDirection(MoveFocusDirection.south);
        else if (isGoUpKeyCode(keyCode))
            handled = moveFocusByDirection(MoveFocusDirection.north);
        else if (isGoLeftKeyCode(keyCode))
            handled = moveFocusByDirection(MoveFocusDirection.west);
        else if (isGoRightKeyCode(keyCode))
            handled = moveFocusByDirection(MoveFocusDirection.east);
        else if (isDefaultActionKeyCode(keyCode)) {
            handled = fireEventOnActor(focussedActor, InputEvent.Type.touchDown);
            isPressed = handled;
        } else if (isEscapeActionKeyCode(keyCode) && escapeActor != null) {
            handled = fireEventOnActor(escapeActor, InputEvent.Type.touchDown);
            isPressed = handled;
        } else
            handled = false;

        if (!handled)
            handled = super.keyDown(keyCode);

        return handled;
    }

    @Override
    public boolean keyUp(int keyCode) {
        boolean handled;
        if (isDefaultActionKeyCode(keyCode)) {
            isPressed = false;
            handled = fireEventOnActor(focussedActor, InputEvent.Type.touchUp);
        } else if (isEscapeActionKeyCode(keyCode) && escapeActor != null) {
            handled = fireEventOnActor(escapeActor, InputEvent.Type.touchUp);
            isPressed = handled;
        } else
            handled = false;

        if (!handled)
            handled = super.keyUp(keyCode);

        return handled;
    }

    /**
     * returns true if the given keyCode is a DefaultAction key/button. You can override this.
     */

    public boolean isDefaultActionKeyCode(int keyCode) {
        switch (keyCode) {
            case Input.Keys.CENTER:
            case Input.Keys.ENTER:
                return true;
            default:
                return false;
        }
    }

    /**
     * returns true if the given keyCode is a EscapeAction key/button. You can override this.
     */
    public boolean isEscapeActionKeyCode(int keyCode) {
        switch (keyCode) {
            case Input.Keys.BACK:
            case Input.Keys.BACKSPACE:
            case Input.Keys.ESCAPE:
                return true;
            default:
                return false;
        }
    }

    /**
     * returns true if the given keyCode is a GoRight key/button. You can override this.
     */
    public boolean isGoRightKeyCode(int keyCode) {
        return (keyCode == Input.Keys.RIGHT);
    }

    /**
     * returns true if the given keyCode is a GoLeft key/button. You can override this.
     */
    public boolean isGoLeftKeyCode(int keyCode) {
        return (keyCode == Input.Keys.LEFT);
    }

    /**
     * returns true if the given keyCode is a GoUp key/button. You can override this.
     */
    public boolean isGoUpKeyCode(int keyCode) {
        return (keyCode == Input.Keys.UP);
    }

    /**
     * returns true if the given keyCode is a GoDown key/button. You can override this.
     */
    public boolean isGoDownKeyCode(int keyCode) {
        return (keyCode == Input.Keys.DOWN);
    }

    protected boolean fireEventOnActor(Actor actor, InputEvent.Type type) {
        if (actor == null || !isActorFocussable(actor) || !isActorHittable(actor))
            return false;

        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(type);
        event.setStage(this);
        event.setRelatedActor(actor);
        event.setPointer(1);
        event.setButton(-1);

        actor.fire(event);

        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    private boolean moveFocusByDirection(MoveFocusDirection direction) {
        if (focussedActor == null)
            return false;

        Vector2 focussedPosition = focussedActor.localToStageCoordinates(
                new Vector2(direction == MoveFocusDirection.east ? focussedActor.getWidth() :
                        direction == MoveFocusDirection.west ? 0 : focussedActor.getWidth() / 2,
                        direction == MoveFocusDirection.north ? focussedActor.getHeight() :
                                direction == MoveFocusDirection.south ? 0 : focussedActor.getHeight() / 2));

        // in Frage kommende raussuchen
        Actor nearestInDirection = null;
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < focussableActors.size; i++) {
            Actor currentActor = focussableActors.get(i);

            if (currentActor != focussedActor && isActorFocussable(currentActor)
                    && isActorInViewportArea(currentActor)) {
                Vector2 currentActorPos = currentActor.localToStageCoordinates(
                        new Vector2(direction == MoveFocusDirection.west ? currentActor.getWidth() :
                                direction == MoveFocusDirection.east ? 0 : currentActor.getWidth() / 2,
                                direction == MoveFocusDirection.south ? currentActor.getHeight() :
                                        direction == MoveFocusDirection.south ? 0 : currentActor.getHeight() / 2));

                boolean isInDirection = false;

                isInDirection = (direction == MoveFocusDirection.south && currentActorPos.y < focussedPosition.y)
                        || (direction == MoveFocusDirection.north && currentActorPos.y > focussedPosition.y)
                        || (direction == MoveFocusDirection.west && currentActorPos.x < focussedPosition.x)
                        || (direction == MoveFocusDirection.east && currentActorPos.x > focussedPosition.x);

                if (isInDirection && isActorHittable(currentActor)) {
                    float currentDist = currentActorPos.dst2(focussedPosition);

                    if (currentDist < distance) {
                        nearestInDirection = currentActor;
                        distance = currentDist;
                    }
                }

            }
        }

        if (nearestInDirection != null)
            setFocussedActor(nearestInDirection);

        return (nearestInDirection != null);
    }

    @Override
    public void unfocusAll() {
        super.unfocusAll();
        setFocussedActor(null);
    }

    @Override
    public void unfocus(Actor actor) {
        super.unfocus(actor);
        if (actor == focussedActor)
            setFocussedActor(null);
    }

    public enum MoveFocusDirection {west, north, east, south}
}
