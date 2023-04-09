package com.ray3k.unbox2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.Box2dBehaviour;

public class KeyboardBehaviour extends BehaviourAdapter {
    private final Vector2 impulse = new Vector2();

    public KeyboardBehaviour(GameObject gameObject) {
        super(gameObject);
    }

    @Override
    public void fixedUpdate() {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        getGameObject().getBehaviour(Box2dBehaviour.class).getBody().applyLinearImpulse(impulse.x, impulse.y, position.x, position.y, true);
    }

    @Override
    public void update(float delta) {
        float speed = 0;
        float direction = 0;

        if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
            speed = .5f;
        }

        if (Gdx.input.isKeyPressed(Keys.UP)) {
            direction = 90;
        } else if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            direction = 270;
        } else if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            direction = 180;
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            direction = 0;
        }

        impulse.set(speed, 0);
        impulse.rotateDeg(direction);
    }
}
