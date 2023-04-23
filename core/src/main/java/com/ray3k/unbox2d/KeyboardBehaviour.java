package com.ray3k.unbox2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.box2d.Box2dBehaviour;

public class KeyboardBehaviour extends BehaviourAdapter {
    private final Vector2 velocity = new Vector2();

    public KeyboardBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void fixedUpdate() {
        getGameObject().getBehaviour(Box2dBehaviour.class).getBody().setLinearVelocity(velocity);
    }

    @Override
    public void update(float delta) {
        float maxSpeed = 5f;
        velocity.set(0, 0);

        if (Gdx.input.isKeyPressed(Keys.UP)) velocity.y += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.DOWN)) velocity.y -= maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) velocity.x += maxSpeed;
        if (Gdx.input.isKeyPressed(Keys.LEFT)) velocity.x -= maxSpeed;
    }
}
