package com.ray3k.unbox2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import dev.lyze.gdxUnBox2d.GameObject;
import dev.lyze.gdxUnBox2d.behaviours.BehaviourAdapter;
import dev.lyze.gdxUnBox2d.behaviours.box2d.Box2dBehaviour;

public class SpriteImprovedBehaviour extends BehaviourAdapter {
    private float offsetX;
    private float offsetY;
    private Sprite sprite;
    private final Vector2 position = new Vector2();

    public SpriteImprovedBehaviour(GameObject gameObject, float offsetX, float offsetY, Sprite sprite, float renderOrder) {
        super(gameObject);
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.sprite = sprite;
        setRenderOrder(renderOrder);
    }

    @Override
    public void fixedUpdate() {
        if (getGameObject().getBehaviour(Box2dBehaviour.class) != null)
            position.set(getGameObject().getBehaviour(Box2dBehaviour.class).getBody().getPosition());
        else position.setZero();

        position.add(offsetX, offsetY);
        this.sprite.setPosition(position.x, position.y);
    }

    @Override
    public void render(Batch batch) {
        sprite.draw(batch);
    }
}
