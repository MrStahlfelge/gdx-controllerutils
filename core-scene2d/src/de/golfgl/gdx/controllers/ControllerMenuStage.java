package de.golfgl.gdx.controllers;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * A stage with support for button or key control
 * <p>
 * See https://github.com/MrStahlfelge/gdx-controllerutils for more information
 * <p>
 * Created by Benjamin Schulte on 04.11.2017.
 */

public class ControllerMenuStage extends Stage {
    private static final float INITIAL_DIRECTION_EMPH_FACTOR = 3.1f;
    private final Vector2 controllerTempCoords = new Vector2();
    private Array<Actor> focusableActors = new Array<>();
    private boolean isPressed;
    private boolean focusOnTouchdown = true;
    private Actor focusedActor;
    private Actor escapeActor;
    private float directionEmphFactor = INITIAL_DIRECTION_EMPH_FACTOR;

    public ControllerMenuStage(Viewport viewport) {
        super(viewport);
    }

    public ControllerMenuStage(Viewport viewport, Batch batch) {
        super(viewport, batch);
    }

    public boolean isFocusOnTouchdown() {
        return focusOnTouchdown;
    }

    /**
     * @param focusOnTouchdown activate if a click or tap on a focusable actor should set the controller focus to this
     *                         actor. Default is true
     */
    public void setFocusOnTouchdown(boolean focusOnTouchdown) {
        this.focusOnTouchdown = focusOnTouchdown;
    }

    public Actor getEscapeActor() {
        return escapeActor;
    }

    /**
     * @param escapeActor the actor that should receive a touch event when escape key is pressed
     */
    public void setEscapeActor(Actor escapeActor) {
        this.escapeActor = escapeActor;
    }

    public void addFocusableActor(Actor actor) {
        focusableActors.add(actor);
    }

    public void addFocusableActors(Array<Actor> actors) {
        for (int i = 0; i < actors.size; i++)
            addFocusableActor(actors.get(i));
    }

    public void clearFocusableActors() {
        setFocusedActor(null);
        focusableActors.clear();
    }

    /**
     * garbage collects all actors from focusable list that are not present on the stage
     */
    public void removeFocusableActorsNotOnStage() {
        for (int i = focusableActors.size - 1; i >= 0; i--) {
            if (focusableActors.get(i).getStage() != this)
                focusableActors.removeIndex(i);
        }
    }

    public void removeFocusableActor(Actor actor) {
        focusableActors.removeValue(actor, true);
    }

    public void removeFocusableActors(Array<Actor> actors) {
        for (int i = 0; i < actors.size; i++)
            removeFocusableActor(actors.get(i));
    }

    public Array<Actor> getFocusableActors() {
        return focusableActors;
    }

    /**
     * sets the currently focused actor
     *
     * @param actor
     * @return
     */
    public boolean setFocusedActor(Actor actor) {
        if (focusedActor == actor)
            return true;

        if (actor == null || !isActorFocusable(actor))
            return false;

        Actor oldFocused = focusedActor;
        if (oldFocused != null) {
            focusedActor = null;
            onFocusLost(oldFocused, actor);
        }

        // focusedActor kann durch onFocusLost->touchCancel verändert worden sein, dann nicht neu setzen
        if (focusedActor == null) {
            focusedActor = actor;
            if (focusedActor != null)
                onFocusGained(focusedActor, oldFocused);
        }

        return true;
    }

    public Actor getFocusedActor() {
        return focusedActor;
    }

    /**
     * fired when focus was set, override for your own special actions
     *
     * @param focusedActor the actor that gained focus
     */
    protected void onFocusGained(Actor focusedActor, Actor oldFocused) {
        fireEventOnActor(focusedActor, InputEvent.Type.enter, -1, oldFocused);
        setKeyboardFocus(focusedActor);
        setScrollFocus(focusedActor);
    }

    /**
     * fired when focus was lost, override for your own special actions
     *
     * @param focusedActor the actor that lost focus
     */
    protected void onFocusLost(Actor focusedActor, Actor newFocused) {
        if (isPressed) {
            cancelTouchFocus();
            isPressed = false;
        }

        fireEventOnActor(focusedActor, InputEvent.Type.exit, -1, newFocused);
    }

    /**
     * checks if the given actor is focusable: in the list of focusable actors, visible, touchable, and on the stage
     *
     * @param actor
     * @return true if focusable
     */
    protected boolean isActorFocusable(Actor actor) {
        if (!focusableActors.contains(actor, true))
            return false;

        if (!actor.isVisible())
            return false;

        if (!actor.isTouchable() && !(actor instanceof IControllerActable))
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
            handled = triggerActionOnActor(true, focusedActor);
            isPressed = handled;
        } else if (isEscapeActionKeyCode(keyCode) && escapeActor != null) {
            handled = triggerActionOnActor(true, escapeActor);
            isPressed = handled;
        } else
            handled = false;

        if (!handled)
            handled = super.keyDown(keyCode);

        return handled;
    }

    /**
     * called on focusedActor when default action key is pressed or on escapeActor when escape key is pressed
     *
     * @param actor focusedActor or escapeActor
     * @return true if the event was handled
     */
    protected boolean triggerActionOnActor(boolean keyDown, Actor actor) {
        if (actor instanceof IControllerActable && keyDown)
            return ((IControllerActable) actor).onControllerDefaultKeyDown();
        else if (actor instanceof IControllerActable)
            return ((IControllerActable) actor).onControllerDefaultKeyUp();
        else
            return fireEventOnActor(actor, keyDown ? InputEvent.Type.touchDown : InputEvent.Type.touchUp, 0, null);
    }

    @Override
    public boolean keyUp(int keyCode) {
        boolean handled;
        if (isDefaultActionKeyCode(keyCode)) {
            isPressed = false;
            handled = triggerActionOnActor(false, focusedActor);
        } else if (isEscapeActionKeyCode(keyCode) && escapeActor != null) {
            handled = triggerActionOnActor(false, escapeActor);
            isPressed = handled;
        } else
            handled = false;

        if (!handled)
            handled = super.keyUp(keyCode);

        return handled;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        // wenn der Actor den fokus kriegen kann, dann kriegt er ihn hiermit auch
        if (isFocusOnTouchdown()) {
            screenToStageCoordinates(controllerTempCoords.set(screenX, screenY));
            Actor target = hit(controllerTempCoords.x, controllerTempCoords.y, true);
            if (target != null) {
                if (isActorFocusable(target))
                    setFocusedActor(target);
                else for (Actor actor : getFocusableActors()) {
                    if (target.isDescendantOf(actor))
                        setFocusedActor(actor);
                }
            }
        }

        return super.touchDown(screenX, screenY, pointer, button);
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

    protected boolean fireEventOnActor(Actor actor, InputEvent.Type type, int pointer, Actor related) {
        if (actor == null || !isActorFocusable(actor) || !isActorHittable(actor))
            return false;

        InputEvent event = Pools.obtain(InputEvent.class);
        event.setType(type);
        event.setStage(this);
        event.setRelatedActor(related);
        event.setPointer(pointer);
        event.setButton(pointer);
        event.setStageX(0);
        event.setStageY(0);

        actor.fire(event);

        boolean handled = event.isHandled();
        Pools.free(event);
        return handled;
    }

    /**
     * moves the focus in the given direction, is applicable
     *
     * @param direction
     * @return true if an action was perforemd
     */
    protected boolean moveFocusByDirection(MoveFocusDirection direction) {
        if (focusedActor == null)
            return false;

        Actor nearestInDirection = findNearestFocusableNeighbour(direction);

        // check for scrollable parents
        boolean hasScrolled = checkForScrollable(direction, nearestInDirection);

        if (!hasScrolled && nearestInDirection != null)
            return setFocusedActor(nearestInDirection);
        else
            return hasScrolled;
    }

    private Actor findNearestFocusableNeighbour(MoveFocusDirection direction) {
        Vector2 focusedPosition = focusedActor.localToStageCoordinates(
                new Vector2(direction == MoveFocusDirection.east ? focusedActor.getWidth() :
                        direction == MoveFocusDirection.west ? 0 : focusedActor.getWidth() / 2,
                        direction == MoveFocusDirection.north ? focusedActor.getHeight() :
                                direction == MoveFocusDirection.south ? 0 : focusedActor.getHeight() / 2));

        // in Frage kommende raussuchen
        Actor nearestInDirection = null;
        float distance = Float.MAX_VALUE;

        for (int i = 0; i < focusableActors.size; i++) {
            Actor currentActor = focusableActors.get(i);

            if (currentActor != focusedActor && isActorFocusable(currentActor)
                    && isActorInViewportArea(currentActor)) {
                Vector2 currentActorPos = currentActor.localToStageCoordinates(
                        new Vector2(direction == MoveFocusDirection.west ? currentActor.getWidth() :
                                direction == MoveFocusDirection.east ? 0 : currentActor.getWidth() / 2,
                                direction == MoveFocusDirection.south ? currentActor.getHeight() :
                                        direction == MoveFocusDirection.south ? 0 : currentActor.getHeight() / 2));

                boolean isInDirection = false;

                isInDirection = (direction == MoveFocusDirection.south && currentActorPos.y <= focusedPosition.y)
                        || (direction == MoveFocusDirection.north && currentActorPos.y >= focusedPosition.y)
                        || (direction == MoveFocusDirection.west && currentActorPos.x <= focusedPosition.x)
                        || (direction == MoveFocusDirection.east && currentActorPos.x >= focusedPosition.x);

                if (isInDirection && isActorHittable(currentActor)) {
                    float currentDist = calcNeighbourDistance(direction, focusedPosition, currentActorPos);

                    if (currentDist < distance) {
                        nearestInDirection = currentActor;
                        distance = currentDist;
                    }
                }

            }
        }
        return nearestInDirection;
    }

    /**
     *
     * @param direction
     * @param focusedPosition
     * @param currentActorPos
     * @return
     */
    protected float calcNeighbourDistance(MoveFocusDirection direction, Vector2 focusedPosition, Vector2
            currentActorPos) {
        float horizontalDist = currentActorPos.x - focusedPosition.x;
        float verticalDist = currentActorPos.y - focusedPosition.y;

        // emphasize the direct direction
        if (direction == MoveFocusDirection.south || direction == MoveFocusDirection.north)
            horizontalDist = horizontalDist * directionEmphFactor;
        else
            verticalDist = verticalDist * directionEmphFactor;

        return horizontalDist * horizontalDist + verticalDist * verticalDist;
    }

    public float getDirectionEmphFactor() {
        return directionEmphFactor;
    }

    /**
     * use this to emphasize a direction when navigating by arrows
     * @param directionEmphFactor - the more you give here, the more actors in direct direction are favored
     */
    public void setDirectionEmphFactor(float directionEmphFactor) {
        this.directionEmphFactor = directionEmphFactor;
    }

    /**
     * checks if a IControllerScrollable actor should scroll instead of focusing nearest neighbour
     *
     * @param direction
     * @param nearestInDirection may be null
     * @return true if a scroll was performed
     */
    protected boolean checkForScrollable(MoveFocusDirection direction, Actor nearestInDirection) {
        Actor findScrollable = focusedActor;
        boolean didScroll = false;

        while (!didScroll) {
            if (findScrollable == null)
                return false;

            if (findScrollable instanceof IControllerScrollable) {
                // we found a scrollable... but if the nearest actor in direction is also child of this one, it
                // shouldn't scroll. In this case, we leave the while loop because every parent will also be a parent
                // of the nearest
                if (nearestInDirection != null) {
                    Actor nearestNeighboursParent = nearestInDirection;
                    while (nearestNeighboursParent != null && nearestNeighboursParent != findScrollable)
                        nearestNeighboursParent = nearestNeighboursParent.getParent();

                    if (nearestNeighboursParent == findScrollable)
                        return false;
                }

                // ok - now we try to scroll!
                didScroll = ((IControllerScrollable) findScrollable).onControllerScroll(direction);
            }
            findScrollable = findScrollable.getParent();
        }
        return didScroll;
    }

    @Override
    public void unfocusAll() {
        super.unfocusAll();
        setFocusedActor(null);
    }

    @Override
    public void unfocus(Actor actor) {
        super.unfocus(actor);
        if (actor == focusedActor)
            setFocusedActor(null);
    }

    public enum MoveFocusDirection {west, north, east, south}
}
