package com.ray3k.unbox2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.Box2dBehaviour;

public class SpriteBehavior extends BehaviourAdapter {
    private float offsetX;
    private float offsetY;
    private Sprite sprite;

    public SpriteBehavior(GameObject gameObject, float offsetX, float offsetY, Sprite sprite) {
        super(gameObject);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sprite = sprite;
    }

    @Override
    public void fixedUpdate() {
        Vector2 position = getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition();
        position.add(offsetX, offsetY);
        this.sprite.setPosition(position.x, position.y);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
