package com.ray3k.unbox2d;

import com.badlogic.gdx.math.Vector2;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.box2d.Box2dBehaviour;

public class MoveBehaviour extends BehaviourAdapter {
    private final boolean moveToRight;

    public MoveBehaviour(boolean moveToRight, GameObject gameObject) {
        super(gameObject);

        this.moveToRight = moveToRight;
    }

    @Override
    public void fixedUpdate() {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        getGameObject().getBehaviour(Box2dBehaviour.class).getBody().applyLinearImpulse(0.01f * (moveToRight ? -1 : 1), 0, position.x, position.y, true);
    }

    @Override
    public void update(float delta) {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        System.out.println("Position: " + position);
    }
}
